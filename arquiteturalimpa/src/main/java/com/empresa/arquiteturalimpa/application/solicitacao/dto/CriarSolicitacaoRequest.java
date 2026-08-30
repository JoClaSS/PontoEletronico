package com.empresa.arquiteturalimpa.application.solicitacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

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

    // Preenchidos via multipart/form-data
    private String anexoNome;
    private String anexoTipo;
    private Long anexoTamanho;
    private byte[] anexoConteudo;

    @Builder.Default
    private Boolean diasConsecutivos = false;
    private Integer quantidadeDias;
}
