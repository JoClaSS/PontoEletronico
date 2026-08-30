package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.application.usuario.UsuarioService;
import com.empresa.arquiteturalimpa.application.usuario.dto.CriarUsuarioRequest;
import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para gerenciamento de usuários.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/funcionarios")
    public ResponseEntity<List<UsuarioResponse>> listarFuncionarios() {
        return ResponseEntity.ok(usuarioService.listarFuncionariosAtivos());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleType>> listarRoles() {
        return ResponseEntity.ok(Arrays.asList(RoleType.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponse>> buscarUsuariosPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(usuarioService.buscarPorNome(nome));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        UsuarioResponse usuarioCriado = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(@PathVariable UUID id,
                                                             @Valid @RequestBody CriarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desativarUsuario(@PathVariable UUID id) {
        usuarioService.desativarUsuario(id);
        return ResponseEntity.ok().body(Map.of("message", "Usuário desativado com sucesso"));
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<?> reativarUsuario(@PathVariable UUID id) {
        usuarioService.reativarUsuario(id);
        return ResponseEntity.ok().body(Map.of("message", "Usuário reativado com sucesso"));
    }
}
