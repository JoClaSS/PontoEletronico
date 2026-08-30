package com.empresa.arquiteturalimpa.domain.repository;

import com.empresa.arquiteturalimpa.domain.model.MotivoSolicitacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída (output port) para persistência de MotivoSolicitacao.
 */
public interface MotivoSolicitacaoRepository {

    List<MotivoSolicitacao> findAllAtivos();

    Optional<MotivoSolicitacao> findById(UUID id);
}
