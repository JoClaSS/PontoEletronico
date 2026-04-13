package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dto.LoginRequest;
import com.empresa.mvcpontoeletronico.dto.LoginResponse;
import com.empresa.mvcpontoeletronico.security.CustomUserPrincipal;
import com.empresa.mvcpontoeletronico.services.AuthService;
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
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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
}