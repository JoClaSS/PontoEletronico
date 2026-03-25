package com.empresa.pontoeletronico.application.usecases;

import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * Relatório com informações sobre horas trabalhadas
 */
@Data
@Builder
public class RelatorioHoras {
    
    private String usuarioId;
    private LocalDate data; // Para relatórios diários
    private LocalDate dataInicio; // Para relatórios de período
    private LocalDate dataFim; // Para relatórios de período
    private Duration horasTrabalhadas;
    private Duration cargaHorariaEsperada;
    private Duration saldoHoras;
    private List<PontoEletronico> pontos;
    
    /**
     * Verifica se há horas em débito
     */
    public boolean temHorasDebito() {
        return saldoHoras.isNegative();
    }
    
    /**
     * Verifica se há horas excedentes
     */
    public boolean temHorasExcedentes() {
        return saldoHoras.toMinutes() > 0;
    }
    
    /**
     * Retorna o saldo em formato legível (horas e minutos)
     */
    public String getSaldoFormatado() {
        long horas = Math.abs(saldoHoras.toHours());
        long minutos = Math.abs(saldoHoras.toMinutes()) % 60;
        String sinal = saldoHoras.isNegative() ? "-" : "+";
        return String.format("%s%02d:%02d", sinal, horas, minutos);
    }
    
    /**
     * Retorna as horas trabalhadas em formato legível
     */
    public String getHorasTrabalhadasFormatadas() {
        long horas = horasTrabalhadas.toHours();
        long minutos = horasTrabalhadas.toMinutes() % 60;
        return String.format("%02d:%02d", horas, minutos);
    }
}