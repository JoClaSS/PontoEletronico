package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.application.auth.AuthService;
import com.empresa.arquiteturalimpa.application.auth.dto.LoginRequest;
import com.empresa.arquiteturalimpa.application.auth.dto.LoginResponse;
import com.empresa.arquiteturalimpa.application.auth.dto.TrocaSenhaRequest;
import com.empresa.arquiteturalimpa.application.usuario.UsuarioService;
import com.empresa.arquiteturalimpa.infrastructure.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Com JWT stateless, logout é feito no lado do cliente removendo o token
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal userPrincipal) {
            return ResponseEntity.ok(usuarioService.toResponse(userPrincipal.getUsuario()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/trocar-senha")
    public ResponseEntity<?> trocarSenha(@Valid @RequestBody TrocaSenhaRequest request, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal userPrincipal)) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        usuarioService.trocarSenha(
                userPrincipal.getUsuario().getId(),
                request.getSenhaAtual(),
                request.getNovaSenha(),
                request.getConfirmarSenha()
        );

        return ResponseEntity.ok().body("Senha alterada com sucesso");
    }
}
