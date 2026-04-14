package com.empresa.mvcpontoeletronico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
 * Entidade Solicitacao - representa uma solicitação do usuário
 * Arquitetura MVC: Camada Model
 */
@Entity
@Table(name = "solicitacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Data de referência é obrigatória")
    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motivo_id", nullable = false)
    @NotNull(message = "Motivo é obrigatório")
    private MotivoSolicitacao motivo;
    
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;
    
    // Campos para anexo
    @Column(name = "anexo_nome")
    private String anexoNome;
    
    @Column(name = "anexo_tipo", length = 100)
    private String anexoTipo;
    
    @Column(name = "anexo_tamanho")
    private Long anexoTamanho;
    
    @Column(name = "anexo_conteudo", columnDefinition = "BYTEA")
    private byte[] anexoConteudo;
    
    // Campos para dias consecutivos
    @Column(name = "dias_consecutivos", nullable = false)
    @Builder.Default
    private Boolean diasConsecutivos = false;
    
    @Column(name = "quantidade_dias")
    private Integer quantidadeDias;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status é obrigatório")
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