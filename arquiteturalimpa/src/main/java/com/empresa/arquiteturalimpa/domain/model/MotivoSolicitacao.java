package com.empresa.arquiteturalimpa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio MotivoSolicitacao - representa os motivos das solicitações.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotivoSolicitacao {

    private UUID id;
    private String descricao;

    @Builder.Default
    private Boolean ativo = true;

    @Builder.Default
    private Boolean requerAnexo = false;

    private LocalDateTime createdAt;
}
