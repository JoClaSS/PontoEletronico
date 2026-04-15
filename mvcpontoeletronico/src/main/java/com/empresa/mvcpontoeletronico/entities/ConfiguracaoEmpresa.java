package com.empresa.mvcpontoeletronico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidade ConfiguracaoEmpresa - configurações globais da empresa
 * Arquitetura MVC: Camada Model
 */
@Entity
@Table(name = "configuracoes_empresa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoEmpresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Nome da empresa é obrigatório")
    @Size(max = 200, message = "Nome da empresa deve ter no máximo 200 caracteres")
    @Column(name = "nome_empresa", nullable = false)
    private String nomeEmpresa;
    
    @NotNull(message = "Horário de check-in é obrigatório")
    @Column(name = "horario_checkin", nullable = false)
    private LocalTime horarioCheckin;
    
    @NotNull(message = "Horário de checkout é obrigatório")
    @Column(name = "horario_checkout", nullable = false)
    private LocalTime horarioCheckout;
    
    @Column(name = "foto_empresa", columnDefinition = "BYTEA")
    private byte[] fotoEmpresa;
    
    @Size(max = 255, message = "Nome do arquivo deve ter no máximo 255 caracteres")
    @Column(name = "logo_empresa_nome")
    private String logoEmpresaNome;
    
    @Size(max = 100, message = "Tipo MIME deve ter no máximo 100 caracteres")
    @Column(name = "logo_empresa_tipo")
    private String logoEmpresaTipo;
    
    @Column(name = "logo_empresa_tamanho")
    private Integer logoEmpresaTamanho;
    
    @Builder.Default
    @Column(name = "intervalo_minimo_minutos", nullable = false)
    private Integer intervaloMinimoMinutos = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Validação customizada para garantir que checkout é após checkin
     */
    @PrePersist
    @PreUpdate
    private void validarHorarios() {
        System.out.println("Validando horários: " + horarioCheckin + " -> " + horarioCheckout);
        
        if (horarioCheckin != null && horarioCheckout != null) {
            if (horarioCheckout.isBefore(horarioCheckin) || horarioCheckout.equals(horarioCheckin)) {
                System.err.println("ERRO: Horário inválido - checkout antes ou igual ao checkin");
                throw new IllegalArgumentException("Horário de checkout deve ser posterior ao horário de check-in");
            }
        }
        
        System.out.println("Validação de horários OK");
    }
}