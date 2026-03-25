package com.empresa.mvcpontoeletronico.controllers;

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
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        log.debug("GET /api/usuarios - Listando todos os usuários");
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
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
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) {
        log.debug("POST /api/usuarios - Criando novo usuário: {}", usuario.getNome());
        try {
            Usuario usuarioCriado = usuarioService.salvar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao criar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Atualiza um usuário existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable UUID id, 
                                                   @Valid @RequestBody Usuario usuario) {
        log.debug("PUT /api/usuarios/{} - Atualizando usuário", id);
        try {
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao atualizar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Remove um usuário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable UUID id) {
        log.debug("DELETE /api/usuarios/{} - Removendo usuário", id);
        try {
            usuarioService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao remover usuário: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}