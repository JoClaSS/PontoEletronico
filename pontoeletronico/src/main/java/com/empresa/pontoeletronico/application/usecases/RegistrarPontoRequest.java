package com.empresa.pontoeletronico.application.usecases;

import com.empresa.pontoeletronico.domain.entities.TipoPonto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request para o caso de uso de registrar ponto
 */
@Data
@Builder
public class RegistrarPontoRequest {
    
    private String usuarioId;
    private LocalDateTime dataHora;
    private TipoPonto tipo; // Opcional, será determinado automaticamente se não informado
    private String localizacao;
    private String observacao;
    private String dispositivoId;
}