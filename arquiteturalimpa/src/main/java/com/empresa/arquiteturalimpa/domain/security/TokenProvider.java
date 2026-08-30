package com.empresa.arquiteturalimpa.domain.security;

import java.util.UUID;

/**
 * Porta de saída (output port) para geração/validação de tokens de autenticação.
 */
public interface TokenProvider {

    String generateToken(String email, UUID userId, String role);

    String extractUsername(String token);

    boolean validateToken(String token, String email);
}
