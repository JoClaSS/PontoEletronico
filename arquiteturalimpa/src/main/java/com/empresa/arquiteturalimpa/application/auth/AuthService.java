package com.empresa.arquiteturalimpa.application.auth;

import com.empresa.arquiteturalimpa.application.auth.dto.LoginRequest;
import com.empresa.arquiteturalimpa.application.auth.dto.LoginResponse;
import com.empresa.arquiteturalimpa.application.usuario.UsuarioService;
import com.empresa.arquiteturalimpa.domain.exception.AuthenticationFailedException;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import com.empresa.arquiteturalimpa.domain.security.PasswordHasher;
import com.empresa.arquiteturalimpa.domain.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso de autenticação. Não depende de nenhum framework de segurança:
 * usa apenas as portas de domínio PasswordHasher e TokenProvider.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationFailedException("Credenciais inválidas"));

        if (!usuario.getAtivo()) {
            throw new AuthenticationFailedException("Usuário inativo");
        }

        if (!passwordHasher.matches(request.getSenha(), usuario.getSenha())) {
            throw new AuthenticationFailedException("Credenciais inválidas");
        }

        String token = tokenProvider.generateToken(usuario.getEmail(), usuario.getId(), usuario.getRole().getValue());

        return LoginResponse.builder()
                .token(token)
                .usuario(usuarioService.toResponse(usuario))
                .primeiroLogin(usuario.getPrimeiroLogin())
                .build();
    }
}
