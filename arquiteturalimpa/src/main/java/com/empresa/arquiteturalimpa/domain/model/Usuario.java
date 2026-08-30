package com.empresa.arquiteturalimpa.domain.model;

import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio Usuario - representa um funcionário no sistema.
 * Livre de anotações de persistência (Clean Architecture: camada Domain).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private UUID id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;

    @Builder.Default
    private RoleType role = RoleType.FUNCIONARIO;

    @Builder.Default
    private Boolean ativo = true;

    @Builder.Default
    private Boolean primeiroLogin = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
