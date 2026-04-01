package com.empresa.mvcpontoeletronico.entities;

/**
 * Enum StatusSolicitacao - representa os status possíveis de uma solicitação
 * Arquitetura MVC: Camada Model
 */
public enum StatusSolicitacao {
    ABERTO("Aberto"),
    RESOLVIDO("Resolvido"),
    CANCELADO("Cancelado");
    
    private final String descricao;
    
    StatusSolicitacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}