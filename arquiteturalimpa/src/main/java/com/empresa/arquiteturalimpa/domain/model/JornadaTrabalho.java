package com.empresa.arquiteturalimpa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio JornadaTrabalho - representa tipos de jornada de trabalho.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JornadaTrabalho {

    private UUID id;
    private String nome;
    private Integer horasSemanais;
    private Integer diasTrabalhados;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Calcula a média de horas por dia.
     */
    public double calcularHorasPorDia() {
        if (diasTrabalhados == null || diasTrabalhados == 0) return 0;
        return (double) horasSemanais / diasTrabalhados;
    }
}
