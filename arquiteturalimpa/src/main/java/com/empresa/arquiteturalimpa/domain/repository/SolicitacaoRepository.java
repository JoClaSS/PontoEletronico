package com.empresa.arquiteturalimpa.domain.repository;

import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import com.empresa.arquiteturalimpa.domain.model.Solicitacao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída (output port) para persistência de Solicitacao.
 */
public interface SolicitacaoRepository {

    Solicitacao save(Solicitacao solicitacao);

    Optional<Solicitacao> findById(UUID id);

    List<Solicitacao> findByUsuarioIdOrderByDataReferenciaDesc(UUID usuarioId);

    Long countByStatus(StatusSolicitacao status);

    Optional<Solicitacao> findTop1ByStatusOrderByDataReferenciaDesc(StatusSolicitacao status);

    boolean existsByUsuarioIdAndDataReferenciaAndStatus(UUID usuarioId, LocalDate dataReferencia, StatusSolicitacao status);
}
