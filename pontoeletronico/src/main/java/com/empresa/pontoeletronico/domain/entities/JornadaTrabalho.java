package com.empresa.pontoeletronico.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Entidade de domínio que representa a jornada de trabalho de um funcionário
 */
@Data
@Builder
public class JornadaTrabalho {
    
    private String id;
    private String nome;
    private LocalTime inicioExpediente;
    private LocalTime fimExpediente;
    private LocalTime inicioIntervalo;
    private LocalTime fimIntervalo;
    private Duration cargaHorariaDiaria;
    private boolean permiteHoraExtra;
    private Duration limiteHoraExtra;
    
    /**
     * Calcula a carga horária esperada para o dia (sem contar intervalos)
     */
    public Duration calcularCargaHorariaEsperada() {
        if (inicioExpediente != null && fimExpediente != null) {
            Duration totalExpediente = Duration.between(inicioExpediente, fimExpediente);
            
            if (inicioIntervalo != null && fimIntervalo != null) {
                Duration intervalo = Duration.between(inicioIntervalo, fimIntervalo);
                return totalExpediente.minus(intervalo);
            }
            
            return totalExpediente;
        }
        
        return cargaHorariaDiaria != null ? cargaHorariaDiaria : Duration.ZERO;
    }
    
    /**
     * Verifica se um horário está dentro do expediente esperado
     */
    public boolean isDentroDoExpediente(LocalTime horario) {
        return inicioExpediente != null && fimExpediente != null
            && horario.isAfter(inicioExpediente.minusMinutes(30))
            && horario.isBefore(fimExpediente.plusMinutes(30));
    }
    
    /**
     * Verifica se um horário está no intervalo de almoço
     */
    public boolean isDentroDoIntervalo(LocalTime horario) {
        return inicioIntervalo != null && fimIntervalo != null
            && horario.isAfter(inicioIntervalo)
            && horario.isBefore(fimIntervalo);
    }
}