package com.empresa.arquiteturalimpa.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA (modelo de persistência) para MotivoSolicitacao.
 */
@Entity
@Table(name = "motivos_solicitacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotivoSolicitacaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "descricao", length = 100, nullable = false, unique = true)
    private String descricao;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Builder.Default
    @Column(name = "requer_anexo", nullable = false)
    private Boolean requerAnexo = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
