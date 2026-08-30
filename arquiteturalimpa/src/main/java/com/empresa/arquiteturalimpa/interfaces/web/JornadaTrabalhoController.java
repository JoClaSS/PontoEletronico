package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.application.jornada.JornadaTrabalhoService;
import com.empresa.arquiteturalimpa.domain.model.JornadaTrabalho;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para gerenciamento de jornadas de trabalho.
 */
@RestController
@RequestMapping("/api/jornadas")
@RequiredArgsConstructor
public class JornadaTrabalhoController {

    private final JornadaTrabalhoService jornadaService;

    @GetMapping
    public ResponseEntity<List<JornadaTrabalho>> listarJornadas() {
        return ResponseEntity.ok(jornadaService.listarJornadas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JornadaTrabalho> buscarJornadaPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(jornadaService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<JornadaTrabalho>> buscarJornadasPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(jornadaService.buscarPorNome(nome));
    }
}
