package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.application.ponto.PontoEletronicoService;
import com.empresa.arquiteturalimpa.application.solicitacao.SolicitacaoService;
import com.empresa.arquiteturalimpa.application.solicitacao.dto.CriarSolicitacaoRequest;
import com.empresa.arquiteturalimpa.application.solicitacao.dto.MotivoSolicitacaoResponse;
import com.empresa.arquiteturalimpa.application.solicitacao.dto.SolicitacaoResponse;
import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * Controller para gerenciamento de solicitações.
 */
@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final PontoEletronicoService pontoEletronicoService;

    @PostMapping
    public ResponseEntity<SolicitacaoResponse> criarSolicitacao(@Valid @RequestBody CriarSolicitacaoRequest request) {
        SolicitacaoResponse response = solicitacaoService.criarSolicitacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SolicitacaoResponse>> listarSolicitacoesPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(solicitacaoService.listarSolicitacoesPorUsuario(usuarioId));
    }

    @PostMapping(value = "/com-anexo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SolicitacaoResponse> criarSolicitacaoComAnexo(
            @RequestParam("usuarioId") UUID usuarioId,
            @RequestParam("dataReferencia") LocalDate dataReferencia,
            @RequestParam("motivoId") UUID motivoId,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "diasConsecutivos", required = false, defaultValue = "false") Boolean diasConsecutivos,
            @RequestParam(value = "quantidadeDias", required = false) Integer quantidadeDias,
            @RequestParam(value = "anexo", required = false) MultipartFile anexo) throws IOException {

        CriarSolicitacaoRequest.CriarSolicitacaoRequestBuilder builder = CriarSolicitacaoRequest.builder()
                .usuarioId(usuarioId)
                .dataReferencia(dataReferencia)
                .motivoId(motivoId)
                .descricao(descricao)
                .diasConsecutivos(diasConsecutivos)
                .quantidadeDias(quantidadeDias);

        if (anexo != null && !anexo.isEmpty()) {
            builder.anexoNome(anexo.getOriginalFilename())
                    .anexoTipo(anexo.getContentType())
                    .anexoTamanho(anexo.getSize())
                    .anexoConteudo(anexo.getBytes());
        }

        SolicitacaoResponse response = solicitacaoService.criarSolicitacao(builder.build());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/anexo")
    public ResponseEntity<byte[]> downloadAnexo(@PathVariable UUID id) {
        SolicitacaoResponse solicitacao = solicitacaoService.buscarSolicitacaoPorId(id);

        if (!solicitacao.getTemAnexo()) {
            return ResponseEntity.notFound().build();
        }

        byte[] anexoConteudo = solicitacaoService.buscarAnexo(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(solicitacao.getAnexoTipo()));
        headers.setContentDispositionFormData("attachment", solicitacao.getAnexoNome());
        headers.setContentLength(solicitacao.getAnexoTamanho());

        return ResponseEntity.ok().headers(headers).body(anexoConteudo);
    }

    @GetMapping("/motivos")
    public ResponseEntity<List<MotivoSolicitacaoResponse>> listarMotivos() {
        return ResponseEntity.ok(solicitacaoService.listarMotivosAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponse> buscarSolicitacao(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.buscarSolicitacaoPorId(id));
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<SolicitacaoResponse> resolverSolicitacao(@PathVariable UUID id,
                                                                    @RequestBody(required = false) Map<String, Object> dados) {
        if (dados == null || !dados.containsKey("observacao") ||
                dados.get("observacao") == null ||
                ((String) dados.get("observacao")).trim().isEmpty()) {
            throw new BusinessRuleException("Observação é obrigatória para resolver a solicitação");
        }

        SolicitacaoResponse solicitacao = solicitacaoService.buscarSolicitacaoPorId(id);

        if (dados.containsKey("pontos")) {
            if (Boolean.TRUE.equals(solicitacao.getDiasConsecutivos()) && solicitacao.getQuantidadeDias() != null && solicitacao.getQuantidadeDias() > 1) {
                for (int i = 0; i < solicitacao.getQuantidadeDias(); i++) {
                    LocalDate dataAtual = solicitacao.getDataReferencia().plusDays(i);
                    pontoEletronicoService.atualizarPontosPorData(solicitacao.getUsuarioId(), dataAtual, dados);
                }
            } else {
                pontoEletronicoService.atualizarPontosPorData(solicitacao.getUsuarioId(), solicitacao.getDataReferencia(), dados);
            }
        }

        SolicitacaoResponse response = solicitacaoService.atualizarStatus(id, StatusSolicitacao.RESOLVIDO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SolicitacaoResponse> cancelarSolicitacao(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.cancelarSolicitacao(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoResponse> atualizarStatus(@PathVariable UUID id,
                                                                @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        if (statusStr == null) {
            throw new BusinessRuleException("Status é obrigatório");
        }

        StatusSolicitacao status = StatusSolicitacao.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(solicitacaoService.atualizarStatus(id, status));
    }

    @GetMapping("/contagem/abertas")
    public ResponseEntity<Map<String, Long>> contarSolicitacoesEmAberto() {
        return ResponseEntity.ok(Map.of("quantidade", solicitacaoService.contarSolicitacoesEmAberto()));
    }

    @GetMapping("/recente/aberta")
    public ResponseEntity<Map<String, Object>> buscarSolicitacaoMaisRecenteAberta() {
        SolicitacaoResponse solicitacao = solicitacaoService.buscarSolicitacaoMaisRecenteAberta();
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("solicitacao", solicitacao);
        return ResponseEntity.ok(body);
    }
}
