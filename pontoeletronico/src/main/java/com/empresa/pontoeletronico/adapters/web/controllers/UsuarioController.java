package com.empresa.pontoeletronico.adapters.web.controllers;

import com.empresa.pontoeletronico.domain.entities.Usuario;
import com.empresa.pontoeletronico.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST para operações de usuários
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioRepository usuarioRepository;
    
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/ativos")
    public ResponseEntity<List<Usuario>> listarAtivos() {
        List<Usuario> usuarios = usuarioRepository.listarAtivos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable String id) {
        Optional<Usuario> usuario = usuarioRepository.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioRepository.buscarPorEmail(email);
        return usuario.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}