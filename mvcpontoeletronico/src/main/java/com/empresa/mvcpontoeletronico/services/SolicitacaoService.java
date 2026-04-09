package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.*;
import com.empresa.mvcpontoeletronico.entities.MotivoSolicitacao;
import com.empresa.mvcpontoeletronico.entities.Solicitacao;
import com.empresa.mvcpontoeletronico.entities.StatusSolicitacao;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.MotivoSolicitacaoRepository;
import com.empresa.mvcpontoeletronico.repositories.SolicitacaoRepository;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de solicitações
 * Arquitetura MVC: Camada de Negócio (Service) 
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SolicitacaoService {
    
    private final SolicitacaoRepository solicitacaoRepository;
    private final MotivoSolicitacaoRepository motivoRepository;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Cria uma nova solicitação
     */
    @Transactional
    public SolicitacaoResponse criarSolicitacao(CriarSolicitacaoRequest request) {
        log.debug("Criando solicitação para usuário: {}", request.getUsuarioId());
        
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Verifica se já existe uma solicitação aberta para esta data
        boolean jaExisteSolicitacaoAberta = solicitacaoRepository.existsByUsuarioIdAndDataReferenciaAndStatus(
            request.getUsuarioId(), 
            request.getDataReferencia(), 
            StatusSolicitacao.ABERTO
        );
        
        if (jaExisteSolicitacaoAberta) {
            throw new IllegalArgumentException("Já existe uma solicitação aberta para esta data. Resolva ou cancele a solicitação existente antes de criar uma nova.");
        }
        
        // Busca o motivo
        MotivoSolicitacao motivo = motivoRepository.findById(request.getMotivoId())
            .orElseThrow(() -> new IllegalArgumentException("Motivo não encontrado"));
        
        // Valida se anexo é obrigatório
        if (motivo.getRequerAnexo() && (request.getAnexoConteudo() == null || request.getAnexoConteudo().length == 0)) {
            throw new IllegalArgumentException("Este motivo requer anexo obrigatório");
        }
        
        // Cria a solicitação
        Solicitacao.SolicitacaoBuilder builder = Solicitacao.builder()
            .dataReferencia(request.getDataReferencia())
            .usuario(usuario)
            .motivo(motivo)
            .descricao(request.getDescricao())
            .diasConsecutivos(request.getDiasConsecutivos() != null ? request.getDiasConsecutivos() : false)
            .quantidadeDias(request.getQuantidadeDias())
            .status(StatusSolicitacao.ABERTO);
        
        // Adiciona anexo se fornecido
        if (request.getAnexoConteudo() != null && request.getAnexoConteudo().length > 0) {
            builder.anexoNome(request.getAnexoNome())
                   .anexoTipo(request.getAnexoTipo())
                   .anexoTamanho(request.getAnexoTamanho())
                   .anexoConteudo(request.getAnexoConteudo());
        }
        
        Solicitacao solicitacao = builder.build();
        
        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);
        log.info("Solicitação criada com sucesso - ID: {}, Usuário: {}", 
                 solicitacaoSalva.getId(), usuario.getNome());
        
        return mapToResponse(solicitacaoSalva);
    }
    
    /**
     * Lista todas as solicitações de um usuário
     */
    public List<SolicitacaoResponse> listarSolicitacoesPorUsuario(UUID usuarioId) {
        log.debug("Listando solicitações do usuário: {}", usuarioId);
        
        List<Solicitacao> solicitacoes = solicitacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
        return solicitacoes.stream()
                          .map(this::mapToResponse)
                          .collect(Collectors.toList());
    }
    
    /**
     * Lista todos os motivos ativos
     */
    public List<MotivoSolicitacaoResponse> listarMotivosAtivos() {
        log.debug("Listando motivos ativos");
        
        List<MotivoSolicitacao> motivos = motivoRepository.findAllAtivos();
        return motivos.stream()
                     .map(this::mapMotivoToResponse)
                     .collect(Collectors.toList());
    }
    
    /**
     * Busca uma solicitação por ID
     */
    public SolicitacaoResponse buscarSolicitacaoPorId(UUID id) {
        log.debug("Buscando solicitação por ID: {}", id);
        
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        
        return mapToResponse(solicitacao);
    }
    
    /**
     * Atualiza o status de uma solicitação
     */
    @Transactional
    public SolicitacaoResponse atualizarStatus(UUID id, StatusSolicitacao novoStatus) {
        log.debug("Atualizando status da solicitação {} para {}", id, novoStatus);
        
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        
        solicitacao.setStatus(novoStatus);
        Solicitacao solicitacaoAtualizada = solicitacaoRepository.save(solicitacao);
        
        log.info("Status da solicitação {} atualizado para {}", id, novoStatus);
        return mapToResponse(solicitacaoAtualizada);
    }
    
    /**
     * Busca anexo de uma solicitação por ID
     */
    public byte[] buscarAnexo(UUID id) {
        log.debug("Buscando anexo da solicitação: {}", id);
        
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        
        if (solicitacao.getAnexoConteudo() == null) {
            throw new IllegalArgumentException("Esta solicitação não possui anexo");
        }
        
        return solicitacao.getAnexoConteudo();
    }
    
    /**
     * Cancela uma solicitação por ID (muda status para CANCELADO)
     */
    @Transactional
    public SolicitacaoResponse cancelarSolicitacao(UUID id) {
        log.debug("Cancelando solicitação: {}", id);
        
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        
        // Validação de negócio: só pode cancelar solicitações em aberto
        if (solicitacao.getStatus() != StatusSolicitacao.ABERTO) {
            throw new IllegalArgumentException("Apenas solicitações em aberto podem ser canceladas");
        }
        
        // Altera status para CANCELADO
        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        Solicitacao solicitacaoAtualizada = solicitacaoRepository.save(solicitacao);
        
        log.info("Solicitação {} cancelada com sucesso", id);
        return mapToResponse(solicitacaoAtualizada);
    }
    
    /**
     * Mapeia Solicitacao para SolicitacaoResponse
     */
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
            .temAnexo(solicitacao.getAnexoConteudo() != null && solicitacao.getAnexoConteudo().length > 0)
            .diasConsecutivos(solicitacao.getDiasConsecutivos())
            .quantidadeDias(solicitacao.getQuantidadeDias())
            .createdAt(solicitacao.getCreatedAt())
            .updatedAt(solicitacao.getUpdatedAt())
            .build();
    }
    
    /**
     * Mapeia MotivoSolicitacao para MotivoSolicitacaoResponse
     */
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