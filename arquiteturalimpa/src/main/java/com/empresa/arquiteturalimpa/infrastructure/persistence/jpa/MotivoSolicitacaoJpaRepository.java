package com.empresa.arquiteturalimpa.infrastructure.persistence.jpa;

import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.MotivoSolicitacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MotivoSolicitacaoJpaRepository extends JpaRepository<MotivoSolicitacaoJpaEntity, UUID> {

    @Query("SELECT m FROM MotivoSolicitacaoJpaEntity m WHERE m.ativo = true ORDER BY m.descricao ASC")
    List<MotivoSolicitacaoJpaEntity> findAllAtivos();
}
