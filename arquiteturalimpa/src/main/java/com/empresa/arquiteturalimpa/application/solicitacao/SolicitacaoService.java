package com.empresa.arquiteturalimpa.application.solicitacao;

import com.empresa.arquiteturalimpa.application.solicitacao.dto.CriarSolicitacaoRequest;
import com.empresa.arquiteturalimpa.application.solicitacao.dto.MotivoSolicitacaoResponse;
import com.empresa.arquiteturalimpa.application.solicitacao.dto.SolicitacaoResponse;
import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import com.empresa.arquiteturalimpa.domain.exception.EntityNotFoundException;
import com.empresa.arquiteturalimpa.domain.model.MotivoSolicitacao;
import com.empresa.arquiteturalimpa.domain.model.Solicitacao;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.MotivoSolicitacaoRepository;
import com.empresa.arquiteturalimpa.domain.repository.SolicitacaoRepository;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final MotivoSolicitacaoRepository motivoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public SolicitacaoResponse criarSolicitacao(CriarSolicitacaoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        boolean jaExisteSolicitacaoAberta = solicitacaoRepository.existsByUsuarioIdAndDataReferenciaAndStatus(
                request.getUsuarioId(), request.getDataReferencia(), StatusSolicitacao.ABERTO);

        if (jaExisteSolicitacaoAberta) {
            throw new BusinessRuleException("Já existe uma solicitação aberta para esta data. Resolva ou cancele a solicitação existente antes de criar uma nova.");
        }

        MotivoSolicitacao motivo = motivoRepository.findById(request.getMotivoId())
                .orElseThrow(() -> new EntityNotFoundException("Motivo não encontrado"));

        if (motivo.getRequerAnexo() && (request.getAnexoConteudo() == null || request.getAnexoConteudo().length == 0)) {
            throw new BusinessRuleException("Este motivo requer anexo obrigatório");
        }

        Solicitacao.SolicitacaoBuilder builder = Solicitacao.builder()
                .dataReferencia(request.getDataReferencia())
                .usuario(usuario)
                .motivo(motivo)
                .descricao(request.getDescricao())
                .diasConsecutivos(request.getDiasConsecutivos() != null ? request.getDiasConsecutivos() : false)
                .quantidadeDias(request.getQuantidadeDias())
                .status(StatusSolicitacao.ABERTO);

        if (request.getAnexoConteudo() != null && request.getAnexoConteudo().length > 0) {
            builder.anexoNome(request.getAnexoNome())
                    .anexoTipo(request.getAnexoTipo())
                    .anexoTamanho(request.getAnexoTamanho())
                    .anexoConteudo(request.getAnexoConteudo());
        }

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(builder.build());
        log.info("Solicitação criada com sucesso - ID: {}, Usuário: {}", solicitacaoSalva.getId(), usuario.getNome());
        return mapToResponse(solicitacaoSalva);
    }

    public List<SolicitacaoResponse> listarSolicitacoesPorUsuario(UUID usuarioId) {
        return solicitacaoRepository.findByUsuarioIdOrderByDataReferenciaDesc(usuarioId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<MotivoSolicitacaoResponse> listarMotivosAtivos() {
        return motivoRepository.findAllAtivos().stream()
                .map(this::mapMotivoToResponse)
                .toList();
    }

    public SolicitacaoResponse buscarSolicitacaoPorId(UUID id) {
        return solicitacaoRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));
    }

    @Transactional
    public SolicitacaoResponse atualizarStatus(UUID id, StatusSolicitacao novoStatus) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));

        solicitacao.setStatus(novoStatus);
        Solicitacao solicitacaoAtualizada = solicitacaoRepository.save(solicitacao);
        log.info("Status da solicitação {} atualizado para {}", id, novoStatus);
        return mapToResponse(solicitacaoAtualizada);
    }

    public byte[] buscarAnexo(UUID id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));

        if (solicitacao.getAnexoConteudo() == null) {
            throw new BusinessRuleException("Esta solicitação não possui anexo");
        }
        return solicitacao.getAnexoConteudo();
    }

    @Transactional
    public SolicitacaoResponse cancelarSolicitacao(UUID id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacao.ABERTO) {
            throw new BusinessRuleException("Apenas solicitações em aberto podem ser canceladas");
        }

        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        Solicitacao solicitacaoAtualizada = solicitacaoRepository.save(solicitacao);
        log.info("Solicitação {} cancelada com sucesso", id);
        return mapToResponse(solicitacaoAtualizada);
    }

    public Long contarSolicitacoesEmAberto() {
        return solicitacaoRepository.countByStatus(StatusSolicitacao.ABERTO);
    }

    public SolicitacaoResponse buscarSolicitacaoMaisRecenteAberta() {
        return solicitacaoRepository.findTop1ByStatusOrderByDataReferenciaDesc(StatusSolicitacao.ABERTO)
                .map(this::mapToResponse)
                .orElse(null);
    }

    private SolicitacaoResponse mapToResponse(Solicitacao solicitacao) {
        return SolicitacaoResponse.builder()
                .id(solicitacao.getId())
                .dataReferencia(solicitacao.getDataReferencia())
                .usuarioId(solicitacao.getUsuario().getId())
                .nomeUsuario(solicitacao.getUsuario().getNome())
                .motivo(SolicitacaoResponse.MotivoResponse.builder()
                        .id(solicitacao.getMotivo().getId())
                        .descricao(solicitacao.getMotivo().getDescricao())
                        .build())
                .descricao(solicitacao.getDescricao())
                .status(solicitacao.getStatus())
                .statusDescricao(solicitacao.getStatus().getDescricao())
                .anexoNome(solicitacao.getAnexoNome())
                .anexoTipo(solicitacao.getAnexoTipo())
                .anexoTamanho(solicitacao.getAnexoTamanho())
                .temAnexo(solicitacao.temAnexo())
                .diasConsecutivos(solicitacao.getDiasConsecutivos())
                .quantidadeDias(solicitacao.getQuantidadeDias())
                .createdAt(solicitacao.getCreatedAt())
                .updatedAt(solicitacao.getUpdatedAt())
                .build();
    }

    private MotivoSolicitacaoResponse mapMotivoToResponse(MotivoSolicitacao motivo) {
        return MotivoSolicitacaoResponse.builder()
                .id(motivo.getId())
                .descricao(motivo.getDescricao())
                .ativo(motivo.getAtivo())
                .requerAnexo(motivo.getRequerAnexo())
                .createdAt(motivo.getCreatedAt())
                .build();
    }
}
