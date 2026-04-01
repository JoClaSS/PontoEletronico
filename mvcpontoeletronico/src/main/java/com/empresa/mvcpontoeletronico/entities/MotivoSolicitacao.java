package com.empresa.mvcpontoeletronico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade MotivoSolicitacao - representa os motivos das solicitações
 * Arquitetura MVC: Camada Model
 */
@Entity
@Table(name = "motivos_solicitacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotivoSolicitacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Descrição é obrigatória")
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