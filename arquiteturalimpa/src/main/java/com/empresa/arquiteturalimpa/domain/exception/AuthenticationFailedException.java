package com.empresa.arquiteturalimpa.domain.exception;

/**
 * Lançada quando as credenciais informadas no login são inválidas.
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
