package com.empresa.arquiteturalimpa.infrastructure.security;

import com.empresa.arquiteturalimpa.domain.security.PasswordHasher;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implementação de PasswordHasher compatível com as senhas MD5 (sem salt) já existentes no banco.
 */
@Component
public class Md5PasswordHasher implements PasswordHasher {

    @Override
    public String hash(String rawPassword) {
        return md5Hash(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return md5Hash(rawPassword).equals(hashedPassword);
    }

    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash MD5", e);
        }
    }
}
