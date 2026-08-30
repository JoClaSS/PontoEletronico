package com.empresa.arquiteturalimpa.infrastructure.persistence.jpa;

import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.JornadaTrabalhoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JornadaTrabalhoJpaRepository extends JpaRepository<JornadaTrabalhoJpaEntity, UUID> {

    @Query("SELECT j FROM JornadaTrabalhoJpaEntity j WHERE LOWER(j.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<JornadaTrabalhoJpaEntity> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    List<JornadaTrabalhoJpaEntity> findAllByOrderByHorasSemanaisAsc();
}
