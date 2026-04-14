package com.empresa.mvcpontoeletronico.dto;

import com.empresa.mvcpontoeletronico.entities.Usuario;
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
    private Usuario usuario;
    private boolean primeiroLogin;
}