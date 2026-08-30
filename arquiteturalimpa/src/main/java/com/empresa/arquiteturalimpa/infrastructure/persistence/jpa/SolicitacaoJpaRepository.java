package com.empresa.arquiteturalimpa.infrastructure.persistence.jpa;

import com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.SolicitacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitacaoJpaRepository extends JpaRepository<SolicitacaoJpaEntity, UUID> {

    @Query("SELECT s FROM SolicitacaoJpaEntity s WHERE s.usuario.id = :usuarioId ORDER BY s.dataReferencia DESC")
    List<SolicitacaoJpaEntity> findByUsuarioIdOrderByDataReferenciaDesc(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT COUNT(s) FROM SolicitacaoJpaEntity s WHERE s.status = :status")
    Long countByStatus(@Param("status") StatusSolicitacao status);

    Optional<SolicitacaoJpaEntity> findTop1ByStatusOrderByDataReferenciaDesc(StatusSolicitacao status);

    boolean existsByUsuarioIdAndDataReferenciaAndStatus(UUID usuarioId, LocalDate dataReferencia, StatusSolicitacao status);
}
