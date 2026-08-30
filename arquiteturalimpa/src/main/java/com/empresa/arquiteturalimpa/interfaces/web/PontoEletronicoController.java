package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.application.ponto.PontoEletronicoService;
import com.empresa.arquiteturalimpa.application.ponto.dto.PontoEletronicoResponse;
import com.empresa.arquiteturalimpa.application.ponto.dto.RegistrarPontoRequest;
import com.empresa.arquiteturalimpa.application.ponto.dto.RelatorioHorasResponse;
import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller principal para gerenciamento de ponto eletrônico.
 */
@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
public class PontoEletronicoController {

    private final PontoEletronicoService pontoService;

    @PostMapping
    public ResponseEntity<PontoEletronicoResponse> registrarPonto(@Valid @RequestBody RegistrarPontoRequest request) {
        PontoEletronicoResponse response = pontoService.registrarPonto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PontoEletronicoResponse>> consultarPontosPorData(
            @PathVariable UUID usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        LocalDate dataConsulta = data != null ? data : LocalDate.now();
        return ResponseEntity.ok(pontoService.consultarPontosPorData(usuarioId, dataConsulta));
    }

    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<PontoEletronicoResponse>> consultarPontosPorPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return ResponseEntity.ok(pontoService.consultarPontosPorPeriodo(usuarioId, dataInicio, dataFim));
    }

    @GetMapping("/usuario/{usuarioId}/relatorio")
    public ResponseEntity<RelatorioHorasResponse> gerarRelatorioHoras(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return ResponseEntity.ok(pontoService.gerarRelatorioHoras(usuarioId, dataInicio, dataFim));
    }

    @PutMapping("/usuario/{usuarioId}/data/{data}")
    public ResponseEntity<?> atualizarPontosPorData(
            @PathVariable UUID usuarioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestBody Map<String, Object> dadosAtualizacao) {

        pontoService.atualizarPontosPorData(usuarioId, data, dadosAtualizacao);
        return ResponseEntity.ok().body(Map.of("message", "Pontos atualizados com sucesso"));
    }

    @DeleteMapping("/{pontoId}")
    public ResponseEntity<?> removerPonto(@PathVariable UUID pontoId) {
        pontoService.removerPonto(pontoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/presenca")
    public ResponseEntity<List<UsuarioResponse>> listarPresencaDodia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        LocalDate dataConsulta = data != null ? data : LocalDate.now();
        return ResponseEntity.ok(pontoService.listarUsuariosComPontoNaData(dataConsulta));
    }
}
