package com.empresa.arquiteturalimpa.application.usuario.dto;

import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private UUID id;
    private String nome;
    private String email;
    private String cpf;
    private RoleType role;
    private Boolean ativo;
    private Boolean primeiroLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
