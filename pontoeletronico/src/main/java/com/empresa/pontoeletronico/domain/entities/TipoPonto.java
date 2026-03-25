package com.empresa.pontoeletronico.domain.entities;

/**
 * Enum que representa os tipos de registro de ponto
 */
public enum TipoPonto {
    ENTRADA("Entrada"),
    SAIDA("Saída"),
    ENTRADA_ALMOCO("Entrada Almoço"),
    SAIDA_ALMOCO("Saída Almoço");
    
    private final String descricao;
    
    TipoPonto(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}