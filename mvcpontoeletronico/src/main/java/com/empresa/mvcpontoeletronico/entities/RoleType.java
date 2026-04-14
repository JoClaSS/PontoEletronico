package com.empresa.mvcpontoeletronico.entities;

public enum RoleType {
    FUNCIONARIO("FUNCIONARIO"),
    ADMIN("ADMIN");

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