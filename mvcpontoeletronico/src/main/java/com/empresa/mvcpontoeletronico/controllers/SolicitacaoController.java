package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.CriarSolicitacaoRequest;
import com.empresa.mvcpontoeletronico.dtos.MotivoSolicitacaoResponse;
import com.empresa.mvcpontoeletronico.dtos.SolicitacaoResponse;
import com.empresa.mvcpontoeletronico.entities.StatusSolicitacao;
import com.empresa.mvcpontoeletronico.services.SolicitacaoService;
import com.empresa.mvcpontoeletronico.services.PontoEletronicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para gerenciamento de solicitações
 * Arquitetura MVC: Camada de Apresentação (Controller)
 */
@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
@Slf4j
public class SolicitacaoController {
    
    private final SolicitacaoService solicitacaoService;
    private final PontoEletronicoService pontoEletronicoService;
    
    /**
     * Cria uma nova solicitação
     * POST /api/solicitacoes
     */
    @PostMapping
    public ResponseEntity<?> criarSolicitacao(@Valid @RequestBody CriarSolicitacaoRequest request) {
        log.debug("POST /api/solicitacoes - Criando solicitação para usuário: {}", request.getUsuarioId());
        try {
            SolicitacaoResponse response = solicitacaoService.criarSolicitacao(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao criar solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Lista solicitações por usuário
     * GET /api/solicitacoes/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SolicitacaoResponse>> listarSolicitacoesPorUsuario(@PathVariable UUID usuarioId) {
        log.debug("GET /api/solicitacoes/usuario/{} - Listando solicitações", usuarioId);
        List<SolicitacaoResponse> solicitacoes = solicitacaoService.listarSolicitacoesPorUsuario(usuarioId);
        return ResponseEntity.ok(solicitacoes);
    }
    
    /**
     * Cria uma nova solicitação com anexo
     * POST /api/solicitacoes/com-anexo
     */
    @PostMapping(value = "/com-anexo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> criarSolicitacaoComAnexo(
            @RequestParam("usuarioId") UUID usuarioId,
            @RequestParam("dataReferencia") LocalDate dataReferencia,
            @RequestParam("motivoId") UUID motivoId,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "diasConsecutivos", required = false, defaultValue = "false") Boolean diasConsecutivos,
            @RequestParam(value = "quantidadeDias", required = false) Integer quantidadeDias,
            @RequestParam(value = "anexo", required = false) MultipartFile anexo) {
        log.debug("POST /api/solicitacoes/com-anexo - Criando solicitação com anexo para usuário: {}", usuarioId);
        try {
            CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
                .usuarioId(usuarioId)
                .dataReferencia(dataReferencia)
                .motivoId(motivoId)
                .descricao(descricao)
                .diasConsecutivos(diasConsecutivos)
                .quantidadeDias(quantidadeDias)
                .build();
            
            // Adiciona anexo se fornecido
            if (anexo != null && !anexo.isEmpty()) {
                request.setAnexoNome(anexo.getOriginalFilename());
                request.setAnexoTipo(anexo.getContentType());
                request.setAnexoTamanho(anexo.getSize());
                request.setAnexoConteudo(anexo.getBytes());
            }
            
            SolicitacaoResponse response = solicitacaoService.criarSolicitacao(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao criar solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Erro ao processar anexo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao processar anexo"));
        }
    }
    
    /**
     * Faz download do anexo de uma solicitação
     * GET /api/solicitacoes/{id}/anexo
     */
    @GetMapping("/{id}/anexo")
    public ResponseEntity<?> downloadAnexo(@PathVariable UUID id) {
        log.debug("GET /api/solicitacoes/{}/anexo - Download de anexo", id);
        try {
            // Busca a solicitação para obter os metadados do anexo
            SolicitacaoResponse solicitacao = solicitacaoService.buscarSolicitacaoPorId(id);
            
            if (!solicitacao.getTemAnexo()) {
                return ResponseEntity.notFound().build();
            }
            
            // Busca o conteúdo do anexo
            byte[] anexoConteudo = solicitacaoService.buscarAnexo(id);
            
            // Define cabeçalhos para download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(solicitacao.getAnexoTipo()));
            headers.setContentDispositionFormData("attachment", solicitacao.getAnexoNome());
            headers.setContentLength(solicitacao.getAnexoTamanho());
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(anexoConteudo);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao buscar anexo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Lista motivos disponíveis
     * GET /api/solicitacoes/motivos
     */
    @GetMapping("/motivos")
    public ResponseEntity<List<MotivoSolicitacaoResponse>> listarMotivos() {
        log.debug("GET /api/solicitacoes/motivos - Listando motivos");
        List<MotivoSolicitacaoResponse> motivos = solicitacaoService.listarMotivosAtivos();
        return ResponseEntity.ok(motivos);
    }
    
    /**
     * Busca uma solicitação por ID
     * GET /api/solicitacoes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarSolicitacao(@PathVariable UUID id) {
        log.debug("GET /api/solicitacoes/{} - Buscando solicitação", id);
        try {
            SolicitacaoResponse solicitacao = solicitacaoService.buscarSolicitacaoPorId(id);
            return ResponseEntity.ok(solicitacao);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao buscar solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Atualiza o status de uma solicitação para RESOLVIDO
     * PUT /api/solicitacoes/{id}/resolver
     */
    @PutMapping("/{id}/resolver")
    public ResponseEntity<?> resolverSolicitacao(@PathVariable UUID id,
                                               @RequestBody(required = false) Map<String, Object> dados) {
        log.debug("PUT /api/solicitacoes/{}/resolver - Resolvendo solicitação", id);
        try {
            // Validação: observação é obrigatória
            if (dados == null || !dados.containsKey("observacao") || 
                dados.get("observacao") == null || 
                ((String) dados.get("observacao")).trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Observação é obrigatória para resolver a solicitação"));
            }
            
            // Busca a solicitação primeiro para obter dados do usuário e data
            SolicitacaoResponse solicitacao = solicitacaoService.buscarSolicitacaoPorId(id);
            
            // Se foram fornecidos dados de pontos, atualiza/cria os pontos primeiro
            if (dados.containsKey("pontos")) {
                pontoEletronicoService.atualizarPontosPorData(
                    solicitacao.getUsuarioId(),
                    solicitacao.getDataReferencia(), 
                    dados
                );
            }
            
            // Resolve a solicitação
            SolicitacaoResponse response = solicitacaoService.atualizarStatus(id, StatusSolicitacao.RESOLVIDO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao resolver solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Cancela uma solicitação (altera status para CANCELADO)
     * DELETE /api/solicitacoes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarSolicitacao(@PathVariable UUID id) {
        log.debug("DELETE /api/solicitacoes/{} - Cancelando solicitação", id);
        try {
            SolicitacaoResponse response = solicitacaoService.cancelarSolicitacao(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao cancelar solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Atualiza o status de uma solicitação
     * PATCH /api/solicitacoes/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable UUID id, 
                                           @RequestBody Map<String, String> request) {
        log.debug("PATCH /api/solicitacoes/{}/status - Atualizando status", id);
        try {
            String statusStr = request.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status é obrigatório"));
            }
            
            StatusSolicitacao status = StatusSolicitacao.valueOf(statusStr.toUpperCase());
            SolicitacaoResponse response = solicitacaoService.atualizarStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao atualizar status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}