package com.empresa.arquiteturalimpa.infrastructure.persistence.jpa;

import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.ConfiguracaoEmpresaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracaoEmpresaJpaRepository extends JpaRepository<ConfiguracaoEmpresaJpaEntity, UUID> {

    @Query("SELECT c FROM ConfiguracaoEmpresaJpaEntity c ORDER BY c.createdAt ASC")
    Optional<ConfiguracaoEmpresaJpaEntity> findFirstConfiguration();
}
