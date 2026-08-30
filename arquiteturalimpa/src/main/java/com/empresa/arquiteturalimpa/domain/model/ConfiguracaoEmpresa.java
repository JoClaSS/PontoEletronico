package com.empresa.arquiteturalimpa.domain.model;

import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidade de domínio ConfiguracaoEmpresa - configurações globais da empresa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoEmpresa {

    private UUID id;
    private String nomeEmpresa;
    private LocalTime horarioCheckin;
    private LocalTime horarioCheckout;
    private byte[] fotoEmpresa;
    private String logoEmpresaNome;
    private String logoEmpresaTipo;
    private Integer logoEmpresaTamanho;

    @Builder.Default
    private Integer intervaloMinimoMinutos = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Valida se o horário de checkout é posterior ao de check-in.
     * Regra de negócio do domínio, antes executada via callback JPA @PrePersist/@PreUpdate.
     */
    public void validarHorarios() {
        if (horarioCheckin != null && horarioCheckout != null) {
            if (horarioCheckout.isBefore(horarioCheckin) || horarioCheckout.equals(horarioCheckin)) {
                throw new BusinessRuleException("Horário de checkout deve ser posterior ao horário de check-in");
            }
        }
    }
}
