package com.empresa.pontoeletronico.adapters.web.dto;

import com.empresa.pontoeletronico.domain.entities.TipoPonto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para resposta de ponto eletrônico
 */
@Data
public class PontoEletronicoResponseDto {
    
    private String id;
    private String usuarioId;
    private LocalDateTime dataHora;
    private TipoPonto tipo;
    private String tipoDescricao;
    private String localizacao;
    private String observacao;
    private String dispositivoId;
}