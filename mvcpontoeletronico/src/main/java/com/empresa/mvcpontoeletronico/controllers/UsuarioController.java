package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.CriarUsuarioRequest;
import com.empresa.mvcpontoeletronico.dtos.UsuarioResponse;
import com.empresa.mvcpontoeletronico.entities.RoleType;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;

/**
 * Controller para gerenciamento de usuários
 * Arquitetura MVC: Camada de Apresentação (Controller)
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * Lista todos os usuários
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        log.debug("GET /api/usuarios - Listando todos os usuários");
        List<UsuarioResponse> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    /**
     * Retorna as roles disponíveis
     */
    @GetMapping("/roles")
    public ResponseEntity<List<RoleType>> listarRoles() {
        log.debug("GET /api/usuarios/roles - Listando roles disponíveis");
        return ResponseEntity.ok(Arrays.asList(RoleType.values()));
    }
    
    /**
     * Busca usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable UUID id) {
        log.debug("GET /api/usuarios/{} - Buscando usuário por ID", id);
        return usuarioService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Busca usuários por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuariosPorNome(@RequestParam String nome) {
        log.debug("GET /api/usuarios/buscar?nome={} - Buscando usuários por nome", nome);
        List<Usuario> usuarios = usuarioService.buscarPorNome(nome);
        return ResponseEntity.ok(usuarios);
    }
    
    /**
     * Busca usuário por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@PathVariable String email) {
        log.debug("GET /api/usuarios/email/{} - Buscando usuário por email", email);
        return usuarioService.buscarPorEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Cria um novo usuário
     */
    @PostMapping
    public ResponseEntity<?> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        log.info("POST /api/usuarios - Tentando criar usuário: {} ({})", request.getNome(), request.getEmail());
        log.debug("Request completo recebido: nome='{}', email='{}', senha='{}', cpf='{}', role='{}'", 
                request.getNome(), request.getEmail(), 
                request.getSenha() != null ? "[PRESENTE]" : "[NULL]", 
                request.getCpf(), request.getRole());
        
        try {
            UsuarioResponse usuarioCriado = usuarioService.criarUsuario(request);
            log.info("Usuário criado com sucesso: {} (ID: {})", usuarioCriado.getNome(), usuarioCriado.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao criar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Erro ao criar usuário: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado ao criar usuário: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Atualiza um usuário existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable UUID id, 
                                             @Valid @RequestBody CriarUsuarioRequest request) {
        log.debug("PUT /api/usuarios/{} - Atualizando usuário", id);
        try {
            UsuarioResponse usuarioAtualizado = usuarioService.atualizar(id, request);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            log.warn("Erro ao atualizar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Desativa um usuário (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desativarUsuario(@PathVariable UUID id) {
        log.debug("DELETE /api/usuarios/{} - Desativando usuário", id);
        try {
            usuarioService.desativarUsuario(id);
            return ResponseEntity.ok().body(Map.of("message", "Usuário desativado com sucesso"));
        } catch (RuntimeException e) {
            log.warn("Erro ao desativar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Reativa um usuário
     */
    @PutMapping("/{id}/reativar")
    public ResponseEntity<?> reativarUsuario(@PathVariable UUID id) {
        log.debug("PUT /api/usuarios/{}/reativar - Reativando usuário", id);
        try {
            usuarioService.reativarUsuario(id);
            return ResponseEntity.ok().body(Map.of("message", "Usuário reativado com sucesso"));
        } catch (RuntimeException e) {
            log.warn("Erro ao reativar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Classe para resposta de erro
     */
    public static class ErrorResponse {
        public final String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}