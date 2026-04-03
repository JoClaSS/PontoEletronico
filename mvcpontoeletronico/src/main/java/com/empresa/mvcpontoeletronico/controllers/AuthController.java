package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.UserInfoDTO;
import com.empresa.mvcpontoeletronico.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/user-info")
    public ResponseEntity<UserInfoDTO> getCurrentUser(Authentication authentication) {
        UserInfoDTO userInfo = authService.getCurrentUserInfo(authentication);
        
        if (userInfo != null) {
            return ResponseEntity.ok(userInfo);
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugAuth(Authentication authentication) {
        Map<String, Object> debug = new HashMap<>();
        
        debug.put("authenticated", authentication != null && authentication.isAuthenticated());
        debug.put("principal", authentication != null ? authentication.getPrincipal().getClass().getSimpleName() : "null");
        debug.put("authorities", authentication != null ? authentication.getAuthorities() : "null");
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            debug.put("jwt_claims", jwt.getClaims());
            debug.put("jwt_subject", jwt.getSubject());
            debug.put("jwt_realm_access", jwt.getClaimAsMap("realm_access"));
        }
        
        return ResponseEntity.ok(debug);
    }

    @GetMapping("/check-role/{role}")
    public ResponseEntity<Boolean> checkRole(@PathVariable String role, Authentication authentication) {
        boolean hasRole = authService.hasRole(authentication, role);
        return ResponseEntity.ok(hasRole);
    }

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin(Authentication authentication) {
        boolean isAdmin = authService.isAdmin(authentication);
        return ResponseEntity.ok(isAdmin);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // O logout será tratado pelo frontend com o Keycloak
        return ResponseEntity.ok("Logout successful");
    }
}