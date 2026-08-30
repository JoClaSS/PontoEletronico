package com.empresa.arquiteturalimpa.domain.enums;

public enum RoleType {
    FUNCIONARIO("FUNCIONARIO"),
    ADMIN("ADMIN"),
    VISITANTE("VISITANTE");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
