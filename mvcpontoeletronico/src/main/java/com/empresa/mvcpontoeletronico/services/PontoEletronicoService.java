package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.PontoEletronicoResponse;
import com.empresa.mvcpontoeletronico.dtos.RegistrarPontoRequest;
import com.empresa.mvcpontoeletronico.dtos.RelatorioHorasResponse;
import com.empresa.mvcpontoeletronico.entities.PontoEletronico;
import com.empresa.mvcpontoeletronico.entities.TipoPonto;
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
    private static final int MAX_REGISTROS_POR_DIA = 4;
    
    /**
     * Registra um novo ponto eletrônico
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
        
        // Valida se não é muito antigo (máximo 1 dia)
        if (dataHora.isBefore(LocalDateTime.now().minusDays(1))) {
            throw new IllegalArgumentException("Não é possível registrar pontos com mais de 1 dia");
        }
        
        // Verifica quantidade de registros no dia
        LocalDate data = dataHora.toLocalDate();
        Long registrosHoje = pontoRepository.countByUsuarioIdAndData(request.getUsuarioId(), data);
        if (registrosHoje >= MAX_REGISTROS_POR_DIA) {
            throw new IllegalArgumentException("Limite máximo de " + MAX_REGISTROS_POR_DIA + " registros por dia já atingido");
        }
        
        // Determina o tipo de ponto se não informado
        TipoPonto tipoPonto = request.getTipoPonto();
        if (tipoPonto == null) {
            Optional<PontoEletronico> ultimoRegistro = pontoRepository.findUltimoRegistroPorUsuario(request.getUsuarioId());
            TipoPonto ultimoTipo = ultimoRegistro.map(PontoEletronico::getTipoPonto).orElse(null);
            tipoPonto = PontoEletronico.determinarProximoTipo(ultimoTipo);
        }
        
        // Valida intervalo mínimo entre registros (15 minutos)
        LocalDateTime inicioJanela = dataHora.minusMinutes(15);
        LocalDateTime fimJanela = dataHora.plusMinutes(15);
        if (pontoRepository.existsByUsuarioIdAndDataHoraBetween(request.getUsuarioId(), inicioJanela, fimJanela)) {
            throw new IllegalArgumentException("Deve haver um intervalo mínimo de 15 minutos entre registros");
        }
        
        // Cria o registro
        PontoEletronico ponto = PontoEletronico.builder()
            .usuario(usuario)
            .dataHora(dataHora)
            .tipoPonto(tipoPonto)
            .localizacao(request.getLocalizacao())
            .observacao(request.getObservacao())
            .build();
        
        PontoEletronico pontoSalvo = pontoRepository.save(ponto);
        log.info("Ponto registrado com sucesso - ID: {}, Usuário: {}, Tipo: {}", 
                 pontoSalvo.getId(), usuario.getNome(), tipoPonto);
        
        return mapToResponse(pontoSalvo);
    }
    
    /**
     * Consulta pontos por usuário e data
     */
    public List<PontoEletronicoResponse> consultarPontosPorData(UUID usuarioId, LocalDate data) {
        log.debug("Consultando pontos - Usuário: {}, Data: {}", usuarioId, data);
        
        List<PontoEletronico> pontos = pontoRepository.findByUsuarioIdAndData(usuarioId, data);
        return pontos.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
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
        
        List<PontoEletronico> pontos = pontoRepository.findByUsuarioIdAndPeriodo(usuarioId, dataInicio, dataFim);
        return pontos.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }
    
    /**
     * Gera relatório de horas trabalhadas
     */
    public RelatorioHorasResponse gerarRelatorioHoras(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Gerando relatório de horas - Usuário: {}, Período: {} a {}", usuarioId, dataInicio, dataFim);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        List<PontoEletronico> pontos = pontoRepository.findByUsuarioIdAndPeriodo(usuarioId, dataInicio, dataFim);
        
        // Agrupa pontos por data
        Map<LocalDate, List<PontoEletronico>> pontosPorData = pontos.stream()
            .collect(Collectors.groupingBy(p -> p.getDataHora().toLocalDate()));
        
        List<RelatorioHorasResponse.RegistroDiario> registrosDiarios = new ArrayList<>();
        long totalMinutosPeriodo = 0;
        
        // Processa cada dia no período
        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            List<PontoEletronico> pontosData = pontosPorData.getOrDefault(dataAtual, new ArrayList<>());
            long minutosTrabalhadosDia = calcularHorasTrabalhadasMinutos(pontosData);
            totalMinutosPeriodo += minutosTrabalhadosDia;
            
            RelatorioHorasResponse.RegistroDiario registro = RelatorioHorasResponse.RegistroDiario.builder()
                .data(dataAtual)
                .pontos(pontosData.stream().map(this::mapToResponse).collect(Collectors.toList()))
                .horasTrabalhadasMinutos(minutosTrabalhadosDia)
                .horasTrabalhadasFormatado(formatarMinutos(minutosTrabalhadosDia))
                .diaCompleto(pontosData.size() == MAX_REGISTROS_POR_DIA)
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
     * Calcula horas trabalhadas em um dia (em minutos)
     */
    private long calcularHorasTrabalhadasMinutos(List<PontoEletronico> pontos) {
        if (pontos.size() < 2) return 0;
        
        // Ordena os pontos por horário
        pontos.sort(Comparator.comparing(PontoEletronico::getDataHora));
        
        long totalMinutos = 0;
        LocalDateTime inicioTrabalho = null;
        
        for (PontoEletronico ponto : pontos) {
            if (ponto.isEntrada()) {
                inicioTrabalho = ponto.getDataHora();
            } else if (ponto.isSaida() && inicioTrabalho != null) {
                Duration duracao = Duration.between(inicioTrabalho, ponto.getDataHora());
                totalMinutos += duracao.toMinutes();
                inicioTrabalho = null; // Reset para o próximo período
            }
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
     * Converte PontoEletronico para Response DTO
     */
    private PontoEletronicoResponse mapToResponse(PontoEletronico ponto) {
        return PontoEletronicoResponse.builder()
            .id(ponto.getId())
            .usuarioId(ponto.getUsuario().getId())
            .nomeUsuario(ponto.getUsuario().getNome())
            .dataHora(ponto.getDataHora())
            .tipoPonto(ponto.getTipoPonto())
            .tipoPontoDescricao(ponto.getTipoPonto().getDescricao())
            .localizacao(ponto.getLocalizacao())
            .observacao(ponto.getObservacao())
            .createdAt(ponto.getCreatedAt())
            .build();
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
    }
}