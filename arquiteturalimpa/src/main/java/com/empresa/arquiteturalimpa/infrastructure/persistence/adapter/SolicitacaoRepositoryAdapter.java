package com.empresa.arquiteturalimpa.infrastructure.persistence.adapter;

import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import com.empresa.arquiteturalimpa.domain.model.Solicitacao;
import com.empresa.arquiteturalimpa.domain.repository.SolicitacaoRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.SolicitacaoJpaEntity;
import com.empresa.arquiteturalimpa.infrastructure.persistence.jpa.SolicitacaoJpaRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.mapper.MotivoSolicitacaoMapper;
import com.empresa.arquiteturalimpa.infrastructure.persistence.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapta o output port SolicitacaoRepository (domínio) para o Spring Data JPA (infraestrutura).
 */
@Component
@RequiredArgsConstructor
public class SolicitacaoRepositoryAdapter implements SolicitacaoRepository {

    private final SolicitacaoJpaRepository jpaRepository;

    @Override
    public Solicitacao save(Solicitacao solicitacao) {
        return toDomain(jpaRepository.save(toEntity(solicitacao)));
    }

    @Override
    public Optional<Solicitacao> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Solicitacao> findByUsuarioIdOrderByDataReferenciaDesc(UUID usuarioId) {
        return jpaRepository.findByUsuarioIdOrderByDataReferenciaDesc(usuarioId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Long countByStatus(StatusSolicitacao status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public Optional<Solicitacao> findTop1ByStatusOrderByDataReferenciaDesc(StatusSolicitacao status) {
        return jpaRepository.findTop1ByStatusOrderByDataReferenciaDesc(status).map(this::toDomain);
    }

    @Override
    public boolean existsByUsuarioIdAndDataReferenciaAndStatus(UUID usuarioId, LocalDate dataReferencia, StatusSolicitacao status) {
        return jpaRepository.existsByUsuarioIdAndDataReferenciaAndStatus(usuarioId, dataReferencia, status);
    }

    private Solicitacao toDomain(SolicitacaoJpaEntity entity) {
        return Solicitacao.builder()
                .id(entity.getId())
                .dataReferencia(entity.getDataReferencia())
                .usuario(UsuarioMapper.toDomain(entity.getUsuario()))
                .motivo(MotivoSolicitacaoMapper.toDomain(entity.getMotivo()))
                .descricao(entity.getDescricao())
                .anexoNome(entity.getAnexoNome())
                .anexoTipo(entity.getAnexoTipo())
                .anexoTamanho(entity.getAnexoTamanho())
                .anexoConteudo(entity.getAnexoConteudo())
                .diasConsecutivos(entity.getDiasConsecutivos())
                .quantidadeDias(entity.getQuantidadeDias())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private SolicitacaoJpaEntity toEntity(Solicitacao solicitacao) {
        return SolicitacaoJpaEntity.builder()
                .id(solicitacao.getId())
                .dataReferencia(solicitacao.getDataReferencia())
                .usuario(UsuarioMapper.toEntity(solicitacao.getUsuario()))
                .motivo(MotivoSolicitacaoMapper.toEntity(solicitacao.getMotivo()))
                .descricao(solicitacao.getDescricao())
                .anexoNome(solicitacao.getAnexoNome())
                .anexoTipo(solicitacao.getAnexoTipo())
                .anexoTamanho(solicitacao.getAnexoTamanho())
                .anexoConteudo(solicitacao.getAnexoConteudo())
                .diasConsecutivos(solicitacao.getDiasConsecutivos())
                .quantidadeDias(solicitacao.getQuantidadeDias())
                .status(solicitacao.getStatus())
                .createdAt(solicitacao.getCreatedAt())
                .updatedAt(solicitacao.getUpdatedAt())
                .build();
    }
}
