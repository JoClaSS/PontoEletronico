package com.empresa.arquiteturalimpa.domain.enums;

/**
 * Representa os status possíveis de uma solicitação
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
