package com.empresa.arquiteturalimpa.infrastructure.persistence.entity;

import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA (modelo de persistência) para Solicitacao.
 */
@Entity
@Table(name = "solicitacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motivo_id", nullable = false)
    private MotivoSolicitacaoJpaEntity motivo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "anexo_nome")
    private String anexoNome;

    @Column(name = "anexo_tipo", length = 100)
    private String anexoTipo;

    @Column(name = "anexo_tamanho")
    private Long anexoTamanho;

    @Column(name = "anexo_conteudo", columnDefinition = "BYTEA")
    private byte[] anexoConteudo;

    @Column(name = "dias_consecutivos", nullable = false)
    @Builder.Default
    private Boolean diasConsecutivos = false;

    @Column(name = "quantidade_dias")
    private Integer quantidadeDias;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StatusSolicitacao status = StatusSolicitacao.ABERTO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
