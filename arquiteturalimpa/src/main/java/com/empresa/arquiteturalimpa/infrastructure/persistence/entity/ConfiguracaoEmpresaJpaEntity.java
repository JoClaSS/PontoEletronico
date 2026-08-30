package com.empresa.arquiteturalimpa.infrastructure.persistence.entity;

import jakarta.persistence.*;
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
 * Entidade JPA (modelo de persistência) para ConfiguracaoEmpresa.
 */
@Entity
@Table(name = "configuracoes_empresa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoEmpresaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_empresa", nullable = false)
    private String nomeEmpresa;

    @Column(name = "horario_checkin", nullable = false)
    private LocalTime horarioCheckin;

    @Column(name = "horario_checkout", nullable = false)
    private LocalTime horarioCheckout;

    @Column(name = "foto_empresa", columnDefinition = "BYTEA")
    private byte[] fotoEmpresa;

    @Column(name = "logo_empresa_nome")
    private String logoEmpresaNome;

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
}
