package com.empresa.arquiteturalimpa.application.ponto;

import com.empresa.arquiteturalimpa.application.configuracao.ConfiguracaoEmpresaService;
import com.empresa.arquiteturalimpa.application.ponto.dto.PontoEletronicoResponse;
import com.empresa.arquiteturalimpa.application.ponto.dto.RegistrarPontoRequest;
import com.empresa.arquiteturalimpa.application.ponto.dto.RelatorioHorasResponse;
import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import com.empresa.arquiteturalimpa.domain.exception.EntityNotFoundException;
import com.empresa.arquiteturalimpa.domain.model.PontoEletronico;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.PontoEletronicoRepository;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Casos de uso relacionados ao registro de ponto eletrônico.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PontoEletronicoService {

    private final PontoEletronicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfiguracaoEmpresaService configuracaoService;

    @Transactional
    public PontoEletronicoResponse registrarPonto(RegistrarPontoRequest request) {
        log.debug("Registrando ponto para usuário: {}", request.getUsuarioId());

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        LocalDateTime dataHora = request.getDataHora() != null ? request.getDataHora() : LocalDateTime.now();
        LocalDate data = dataHora.toLocalDate();

        validarHorarioPermitido(dataHora);

        if (dataHora.isBefore(LocalDateTime.now().minusDays(7))) {
            throw new BusinessRuleException("Não é possível registrar pontos com mais de 7 dias");
        }

        Optional<PontoEletronico> registroExistenteOpt = pontoRepository.findByUsuarioIdAndData(request.getUsuarioId(), data);

        PontoEletronico pontoRegistro;

        if (registroExistenteOpt.isPresent()) {
            pontoRegistro = registroExistenteOpt.get();
            String proximaColuna = pontoRegistro.getProximaColunaDisponivel();

            if (proximaColuna == null) {
                throw new BusinessRuleException("Limite máximo de 6 registros por dia já atingido (3 entradas + 3 saídas)");
            }

            LocalDateTime ultimoRegistro = pontoRegistro.getUltimoRegistro();
            if (ultimoRegistro != null && dataHora.isBefore(ultimoRegistro)) {
                throw new BusinessRuleException("O horário do ponto deve ser posterior ao último registro (" +
                        ultimoRegistro.toLocalTime() + ")");
            }

            int intervaloMinimoMinutos = configuracaoService.obterConfiguracoes().getIntervaloMinimoMinutos();
            if (intervaloMinimoMinutos > 0 && ultimoRegistro != null) {
                long minutosDecorridos = Duration.between(ultimoRegistro, dataHora).toMinutes();
                if (minutosDecorridos < intervaloMinimoMinutos) {
                    throw new BusinessRuleException("Deve haver um intervalo mínimo de " + intervaloMinimoMinutos + " minuto(s) entre registros");
                }
            }

            boolean sucesso = pontoRegistro.registrarPonto(dataHora);
            if (!sucesso) {
                throw new IllegalStateException("Erro interno ao registrar ponto");
            }
        } else {
            pontoRegistro = PontoEletronico.builder()
                    .usuario(usuario)
                    .data(data)
                    .entrada1(dataHora)
                    .localizacao(request.getLocalizacao())
                    .observacao(request.getObservacao())
                    .build();
        }

        if (request.getLocalizacao() != null) {
            pontoRegistro.setLocalizacao(request.getLocalizacao());
        }
        if (request.getObservacao() != null) {
            pontoRegistro.setObservacao(request.getObservacao());
        }

        PontoEletronico pontoSalvo = pontoRepository.save(pontoRegistro);
        log.info("Ponto registrado com sucesso - ID: {}", pontoSalvo.getId());

        List<PontoEletronicoResponse> responses = mapToResponseList(pontoSalvo);
        return responses.isEmpty() ? null : responses.get(responses.size() - 1);
    }

    public List<PontoEletronicoResponse> consultarPontosPorData(UUID usuarioId, LocalDate data) {
        Optional<PontoEletronico> registroOpt = pontoRepository.findByUsuarioIdAndData(usuarioId, data);
        return registroOpt.map(this::mapToResponseList).orElse(Collections.emptyList());
    }

    public List<PontoEletronicoResponse> consultarPontosPorPeriodo(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessRuleException("Data início deve ser anterior à data fim");
        }

        List<PontoEletronico> registros = pontoRepository.findByUsuarioIdAndPeriodo(usuarioId, dataInicio, dataFim);

        List<PontoEletronicoResponse> todosPontos = new ArrayList<>();
        for (PontoEletronico registro : registros) {
            todosPontos.addAll(mapToResponseList(registro));
        }

        todosPontos.sort(Comparator.comparing(PontoEletronicoResponse::getDataHora));
        return todosPontos;
    }

    public RelatorioHorasResponse gerarRelatorioHoras(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        List<PontoEletronico> pontos = pontoRepository.findByUsuarioIdAndPeriodo(usuarioId, dataInicio, dataFim);

        Map<LocalDate, PontoEletronico> registrosPorData = pontos.stream()
                .collect(Collectors.toMap(PontoEletronico::getData, p -> p));

        List<RelatorioHorasResponse.RegistroDiario> registrosDiarios = new ArrayList<>();
        long totalMinutosPeriodo = 0;

        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            PontoEletronico registroDia = registrosPorData.get(dataAtual);
            long minutosTrabalhadosDia = registroDia != null ? calcularHorasTrabalhadasMinutos(registroDia) : 0;
            totalMinutosPeriodo += minutosTrabalhadosDia;

            List<PontoEletronicoResponse> pontosResponse = registroDia != null ?
                    mapToResponseList(registroDia) : Collections.emptyList();

            registrosDiarios.add(RelatorioHorasResponse.RegistroDiario.builder()
                    .data(dataAtual)
                    .pontos(pontosResponse)
                    .horasTrabalhadasMinutos(minutosTrabalhadosDia)
                    .horasTrabalhadasFormatado(formatarMinutos(minutosTrabalhadosDia))
                    .diaCompleto(registroDia != null && registroDia.isCompleto())
                    .build());

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

    @Transactional
    public void removerPonto(UUID pontoId) {
        if (!pontoRepository.existsById(pontoId)) {
            throw new EntityNotFoundException("Registro de ponto não encontrado");
        }
        pontoRepository.deleteById(pontoId);
        log.info("Ponto removido com sucesso - ID: {}", pontoId);
    }

    @Transactional
    public void atualizarPontosPorData(UUID usuarioId, LocalDate data, Map<String, Object> dadosAtualizacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        String observacao = (String) dadosAtualizacao.get("observacao");
        if (observacao == null || observacao.trim().isEmpty()) {
            throw new BusinessRuleException("Observação é obrigatória");
        }

        Optional<PontoEletronico> registroOpt = pontoRepository.findByUsuarioIdAndData(usuarioId, data);

        PontoEletronico registro = registroOpt.orElseGet(() -> PontoEletronico.builder()
                .usuario(usuario)
                .data(data)
                .build());

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

        registro.setObservacao(observacao.trim());
        pontoRepository.save(registro);
        log.info("Pontos atualizados para usuário {} na data {} via resolução de solicitação", usuarioId, data);
    }

    public List<UsuarioResponse> listarUsuariosComPontoNaData(LocalDate data) {
        List<PontoEletronico> pontos = pontoRepository.findByDataOrderByDataAsc(data);

        Set<UUID> usuariosComPonto = pontos.stream()
                .map(ponto -> ponto.getUsuario().getId())
                .collect(Collectors.toSet());

        List<Usuario> usuarios = usuarioRepository.findAllById(usuariosComPonto);

        return usuarios.stream()
                .filter(Usuario::getAtivo)
                .sorted(Comparator.comparing(Usuario::getNome))
                .map(this::toUsuarioResponse)
                .toList();
    }

    private void validarHorarioPermitido(LocalDateTime dataHora) {
        try {
            var configuracoes = configuracaoService.obterConfiguracoes();
            LocalTime horarioCheckin = configuracoes.getHorarioCheckin();
            LocalTime horarioCheckout = configuracoes.getHorarioCheckout();
            LocalTime horarioAtual = dataHora.toLocalTime();

            if (horarioAtual.isBefore(horarioCheckin) || horarioAtual.isAfter(horarioCheckout)) {
                throw new BusinessRuleException(
                        String.format("Registros de ponto só são permitidos entre %s e %s. Horário atual: %s",
                                horarioCheckin, horarioCheckout, horarioAtual));
            }
        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Erro ao validar horário baseado nas configurações da empresa. Permitindo registro. Erro: {}", e.getMessage());
        }
    }

    private LocalDateTime parseHorario(LocalDate data, String horario) {
        if (horario == null || horario.trim().isEmpty()) {
            return null;
        }
        String[] partes = horario.trim().split(":");
        if (partes.length != 2) {
            throw new BusinessRuleException("Formato de horário inválido: " + horario);
        }
        try {
            int hora = Integer.parseInt(partes[0]);
            int minuto = Integer.parseInt(partes[1]);
            return data.atTime(hora, minuto);
        } catch (NumberFormatException e) {
            throw new BusinessRuleException("Formato de horário inválido: " + horario);
        }
    }

    private long calcularHorasTrabalhadasMinutos(PontoEletronico registro) {
        long totalMinutos = 0;
        if (registro.getEntrada1() != null && registro.getSaida1() != null) {
            totalMinutos += Duration.between(registro.getEntrada1(), registro.getSaida1()).toMinutes();
        }
        if (registro.getEntrada2() != null && registro.getSaida2() != null) {
            totalMinutos += Duration.between(registro.getEntrada2(), registro.getSaida2()).toMinutes();
        }
        if (registro.getEntrada3() != null && registro.getSaida3() != null) {
            totalMinutos += Duration.between(registro.getEntrada3(), registro.getSaida3()).toMinutes();
        }
        return totalMinutos;
    }

    private String formatarMinutos(long totalMinutos) {
        long horas = totalMinutos / 60;
        long minutos = totalMinutos % 60;
        if (horas == 0) return minutos + "min";
        if (minutos == 0) return horas + "h";
        return horas + "h " + minutos + "min";
    }

    private List<PontoEletronicoResponse> mapToResponseList(PontoEletronico registro) {
        List<PontoEletronicoResponse> responses = new ArrayList<>();
        if (registro.getEntrada1() != null) responses.add(createResponse(registro, registro.getEntrada1(), "ENTRADA_1", "Entrada 1"));
        if (registro.getSaida1() != null) responses.add(createResponse(registro, registro.getSaida1(), "SAIDA_1", "Saída 1"));
        if (registro.getEntrada2() != null) responses.add(createResponse(registro, registro.getEntrada2(), "ENTRADA_2", "Entrada 2"));
        if (registro.getSaida2() != null) responses.add(createResponse(registro, registro.getSaida2(), "SAIDA_2", "Saída 2"));
        if (registro.getEntrada3() != null) responses.add(createResponse(registro, registro.getEntrada3(), "ENTRADA_3", "Entrada 3"));
        if (registro.getSaida3() != null) responses.add(createResponse(registro, registro.getSaida3(), "SAIDA_3", "Saída 3"));
        return responses;
    }

    private PontoEletronicoResponse createResponse(PontoEletronico registro, LocalDateTime dataHora,
                                                    String tipoCodigo, String tipoDescricao) {
        return PontoEletronicoResponse.builder()
                .id(registro.getId())
                .usuarioId(registro.getUsuario().getId())
                .nomeUsuario(registro.getUsuario().getNome())
                .dataHora(dataHora)
                .tipoPonto(tipoCodigo)
                .tipoPontoDescricao(tipoDescricao)
                .localizacao(registro.getLocalizacao())
                .observacao(registro.getObservacao())
                .createdAt(registro.getCreatedAt())
                .build();
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .primeiroLogin(usuario.getPrimeiroLogin())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
