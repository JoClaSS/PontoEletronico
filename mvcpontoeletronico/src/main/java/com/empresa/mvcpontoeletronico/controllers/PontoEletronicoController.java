package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.PontoEletronicoResponse;
import com.empresa.mvcpontoeletronico.dtos.RegistrarPontoRequest;
import com.empresa.mvcpontoeletronico.dtos.RelatorioHorasResponse;
import com.empresa.mvcpontoeletronico.services.PontoEletronicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller principal para gerenciamento de ponto eletrônico  
 * Arquitetura MVC: Camada de Apresentação (Controller)
 */
@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
@Slf4j
public class PontoEletronicoController {
    
    private final PontoEletronicoService pontoService;
    
    /**
     * Registra um novo ponto
     * POST /api/pontos
     */
    @PostMapping
    public ResponseEntity<?> registrarPonto(@Valid @RequestBody RegistrarPontoRequest request) {
        log.debug("POST /api/pontos - Registrando ponto para usuário: {}", request.getUsuarioId());
        try {
            PontoEletronicoResponse response = pontoService.registrarPonto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao registrar ponto: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Consulta pontos por usuário e data  
     * GET /api/pontos/usuario/{usuarioId}?data=2024-03-15
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PontoEletronicoResponse>> consultarPontosPorData(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        
        log.debug("GET /api/pontos/usuario/{} - Consultando pontos para data: {}", usuarioId, data);
        List<PontoEletronicoResponse> pontos = pontoService.consultarPontosPorData(usuarioId, data);
        return ResponseEntity.ok(pontos);
    }
    
    /**
     * Consulta pontos por usuário e período
     * GET /api/pontos/usuario/{usuarioId}/periodo?dataInicio=2024-03-01&dataFim=2024-03-31
     */
    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<?> consultarPontosPorPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        log.debug("GET /api/pontos/usuario/{}/periodo - Período: {} a {}", usuarioId, dataInicio, dataFim);
        try {
            List<PontoEletronicoResponse> pontos = pontoService.consultarPontosPorPeriodo(usuarioId, dataInicio, dataFim);
            return ResponseEntity.ok(pontos);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao consultar pontos por período: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gera relatório de horas trabalhadas
     * GET /api/pontos/usuario/{usuarioId}/relatorio?dataInicio=2024-03-01&dataFim=2024-03-31
     */
    @GetMapping("/usuario/{usuarioId}/relatorio")
    public ResponseEntity<?> gerarRelatorioHoras(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,  
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        log.debug("GET /api/pontos/usuario/{}/relatorio - Período: {} a {}", usuarioId, dataInicio, dataFim);
        try {
            RelatorioHorasResponse relatorio = pontoService.gerarRelatorioHoras(usuarioId, dataInicio, dataFim);
            return ResponseEntity.ok(relatorio);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao gerar relatório: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Remove um registro de ponto
     * DELETE /api/pontos/{pontoId}
     */
    @DeleteMapping("/{pontoId}")
    public ResponseEntity<?> removerPonto(@PathVariable UUID pontoId) {
        log.debug("DELETE /api/pontos/{} - Removendo ponto", pontoId);
        try {
            pontoService.removerPonto(pontoId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao remover ponto: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}