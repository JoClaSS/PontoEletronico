package com.empresa.arquiteturalimpa.infrastructure.persistence.adapter;

import com.empresa.arquiteturalimpa.domain.model.MotivoSolicitacao;
import com.empresa.arquiteturalimpa.domain.repository.MotivoSolicitacaoRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.jpa.MotivoSolicitacaoJpaRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.mapper.MotivoSolicitacaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapta o output port MotivoSolicitacaoRepository (domínio) para o Spring Data JPA (infraestrutura).
 */
@Component
@RequiredArgsConstructor
public class MotivoSolicitacaoRepositoryAdapter implements MotivoSolicitacaoRepository {

    private final MotivoSolicitacaoJpaRepository jpaRepository;

    @Override
    public List<MotivoSolicitacao> findAllAtivos() {
        return jpaRepository.findAllAtivos().stream()
                .map(MotivoSolicitacaoMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<MotivoSolicitacao> findById(UUID id) {
        return jpaRepository.findById(id).map(MotivoSolicitacaoMapper::toDomain);
    }
}
