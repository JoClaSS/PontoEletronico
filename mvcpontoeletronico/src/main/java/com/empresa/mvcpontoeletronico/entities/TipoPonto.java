package com.empresa.mvcpontoeletronico.entities;

/**
 * Enum que representa os tipos de registro de ponto
 */
public enum TipoPonto {
    ENTRADA("Entrada"),
    SAIDA_ALMOCO("Saída para Almoço"),
    RETORNO_ALMOCO("Retorno do Almoço"),
    SAIDA("Saída");
    
    private final String descricao;
    
    TipoPonto(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}