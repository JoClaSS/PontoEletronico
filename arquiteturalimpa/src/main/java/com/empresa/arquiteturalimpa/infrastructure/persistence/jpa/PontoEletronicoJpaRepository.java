package com.empresa.arquiteturalimpa.infrastructure.persistence.jpa;

import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.PontoEletronicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PontoEletronicoJpaRepository extends JpaRepository<PontoEletronicoJpaEntity, UUID> {

    @Query("SELECT p FROM PontoEletronicoJpaEntity p WHERE p.usuario.id = :usuarioId AND p.data = :data")
    Optional<PontoEletronicoJpaEntity> findByUsuarioIdAndData(@Param("usuarioId") UUID usuarioId,
                                                               @Param("data") LocalDate data);

    @Query("SELECT p FROM PontoEletronicoJpaEntity p WHERE p.usuario.id = :usuarioId " +
           "AND p.data BETWEEN :dataInicio AND :dataFim ORDER BY p.data ASC")
    List<PontoEletronicoJpaEntity> findByUsuarioIdAndPeriodo(@Param("usuarioId") UUID usuarioId,
                                                              @Param("dataInicio") LocalDate dataInicio,
                                                              @Param("dataFim") LocalDate dataFim);

    @Query("SELECT p FROM PontoEletronicoJpaEntity p WHERE p.data = :data ORDER BY p.data ASC")
    List<PontoEletronicoJpaEntity> findByDataOrderByDataAsc(@Param("data") LocalDate data);
}
