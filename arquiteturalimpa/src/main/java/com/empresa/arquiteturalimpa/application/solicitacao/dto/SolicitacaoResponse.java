package com.empresa.arquiteturalimpa.application.solicitacao.dto;

import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    private String anexoNome;
    private String anexoTipo;
    private Long anexoTamanho;
    private Boolean temAnexo;

    private Boolean diasConsecutivos;
    private Integer quantidadeDias;

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
