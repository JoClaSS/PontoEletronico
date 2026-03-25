package com.empresa.pontoeletronico.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidade de domínio para registro de ponto eletrônico
 * Representa um registro de entrada ou saída de um funcionário
 */
@Data
@Builder
public class PontoEletronico {
    
    private String id;
    private String usuarioId;
    private LocalDateTime dataHora;
    private TipoPonto tipo;
    private String localizacao;
    private String observacao;
    private String dispositivoId;
    
    /**
     * Valida se o registro é válido
     */
    public boolean isValido() {
        return usuarioId != null && !usuarioId.trim().isEmpty() 
            && dataHora != null
            && tipo != null;
    }
    
    /**
     * Verifica se é um registro de entrada
     */
    public boolean isEntrada() {
        return TipoPonto.ENTRADA.equals(tipo);
    }
    
    /**
     * Verifica se é um registro de saída
     */
    public boolean isSaida() {
        return TipoPonto.SAIDA.equals(tipo);
    }
}