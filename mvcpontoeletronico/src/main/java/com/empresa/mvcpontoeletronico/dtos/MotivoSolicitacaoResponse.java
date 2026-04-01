package com.empresa.mvcpontoeletronico.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de consulta de motivos de solicitação
 * Arquitetura MVC: Camada de Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotivoSolicitacaoResponse {
    
    private UUID id;
    private String descricao;
    private Boolean ativo;
    private Boolean requerAnexo;
    private LocalDateTime createdAt;
}