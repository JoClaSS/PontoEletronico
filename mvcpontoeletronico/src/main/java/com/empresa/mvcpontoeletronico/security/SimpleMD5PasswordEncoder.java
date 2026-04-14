package com.empresa.mvcpontoeletronico.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordEncoder customizado para compatibilidade com senhas MD5 simples
 * do banco de dados (sem salt)
 */
public class SimpleMD5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        String hash = md5Hash(rawPassword.toString());
        System.out.println("SimpleMD5PasswordEncoder.encode - Input: '" + rawPassword + "' -> Hash: '" + hash + "'");
        return hash;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String hashedInput = md5Hash(rawPassword.toString());
        boolean matches = hashedInput.equals(encodedPassword);
        System.out.println("SimpleMD5PasswordEncoder.matches - Input: '" + rawPassword + "' -> Hash: '" + hashedInput + "' vs Stored: '" + encodedPassword + "' = " + matches);
        return matches;
    }

    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            
            // Converter byte array em hexadecimal
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