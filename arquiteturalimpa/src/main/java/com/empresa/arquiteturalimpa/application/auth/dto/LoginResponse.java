package com.empresa.arquiteturalimpa.application.auth.dto;

import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private UsuarioResponse usuario;
    private boolean primeiroLogin;
}
