package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.PontoEletronicoResponse;
import com.empresa.mvcpontoeletronico.dtos.RegistrarPontoRequest;
import com.empresa.mvcpontoeletronico.dtos.RelatorioHorasResponse;
import com.empresa.mvcpontoeletronico.entities.PontoEletronico;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.PontoEletronicoRepository;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service principal para gerenciamento de ponto eletrônico
 * Arquitetura MVC: Camada de Negócio (Service)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PontoEletronicoService {
    
    private final PontoEletronicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfiguracaoEmpresaService configuracaoService;
    private static final int INTERVALO_MINIMO_MINUTOS = 10; // 0 = desabilitado para facilitar testes
    
    /**
     * Registra um novo ponto eletrônico com a nova lógica:
     * - Busca se já existe registro para a data
     * - Se não existe, cria novo com entrada1
     * - Se existe, atualiza próxima coluna disponível
     */
    @Transactional
    public PontoEletronicoResponse registrarPonto(RegistrarPontoRequest request) {
        log.debug("Registrando ponto para usuário: {}", request.getUsuarioId());
        
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Define data/hora se não informada
        LocalDateTime dataHora = request.getDataHora() != null ? 
            request.getDataHora() : LocalDateTime.now();
            
        LocalDate data = dataHora.toLocalDate();
        log.debug("Data/hora do registro: {} (data: {})", dataHora, data);
        
        // Valida horário baseado nas configurações da empresa
        validarHorarioPermitido(dataHora);
        
        // Valida se não é muito antigo (máximo 7 dias)
        if (dataHora.isBefore(LocalDateTime.now().minusDays(7))) {
            log.warn("Tentativa de registrar ponto com data muito antiga: {}", dataHora);
            throw new IllegalArgumentException("Não é possível registrar pontos com mais de 7 dias");
        }
        
        // Busca ou cria o registro do dia
        Optional<PontoEletronico> registroExistenteOpt = pontoRepository.findByUsuarioIdAndData(request.getUsuarioId(), data);
        
        PontoEletronico pontoRegistro;
        String proximaColuna;
        
        if (registroExistenteOpt.isPresent()) {
            // Atualiza registro existente
            pontoRegistro = registroExistenteOpt.get();
            proximaColuna = pontoRegistro.getProximaColunaDisponivel();
            
            if (proximaColuna == null) {
                log.warn("Tentativa de registrar ponto quando todas as 6 colunas já estão preenchidas para usuário {} na data {}", 
                    request.getUsuarioId(), data);
                throw new IllegalArgumentException("Limite máximo de 6 registros por dia já atingido (3 entradas + 3 saídas)");
            }
            
            // Valida intervalo mínimo se habilitado
            if (INTERVALO_MINIMO_MINUTOS > 0) {
                LocalDateTime ultimoRegistro = getUltimoRegistroDoUsuario(pontoRegistro);
                if (ultimoRegistro != null) {
                    long minutosDecorridos = java.time.Duration.between(ultimoRegistro, dataHora).toMinutes();
                    if (minutosDecorridos < INTERVALO_MINIMO_MINUTOS) {
                        log.warn("Intervalo insuficiente entre registros. Último: {}, Atual: {}, Decorridos: {} min", 
                            ultimoRegistro, dataHora, minutosDecorridos);
                        throw new IllegalArgumentException("Deve haver um intervalo mínimo de " + INTERVALO_MINIMO_MINUTOS + " minuto(s) entre registros");
                    }
                }
            }
            
            // Registra o ponto na próxima coluna
            boolean sucesso = pontoRegistro.registrarPonto(dataHora);
            if (!sucesso) {
                throw new IllegalStateException("Erro interno ao registrar ponto");
            }
            
            log.info("Ponto atualizado na coluna {} - ID: {}, Usuário: {}", 
                proximaColuna, pontoRegistro.getId(), usuario.getNome());
            
        } else {
            // Cria novo registro
            pontoRegistro = PontoEletronico.builder()
                .usuario(usuario)
                .data(data)  // Define a data de referência
                .entrada1(dataHora) // Primeiro registro sempre é entrada1
                .localizacao(request.getLocalizacao()) 
                .observacao(request.getObservacao())
                .build();
            
            proximaColuna = "entrada1";
            log.info("Novo registro criado - Usuário: {}, Data: {}", 
                usuario.getNome(), data);
        }
        
        // Atualiza localização e observação se fornecidas
        if (request.getLocalizacao() != null) {
            pontoRegistro.setLocalizacao(request.getLocalizacao());
        }
        if (request.getObservacao() != null) {
            pontoRegistro.setObservacao(request.getObservacao());
        }
        
        PontoEletronico pontoSalvo = pontoRepository.save(pontoRegistro);
        log.info("Ponto registrado com sucesso na coluna {} - ID: {}", 
                 proximaColuna, pontoSalvo.getId());
        
        return mapToResponse(pontoSalvo);
    }
    
    /**
     * Método auxiliar para encontrar o último registro válido no objeto PontoEletronico
     */
    private LocalDateTime getUltimoRegistroDoUsuario(PontoEletronico ponto) {
        // Retorna o último timestamp não-nulo das colunas de entrada/saída
        if (ponto.getSaida3() != null) return ponto.getSaida3();
        if (ponto.getEntrada3() != null) return ponto.getEntrada3();
        if (ponto.getSaida2() != null) return ponto.getSaida2();
        if (ponto.getEntrada2() != null) return ponto.getEntrada2();
        if (ponto.getSaida1() != null) return ponto.getSaida1();
        if (ponto.getEntrada1() != null) return ponto.getEntrada1();
        return null;
    }
    
    /**
     * Consulta pontos por usuário e data
     * Agora retorna os registros de cada coluna do registro único do dia
     */
    public List<PontoEletronicoResponse> consultarPontosPorData(UUID usuarioId, LocalDate data) {
        log.debug("Consultando pontos - Usuário: {}, Data: {}", usuarioId, data);
        
        Optional<PontoEletronico> registroOpt = pontoRepository.findByUsuarioIdAndData(usuarioId, data);
        if (registroOpt.isEmpty()) {
            return Collections.emptyList();
        }
        
        return mapToResponseList(registroOpt.get());
    }
    
    /**
     * Consulta pontos por usuário e período
     */
    public List<PontoEletronicoResponse> consultarPontosPorPeriodo(UUID usuarioId, 
                                                                  LocalDate dataInicio, 
                                                                  LocalDate dataFim) {
        log.debug("Consultando pontos - Usuário: {}, Período: {} a {}", usuarioId, dataInicio, dataFim);
        
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data início deve ser anterior à data fim");
        }
        
        List<PontoEletronico> registros = pontoRepository.findByUsuarioIdAndPeriodo(usuarioId, dataInicio, dataFim);
        
        // Converte cada registro (que pode ter múltiplas colunas) em múltiplas respostas
        List<PontoEletronicoResponse> todosPontos = new ArrayList<>();
        for (PontoEletronico registro : registros) {
            todosPontos.addAll(mapToResponseList(registro));
        }
        
        // Ordena por data/hora
        todosPontos.sort(Comparator.comparing(PontoEletronicoResponse::getDataHora));
        return todosPontos;
    }
    
    /**
     * Gera relatório de horas trabalhadas
     */
    public RelatorioHorasResponse gerarRelatorioHoras(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Gerando relatório de horas - Usuário: {}, Período: {} a {}", usuarioId, dataInicio, dataFim);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        List<PontoEletronico> pontos = pontoRepository.findByUsuarioIdAndPeriodo(usuarioId, dataInicio, dataFim);
        
        // Agrupa registros por data (cada registro já representa um dia)
        Map<LocalDate, PontoEletronico> registrosPorData = pontos.stream()
            .collect(Collectors.toMap(
                p -> p.getCreatedAt().toLocalDate(), 
                Function.identity()
            ));
        
        List<RelatorioHorasResponse.RegistroDiario> registrosDiarios = new ArrayList<>();
        long totalMinutosPeriodo = 0;
        
        // Processa cada dia no período
        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            PontoEletronico registroDia = registrosPorData.get(dataAtual);
            long minutosTrabalhadosDia = registroDia != null ? calcularHorasTrabalhadasMinutos(registroDia) : 0;
            totalMinutosPeriodo += minutosTrabalhadosDia;
            
            List<PontoEletronicoResponse> pontosResponse = registroDia != null ? 
                mapToResponseList(registroDia) : Collections.emptyList();
            
            RelatorioHorasResponse.RegistroDiario registro = RelatorioHorasResponse.RegistroDiario.builder()
                .data(dataAtual)
                .pontos(pontosResponse)
                .horasTrabalhadasMinutos(minutosTrabalhadosDia)
                .horasTrabalhadasFormatado(formatarMinutos(minutosTrabalhadosDia))
                .diaCompleto(registroDia != null && registroDia.isCompleto())
                .build();
            
            registrosDiarios.add(registro);
            dataAtual = dataAtual.plusDays(1);
        }
        
        return RelatorioHorasResponse.builder()
            .usuarioId(usuarioId)
            .nomeUsuario(usuario.getNome())
            .dataInicio(dataInicio)
            .dataFim(dataFim)
            .totalHorasMinutos(totalMinutosPeriodo)
            .totalHorasFormatado(formatarMinutos(totalMinutosPeriodo))
            .registrosDiarios(registrosDiarios)
            .build();
    }
    
    /**
     * Calcula horas trabalhadas em um dia (em minutos) - agora usando as colunas entrada/saída
     */
    private long calcularHorasTrabalhadasMinutos(PontoEletronico registro) {
        long totalMinutos = 0;
        
        // Calcula durações entre pares entrada/saída
        if (registro.getEntrada1() != null && registro.getSaida1() != null) {
            Duration duracao = Duration.between(registro.getEntrada1(), registro.getSaida1());
            totalMinutos += duracao.toMinutes();
        }
        
        if (registro.getEntrada2() != null && registro.getSaida2() != null) {
            Duration duracao = Duration.between(registro.getEntrada2(), registro.getSaida2());
            totalMinutos += duracao.toMinutes();
        }
        
        if (registro.getEntrada3() != null && registro.getSaida3() != null) {
            Duration duracao = Duration.between(registro.getEntrada3(), registro.getSaida3());
            totalMinutos += duracao.toMinutes();
        }
        
        return totalMinutos;
    }
    
    /**
     * Formata minutos em "Xh Ymin"
     */
    private String formatarMinutos(long totalMinutos) {
        long horas = totalMinutos / 60;
        long minutos = totalMinutos % 60;
        
        if (horas == 0) return minutos + "min";
        if (minutos == 0) return horas + "h";
        return horas + "h " + minutos + "min";
    }
    
    /**
     * Converte um registro PontoEletronico em uma lista de responses (uma para cada coluna preenchida)
     */
    private List<PontoEletronicoResponse> mapToResponseList(PontoEletronico registro) {
        List<PontoEletronicoResponse> responses = new ArrayList<>();
        
        if (registro.getEntrada1() != null) {
            responses.add(createResponse(registro, registro.getEntrada1(), "ENTRADA_1", "Entrada 1"));
        }
        if (registro.getSaida1() != null) {
            responses.add(createResponse(registro, registro.getSaida1(), "SAIDA_1", "Saída 1"));
        }
        if (registro.getEntrada2() != null) {
            responses.add(createResponse(registro, registro.getEntrada2(), "ENTRADA_2", "Entrada 2"));
        }
        if (registro.getSaida2() != null) {
            responses.add(createResponse(registro, registro.getSaida2(), "SAIDA_2", "Saída 2"));
        }
        if (registro.getEntrada3() != null) {
            responses.add(createResponse(registro, registro.getEntrada3(), "ENTRADA_3", "Entrada 3"));
        }
        if (registro.getSaida3() != null) {
            responses.add(createResponse(registro, registro.getSaida3(), "SAIDA_3", "Saída 3"));
        }
        
        return responses;
    }
    
    /**
     * Cria um PontoEletronicoResponse para um registro específico
     */
    private PontoEletronicoResponse createResponse(PontoEletronico registro, LocalDateTime dataHora, 
                                                  String tipoCodigo, String tipoDescricao) {
        return PontoEletronicoResponse.builder()
            .id(registro.getId())
            .usuarioId(registro.getUsuario().getId())
            .nomeUsuario(registro.getUsuario().getNome())
            .dataHora(dataHora)
            .tipoPonto(tipoCodigo) // Agora é uma string ao invés de enum
            .tipoPontoDescricao(tipoDescricao)
            .localizacao(registro.getLocalizacao())
            .observacao(registro.getObservacao())
            .createdAt(registro.getCreatedAt())
            .build();
    }
    
    /**
     * Converte PontoEletronico para Response DTO (método legado - agora retorna apenas a primeira entrada disponível)
     */
    private PontoEletronicoResponse mapToResponse(PontoEletronico registro) {
        // Para compatibilidade, retorna o primeiro registro não-nulo
        List<PontoEletronicoResponse> responses = mapToResponseList(registro);
        return responses.isEmpty() ? null : responses.get(0);
    }
    
    /**
     * Remove um registro de ponto
     */
    @Transactional
    public void removerPonto(UUID pontoId) {
        log.debug("Removendo ponto ID: {}", pontoId);
        
        if (!pontoRepository.existsById(pontoId)) {
            throw new IllegalArgumentException("Registro de ponto não encontrado");
        }
        
        pontoRepository.deleteById(pontoId);
        log.info("Ponto removido com sucesso - ID: {}", pontoId);
    }
    /**
     * Valida se o horário atual está dentro do intervalo permitido para registrar ponto
     * baseado nas configurações da empresa
     */
    private void validarHorarioPermitido(LocalDateTime dataHora) {
        try {
            // Busca as configurações da empresa
            var configuracoes = configuracaoService.obterConfiguracoes();
            
            LocalTime horarioCheckin = configuracoes.getHorarioCheckin();
            LocalTime horarioCheckout = configuracoes.getHorarioCheckout();
            LocalTime horarioAtual = dataHora.toLocalTime();
            
            log.debug("Validando horário - Checkin: {}, Checkout: {}, Atual: {}", 
                    horarioCheckin, horarioCheckout, horarioAtual);
            
            // Verifica se o horário atual está dentro do intervalo permitido
            if (horarioAtual.isBefore(horarioCheckin) || horarioAtual.isAfter(horarioCheckout)) {
                log.warn("Tentativa de registrar ponto fora do horário permitido. " +
                        "Horário atual: {}, Permitido: {} às {}", 
                        horarioAtual, horarioCheckin, horarioCheckout);
                
                throw new IllegalArgumentException(
                    String.format("Registros de ponto só são permitidos entre %s e %s. Horário atual: %s", 
                            horarioCheckin, horarioCheckout, horarioAtual));
            }
            
            log.debug("Horário validado com sucesso - dentro do intervalo permitido");
            
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                // Re-propaga erros de validação de horário
                throw e;
            }
            
            // Log do erro mas permite o registro se não conseguir carregar configurações
            // (para não bloquear o sistema em caso de problemas na configuração)
            log.warn("Erro ao validar horário baseado nas configurações da empresa. " +
                    "Permitindo registro de ponto. Erro: {}", e.getMessage());
        }
    }    
    /**
     * Atualiza pontos de uma data específica (para resolução de solicitações)
     */
    @Transactional  
    public void atualizarPontosPorData(UUID usuarioId, LocalDate data, Map<String, Object> dadosAtualizacao) {
        log.debug("Atualizando pontos para usuário {} na data {}", usuarioId, data);
        
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Valida observação obrigatória
        String observacao = (String) dadosAtualizacao.get("observacao");
        if (observacao == null || observacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Observação é obrigatória");
        }
        
        // Busca ou cria o registro da data
        Optional<PontoEletronico> registroOpt = pontoRepository.findByUsuarioIdAndData(usuarioId, data);
        
        PontoEletronico registro;
        boolean isNovoRegistro = false;
        
        if (registroOpt.isPresent()) {
            registro = registroOpt.get();
            log.debug("Atualizando registro existente para a data {}", data);
        } else {
            // Cria novo registro se não existir
            registro = PontoEletronico.builder()
                .usuario(usuario)
                .data(data)  // Define a data de referência
                .build();
            isNovoRegistro = true;
            log.debug("Criando novo registro para a data {}", data);
        }
        
        // Atualiza os horários se fornecidos nos dados de atualização
        @SuppressWarnings("unchecked")
        Map<String, String> pontos = (Map<String, String>) dadosAtualizacao.get("pontos");
        
        if (pontos != null) {
            if (pontos.containsKey("entrada1") && !pontos.get("entrada1").isEmpty()) {
                registro.setEntrada1(parseHorario(data, pontos.get("entrada1")));
            }
            if (pontos.containsKey("saida1") && !pontos.get("saida1").isEmpty()) {
                registro.setSaida1(parseHorario(data, pontos.get("saida1")));
            }
            if (pontos.containsKey("entrada2") && !pontos.get("entrada2").isEmpty()) {
                registro.setEntrada2(parseHorario(data, pontos.get("entrada2")));
            }
            if (pontos.containsKey("saida2") && !pontos.get("saida2").isEmpty()) {
                registro.setSaida2(parseHorario(data, pontos.get("saida2")));
            }
            if (pontos.containsKey("entrada3") && !pontos.get("entrada3").isEmpty()) {
                registro.setEntrada3(parseHorario(data, pontos.get("entrada3")));
            }
            if (pontos.containsKey("saida3") && !pontos.get("saida3").isEmpty()) {
                registro.setSaida3(parseHorario(data, pontos.get("saida3")));
            }
        }
        
        // Atualiza observação
        registro.setObservacao(observacao.trim());
        
        pontoRepository.save(registro);
        
        if (isNovoRegistro) {
            log.info("Novo registro de pontos criado para usuário {} na data {} via resolução de solicitação", usuarioId, data);
        } else {
            log.info("Pontos atualizados para usuário {} na data {} via resolução de solicitação", usuarioId, data);
        }
    }
    
    /**
     * Converte string de horário (HH:mm) para LocalDateTime na data especificada
     */
    private LocalDateTime parseHorario(LocalDate data, String horario) {
        if (horario == null || horario.trim().isEmpty()) {
            return null;
        }
        
        String[] partes = horario.trim().split(":");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato de horário inválido: " + horario);
        }
        
        try {
            int hora = Integer.parseInt(partes[0]);
            int minuto = Integer.parseInt(partes[1]);
            return data.atTime(hora, minuto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de horário inválido: " + horario);
        }
    }
}