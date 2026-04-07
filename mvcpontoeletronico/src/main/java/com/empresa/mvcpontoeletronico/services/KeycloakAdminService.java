package com.empresa.mvcpontoeletronico.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String username;

    @Value("${keycloak.admin.password}")
    private String password;

    @Value("${keycloak.admin.target-realm}")
    private String targetRealm;

    private Keycloak keycloak;
    private RealmResource realmResource;

    @PostConstruct
    public void initKeycloak() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();

        this.realmResource = keycloak.realm(targetRealm);
    }

    public String createUser(String email, String cpf, String firstName, String lastName, String role) {
        UsersResource usersResource = realmResource.users();

        // Criar representação do usuário
        UserRepresentation user = new UserRepresentation();
        user.setUsername(cpf);  // CPF como username
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        // Criar credencial (senha = CPF) - marcada como temporária
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(cpf);
        credential.setTemporary(true); // Força o usuário a trocar a senha no primeiro login
        
        user.setCredentials(Collections.singletonList(credential));

        // Criar usuário
        Response response = usersResource.create(user);
        
        if (response.getStatus() == 201) {
            // Extrair ID do usuário criado
            String locationHeader = response.getHeaderString("Location");
            String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
            
            // Atribuir role
            assignRoleToUser(userId, role);
            
            response.close();
            return userId;
        } else {
            response.close();
            throw new RuntimeException("Erro ao criar usuário no Keycloak: " + response.getStatus());
        }
    }

    private void assignRoleToUser(String userId, String roleName) {
        try {
            var roleResource = realmResource.roles();
            var role = roleResource.get(roleName).toRepresentation();
            
            realmResource.users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atribuir role ao usuário no Keycloak", e);
        }
    }

    public void updateUser(String userId, String email, String firstName, String lastName) {
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            
            realmResource.users().get(userId).update(user);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar usuário no Keycloak", e);
        }
    }

    public void deleteUser(String userId) {
        try {
            realmResource.users().get(userId).remove();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar usuário no Keycloak", e);
        }
    }

    public void disableUser(String userId) {
        try {
            // Primeiro verifica se o usuário existe no Keycloak
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            if (user == null) {
                // Usuário não encontrado no Keycloak - isso é OK, pode ter sido removido manualmente
                return;
            }
            
            // Se o usuário já está desabilitado, não faz nada
            if (!user.isEnabled()) {
                return;
            }
            
            // Desabilita o usuário
            user.setEnabled(false);
            realmResource.users().get(userId).update(user);
            
        } catch (NotFoundException e) {
            // Usuário não encontrado no Keycloak - isso é OK para desativação
            // Não relança a exceção, apenas loga
            System.out.println("Usuário " + userId + " não encontrado no Keycloak - continuando com desativação local");
        } catch (Exception e) {
            // Outros erros são mais sérios
            throw new RuntimeException("Erro ao desabilitar usuário no Keycloak: " + e.getMessage(), e);
        }
    }

    public void enableUser(String userId) {
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            if (user == null) {
                throw new RuntimeException("Usuário não encontrado no Keycloak para reativação");
            }
            
            // Se o usuário já está habilitado, não faz nada
            if (user.isEnabled()) {
                return;
            }
            
            user.setEnabled(true);
            realmResource.users().get(userId).update(user);
            
        } catch (NotFoundException e) {
            throw new RuntimeException("Usuário não encontrado no Keycloak para reativação", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao habilitar usuário no Keycloak: " + e.getMessage(), e);
        }
    }

    public void updateUserRoles(String userId, List<String> newRoles) {
        try {
            var userResource = realmResource.users().get(userId);
            
            // Remover todas as roles atuais
            var currentRoles = userResource.roles().realmLevel().listAll();
            if (!currentRoles.isEmpty()) {
                userResource.roles().realmLevel().remove(currentRoles);
            }
            
            // Adicionar novas roles
            for (String roleName : newRoles) {
                var role = realmResource.roles().get(roleName).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(role));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar roles do usuário no Keycloak", e);
        }
    }
}