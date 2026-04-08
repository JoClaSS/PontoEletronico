package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.AtualizarConfiguracaoRequest;
import com.empresa.mvcpontoeletronico.dtos.ConfiguracaoEmpresaResponse;
import com.empresa.mvcpontoeletronico.services.ConfiguracaoEmpresaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller para gerenciamento das configurações da empresa
 * Arquitetura MVC: Camada de Apresentação (Controller)
 */
@RestController
@RequestMapping("/api/configuracoes")
@RequiredArgsConstructor
@Slf4j
public class ConfiguracaoEmpresaController {
    
    private final ConfiguracaoEmpresaService configuracaoService;
    
    /**
     * Busca as configurações da empresa
     * Endpoint acessível por todos os usuários autenticados
     */
    @GetMapping
    public ResponseEntity<ConfiguracaoEmpresaResponse> obterConfiguracoes() {
        log.debug("GET /api/configuracoes - Buscando configurações da empresa");
        ConfiguracaoEmpresaResponse configuracoes = configuracaoService.obterConfiguracoes();
        return ResponseEntity.ok(configuracoes);
    }
    
    /**
     * Salva ou atualiza as configurações da empresa
     * Restrito apenas para usuários ADMIN ou MASTER
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER')")
    public ResponseEntity<ConfiguracaoEmpresaResponse> salvarConfiguracoes(
            @Valid @RequestBody AtualizarConfiguracaoRequest request) {
        
        log.debug("POST /api/configuracoes - Salvando configurações da empresa");
        log.debug("Dados recebidos: nomeEmpresa={}, horarioCheckin={}, horarioCheckout={}", 
                request.getNomeEmpresa(), request.getHorarioCheckin(), request.getHorarioCheckout());
        
        try {
            ConfiguracaoEmpresaResponse configuracoesSalvas = configuracaoService.salvarConfiguracoes(request);
            log.debug("Configurações salvas com sucesso: {}", configuracoesSalvas.getId());
            return ResponseEntity.ok(configuracoesSalvas);
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao salvar configurações: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Erro de integridade de dados: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            
        } catch (org.springframework.transaction.TransactionSystemException e) {
            log.error("Erro de transação/validação: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            
        } catch (Exception e) {
            log.error("Erro interno ao salvar configurações", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Atualiza as configurações da empresa (método PUT para seguir padrões REST)
     * Restrito apenas para usuários ADMIN ou MASTER
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MASTER')")
    public ResponseEntity<ConfiguracaoEmpresaResponse> atualizarConfiguracoes(
            @Valid @RequestBody AtualizarConfiguracaoRequest request) {
        
        log.debug("PUT /api/configuracoes - Atualizando configurações da empresa");
        
        try {
            ConfiguracaoEmpresaResponse configuracoesSalvas = configuracaoService.salvarConfiguracoes(request);
            return ResponseEntity.ok(configuracoesSalvas);
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar configurações: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            
        } catch (Exception e) {
            log.error("Erro interno ao atualizar configurações", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}