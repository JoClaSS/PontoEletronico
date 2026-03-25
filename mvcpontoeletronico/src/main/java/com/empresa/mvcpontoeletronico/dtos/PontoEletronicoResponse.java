package com.empresa.mvcpontoeletronico.dtos;

import com.empresa.mvcpontoeletronico.entities.TipoPonto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de consulta de pontos
 * Arquitetura MVC: Camada de Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PontoEletronicoResponse {
    
    private UUID id;
    private UUID usuarioId;
    private String nomeUsuario;
    private LocalDateTime dataHora;
    private TipoPonto tipoPonto;
    private String tipoPontoDescricao;
    private String localizacao;
    private String observacao;
    private LocalDateTime createdAt;
}