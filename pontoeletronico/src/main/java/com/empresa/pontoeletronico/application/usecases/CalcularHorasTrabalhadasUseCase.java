package com.empresa.pontoeletronico.application.usecases;

import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import com.empresa.pontoeletronico.domain.entities.Usuario;
import com.empresa.pontoeletronico.domain.repositories.PontoEletronicoRepository;
import com.empresa.pontoeletronico.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso para calcular horas trabalhadas
 */
@RequiredArgsConstructor
public class CalcularHorasTrabalhadasUseCase {
    
    private final PontoEletronicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public RelatorioHoras calcularHorasDia(String usuarioId, LocalDate data) {
        // Valida se o usuário existe
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        List<PontoEletronico> pontos = pontoRepository.buscarPorUsuarioEData(usuarioId, data);
        
        Duration horasTrabalhadas = calcularHorasTrabalhadasDoPontos(pontos);
        Duration cargaHorariaEsperada = usuario.getJornada() != null 
            ? usuario.getJornada().calcularCargaHorariaEsperada() 
            : Duration.ofHours(8); // Default 8 horas
        
        Duration saldo = horasTrabalhadas.minus(cargaHorariaEsperada);
        
        return RelatorioHoras.builder()
            .usuarioId(usuarioId)
            .data(data)
            .horasTrabalhadas(horasTrabalhadas)
            .cargaHorariaEsperada(cargaHorariaEsperada)
            .saldoHoras(saldo)
            .pontos(pontos)
            .build();
    }
    
    public RelatorioHoras calcularHorasPeriodo(String usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        // Valida se o usuário existe
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Valida o período
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
        }
        
        List<PontoEletronico> pontos = pontoRepository.buscarPorUsuarioEPeriodo(usuarioId, dataInicio, dataFim);
        
        Duration horasTrabalhadas = calcularHorasTrabalhadasDoPontos(pontos);
        
        // Calcula carga horária esperada baseada nos dias úteis do período
        Duration cargaHorariaDiaria = usuario.getJornada() != null 
            ? usuario.getJornada().calcularCargaHorariaEsperada() 
            : Duration.ofHours(8);
            
        long diasPeriodo = dataInicio.datesUntil(dataFim.plusDays(1)).count();
        Duration cargaHorariaEsperada = cargaHorariaDiaria.multipliedBy(diasPeriodo);
        
        Duration saldo = horasTrabalhadas.minus(cargaHorariaEsperada);
        
        return RelatorioHoras.builder()
            .usuarioId(usuarioId)
            .dataInicio(dataInicio)
            .dataFim(dataFim)
            .horasTrabalhadas(horasTrabalhadas)
            .cargaHorariaEsperada(cargaHorariaEsperada)
            .saldoHoras(saldo)
            .pontos(pontos)
            .build();
    }
    
    private Duration calcularHorasTrabalhadasDoPontos(List<PontoEletronico> pontos) {
        Duration totalHoras = Duration.ZERO;
        LocalDateTime entrada = null;
        LocalDateTime saidaAlmoco = null;
        
        for (PontoEletronico ponto : pontos) {
            switch (ponto.getTipo()) {
                case ENTRADA -> entrada = ponto.getDataHora();
                case SAIDA_ALMOCO -> {
                    if (entrada != null) {
                        saidaAlmoco = ponto.getDataHora();
                        // Adiciona período da manhã
                        totalHoras = totalHoras.plus(Duration.between(entrada, saidaAlmoco));
                    }
                }
                case ENTRADA_ALMOCO -> {
                    // Reinicia a contagem para o período da tarde
                    entrada = ponto.getDataHora();
                }
                case SAIDA -> {
                    if (entrada != null) {
                        // Adiciona período da tarde ou expediente completo
                        totalHoras = totalHoras.plus(Duration.between(entrada, ponto.getDataHora()));
                        entrada = null; // Reset para próximo ciclo
                    }
                }
            }
        }
        
        return totalHoras;
    }
}