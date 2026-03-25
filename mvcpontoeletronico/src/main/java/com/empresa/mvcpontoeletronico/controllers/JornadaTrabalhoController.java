package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.entities.JornadaTrabalho;
import com.empresa.mvcpontoeletronico.repositories.JornadaTrabalhoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para gerenciamento de jornadas de trabalho
 * Arquitetura MVC: Camada de Apresentação (Controller)
 */
@RestController
@RequestMapping("/api/jornadas")
@RequiredArgsConstructor
@Slf4j
public class JornadaTrabalhoController {
    
    private final JornadaTrabalhoRepository jornadaRepository;
    
    /**
     * Lista todas as jornadas de trabalho
     */
    @GetMapping
    public ResponseEntity<List<JornadaTrabalho>> listarJornadas() {
        log.debug("GET /api/jornadas - Listando todas as jornadas");
        List<JornadaTrabalho> jornadas = jornadaRepository.findAllByOrderByHorasSemanaisAsc();
        return ResponseEntity.ok(jornadas);
    }
    
    /**
     * Busca jornada por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<JornadaTrabalho> buscarJornadaPorId(@PathVariable UUID id) {
        log.debug("GET /api/jornadas/{} - Buscando jornada por ID", id);
        return jornadaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Busca jornadas por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<JornadaTrabalho>> buscarJornadasPorNome(@RequestParam String nome) {
        log.debug("GET /api/jornadas/buscar?nome={} - Buscando jornadas por nome", nome);
        List<JornadaTrabalho> jornadas = jornadaRepository.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(jornadas);
    }
}