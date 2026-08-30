package com.empresa.arquiteturalimpa.application.solicitacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

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
