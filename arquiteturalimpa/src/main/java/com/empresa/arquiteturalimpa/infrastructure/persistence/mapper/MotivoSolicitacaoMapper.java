package com.empresa.arquiteturalimpa.infrastructure.persistence.mapper;

import com.empresa.arquiteturalimpa.domain.model.MotivoSolicitacao;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.MotivoSolicitacaoJpaEntity;

/**
 * Converte entre o modelo de domínio MotivoSolicitacao e a entidade de persistência MotivoSolicitacaoJpaEntity.
 */
public final class MotivoSolicitacaoMapper {

    private MotivoSolicitacaoMapper() {
    }

    public static MotivoSolicitacao toDomain(MotivoSolicitacaoJpaEntity entity) {
        if (entity == null) return null;
        return MotivoSolicitacao.builder()
                .id(entity.getId())
                .descricao(entity.getDescricao())
                .ativo(entity.getAtivo())
                .requerAnexo(entity.getRequerAnexo())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static MotivoSolicitacaoJpaEntity toEntity(MotivoSolicitacao motivo) {
        if (motivo == null) return null;
        return MotivoSolicitacaoJpaEntity.builder()
                .id(motivo.getId())
                .descricao(motivo.getDescricao())
                .ativo(motivo.getAtivo())
                .requerAnexo(motivo.getRequerAnexo())
                .createdAt(motivo.getCreatedAt())
                .build();
    }
}
