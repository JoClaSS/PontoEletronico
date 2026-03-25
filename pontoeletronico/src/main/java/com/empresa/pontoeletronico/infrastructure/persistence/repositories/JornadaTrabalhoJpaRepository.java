package com.empresa.pontoeletronico.infrastructure.persistence.repositories;

import com.empresa.pontoeletronico.infrastructure.persistence.entities.JornadaTrabalhoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório Spring Data JPA para JornadaTrabalho
 */
@Repository
public interface JornadaTrabalhoJpaRepository extends JpaRepository<JornadaTrabalhoJpaEntity, UUID> {
}