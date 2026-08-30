package com.empresa.arquiteturalimpa.domain.model;

import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio Solicitacao - representa uma solicitação do usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitacao {

    private UUID id;
    private LocalDate dataReferencia;
    private Usuario usuario;
    private MotivoSolicitacao motivo;
    private String descricao;

    private String anexoNome;
    private String anexoTipo;
    private Long anexoTamanho;
    private byte[] anexoConteudo;

    @Builder.Default
    private Boolean diasConsecutivos = false;
    private Integer quantidadeDias;

    @Builder.Default
    private StatusSolicitacao status = StatusSolicitacao.ABERTO;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean temAnexo() {
        return anexoConteudo != null && anexoConteudo.length > 0;
    }
}
