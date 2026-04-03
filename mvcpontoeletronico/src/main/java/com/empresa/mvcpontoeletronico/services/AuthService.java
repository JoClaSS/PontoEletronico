package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.UserInfoDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    public UserInfoDTO getCurrentUserInfo(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return extractUserInfo(jwt);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private UserInfoDTO extractUserInfo(Jwt jwt) {
        UserInfoDTO userInfo = new UserInfoDTO();
        
        // Informações básicas do JWT
        userInfo.setId(jwt.getClaimAsString("sub"));
        userInfo.setUsername(jwt.getClaimAsString("preferred_username"));
        userInfo.setEmail(jwt.getClaimAsString("email"));
        userInfo.setFirstName(jwt.getClaimAsString("given_name"));
        userInfo.setLastName(jwt.getClaimAsString("family_name"));
        userInfo.setEmailVerified(jwt.getClaimAsBoolean("email_verified"));
        
        // Extrair roles do realm_access
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            userInfo.setRoles(roles);
        }
        
        return userInfo;
    }

    public boolean hasRole(Authentication authentication, String role) {
        UserInfoDTO userInfo = getCurrentUserInfo(authentication);
        return userInfo != null && userInfo.getRoles() != null && 
               userInfo.getRoles().contains(role);
    }

    public boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ADMIN");
    }

    public boolean isFuncionario(Authentication authentication) {
        return hasRole(authentication, "FUNCIONARIO");
    }

    public boolean isMaster(Authentication authentication) {
        return hasRole(authentication, "MASTER");
    }
}