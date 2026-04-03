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
        log.debug("POST /api/usuarios - Criando novo usuário: {}", request.getNome());
        try {
            UsuarioResponse usuarioCriado = usuarioService.criarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
        } catch (RuntimeException e) {
            log.warn("Erro ao criar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
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
     * Remove um usuário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerUsuario(@PathVariable UUID id) {
        log.debug("DELETE /api/usuarios/{} - Removendo usuário", id);
        try {
            usuarioService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Erro ao remover usuário: {}", e.getMessage());
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