package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dto.LoginRequest;
import com.empresa.mvcpontoeletronico.dto.LoginResponse;
import com.empresa.mvcpontoeletronico.dto.TrocaSenhaRequest;
import com.empresa.mvcpontoeletronico.security.CustomUserPrincipal;
import com.empresa.mvcpontoeletronico.services.AuthService;
import com.empresa.mvcpontoeletronico.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            System.out.println("Tentando login para email: " + request.getEmail());
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Erro no login: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Com JWT stateless, logout é feito no lado do cliente removendo o token
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal userPrincipal) {
            return ResponseEntity.ok(userPrincipal.getUsuario());
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/trocar-senha")
    public ResponseEntity<?> trocarSenha(@Valid @RequestBody TrocaSenhaRequest request, Authentication authentication) {
        try {
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
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }
}