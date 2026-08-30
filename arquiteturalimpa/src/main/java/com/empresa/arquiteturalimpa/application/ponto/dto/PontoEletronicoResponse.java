package com.empresa.arquiteturalimpa.application.ponto.dto;

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
public class PontoEletronicoResponse {

    private UUID id;
    private UUID usuarioId;
    private String nomeUsuario;
    private LocalDateTime dataHora;
    private String tipoPonto;
    private String tipoPontoDescricao;
    private String localizacao;
    private String observacao;
    private LocalDateTime createdAt;
}
