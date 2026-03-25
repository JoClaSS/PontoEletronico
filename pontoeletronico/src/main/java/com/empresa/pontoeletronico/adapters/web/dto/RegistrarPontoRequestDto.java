package com.empresa.pontoeletronico.adapters.web.dto;

import com.empresa.pontoeletronico.domain.entities.TipoPonto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para requisição de registro de ponto
 */
@Data
public class RegistrarPontoRequestDto {
    
    private String usuarioId;
    private LocalDateTime dataHora;
    private TipoPonto tipo;
    private String localizacao;
    private String observacao;
    private String dispositivoId;
}