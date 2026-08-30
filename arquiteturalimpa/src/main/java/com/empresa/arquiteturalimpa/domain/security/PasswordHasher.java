package com.empresa.arquiteturalimpa.domain.security;

/**
 * Porta de saída (output port) para hashing/verificação de senhas.
 * Mantém a camada de aplicação independente do provedor de criptografia usado.
 */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
