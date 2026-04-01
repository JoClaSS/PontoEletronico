package com.empresa.pontoeletronico.domain.entities;

/**
 * Enum que representa os tipos de registro de ponto
 */
public enum TipoPonto {
    ENTRADA_1("entrada_1", "Entrada 1"),
    SAIDA_1("saida_1", "Saída 1"),
    ENTRADA_2("entrada_2", "Entrada 2"),
    SAIDA_2("saida_2", "Saída 2"),
    ENTRADA_3("entrada_3", "Entrada 3"),
    SAIDA_3("saida_3", "Saída 3");
    
    private final String codigo;
    private final String descricao;
    
    TipoPonto(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
}