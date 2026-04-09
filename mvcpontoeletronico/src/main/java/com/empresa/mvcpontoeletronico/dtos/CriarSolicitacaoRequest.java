package com.empresa.mvcpontoeletronico.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para criação de solicitação
 * Arquitetura MVC: Camada de Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarSolicitacaoRequest {
    
    @NotNull(message = "Data de referência é obrigatória")
    private LocalDate dataReferencia;
    
    @NotNull(message = "ID do usuário é obrigatório")
    private UUID usuarioId;
    
    @NotNull(message = "ID do motivo é obrigatório")
    private UUID motivoId;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    // Campos para anexo (serão preenchidos via multipart/form-data)
    private String anexoNome;
    private String anexoTipo;
    private Long anexoTamanho;
    private byte[] anexoConteudo;
    
    // Campos para dias consecutivos
    @Builder.Default
    private Boolean diasConsecutivos = false;
    private Integer quantidadeDias;
}