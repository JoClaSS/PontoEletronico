package com.empresa.pontoeletronico.infrastructure.persistence.repositories;

import com.empresa.pontoeletronico.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para Usuario
 */
@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    
    Optional<UsuarioJpaEntity> findByEmail(String email);
    
    List<UsuarioJpaEntity> findByAtivoTrue();
    
    @Query("SELECT u FROM UsuarioJpaEntity u LEFT JOIN FETCH u.jornada WHERE u.id = :id")
    Optional<UsuarioJpaEntity> findByIdWithJornada(UUID id);
}