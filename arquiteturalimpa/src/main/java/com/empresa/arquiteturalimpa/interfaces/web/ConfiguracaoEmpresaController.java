package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.application.configuracao.ConfiguracaoEmpresaService;
import com.empresa.arquiteturalimpa.application.configuracao.dto.AtualizarConfiguracaoRequest;
import com.empresa.arquiteturalimpa.application.configuracao.dto.ConfiguracaoEmpresaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerenciamento das configurações da empresa.
 */
@RestController
@RequestMapping("/api/configuracoes")
@RequiredArgsConstructor
public class ConfiguracaoEmpresaController {

    private final ConfiguracaoEmpresaService configuracaoService;

    @GetMapping
    public ResponseEntity<ConfiguracaoEmpresaResponse> obterConfiguracoes() {
        return ResponseEntity.ok(configuracaoService.obterConfiguracoes());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER')")
    public ResponseEntity<ConfiguracaoEmpresaResponse> salvarConfiguracoes(@Valid @RequestBody AtualizarConfiguracaoRequest request) {
        return ResponseEntity.ok(configuracaoService.salvarConfiguracoes(request));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER')")
    public ResponseEntity<ConfiguracaoEmpresaResponse> atualizarConfiguracoes(@Valid @RequestBody AtualizarConfiguracaoRequest request) {
        return ResponseEntity.ok(configuracaoService.salvarConfiguracoes(request));
    }
}
