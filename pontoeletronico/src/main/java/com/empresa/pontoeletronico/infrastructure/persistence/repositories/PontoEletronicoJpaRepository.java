package com.empresa.pontoeletronico.infrastructure.persistence.repositories;

import com.empresa.pontoeletronico.infrastructure.persistence.entities.PontoEletronicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para PontoEletronico
 */
@Repository
public interface PontoEletronicoJpaRepository extends JpaRepository<PontoEletronicoJpaEntity, UUID> {
    
    @Query("SELECT p FROM PontoEletronicoJpaEntity p " +
           "WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) = :data " +
           "ORDER BY p.dataHora ASC")
    List<PontoEletronicoJpaEntity> findByUsuarioIdAndData(
        @Param("usuarioId") UUID usuarioId, 
        @Param("data") LocalDate data
    );
    
    @Query("SELECT p FROM PontoEletronicoJpaEntity p " +
           "WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY p.dataHora ASC")
    List<PontoEletronicoJpaEntity> findByUsuarioIdAndPeriodo(
        @Param("usuarioId") UUID usuarioId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
    
    @Query("SELECT p FROM PontoEletronicoJpaEntity p " +
           "WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) = CURRENT_DATE " +
           "ORDER BY p.dataHora DESC")
    Optional<PontoEletronicoJpaEntity> findUltimoRegistroPorUsuario(
        @Param("usuarioId") UUID usuarioId
    );
    
    @Query("SELECT p FROM PontoEletronicoJpaEntity p " +
           "WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) = CURRENT_DATE " +
           "ORDER BY p.dataHora ASC")
    List<PontoEletronicoJpaEntity> findPontosHojePorUsuario(
        @Param("usuarioId") UUID usuarioId
    );
}