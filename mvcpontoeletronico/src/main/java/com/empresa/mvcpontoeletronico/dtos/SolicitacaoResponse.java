package com.empresa.mvcpontoeletronico.dtos;

import com.empresa.mvcpontoeletronico.entities.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de consulta de solicitações
 * Arquitetura MVC: Camada de Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResponse {
    
    private UUID id;
    private LocalDate dataReferencia;
    private UUID usuarioId;
    private String nomeUsuario;
    private MotivoResponse motivo;
    private String descricao;
    
    // Informações do anexo (sem o conteúdo para evitar overhead)
    private String anexoNome;
    private String anexoTipo;
    private Long anexoTamanho;
    private Boolean temAnexo;
    
    private StatusSolicitacao status;
    private String statusDescricao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MotivoResponse {
        private UUID id;
        private String descricao;
    }
}