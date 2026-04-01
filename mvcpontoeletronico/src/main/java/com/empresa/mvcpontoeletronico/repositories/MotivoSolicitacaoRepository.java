package com.empresa.mvcpontoeletronico.repositories;

import com.empresa.mvcpontoeletronico.entities.MotivoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository para entidade MotivoSolicitacao
 * Arquitetura MVC: Camada de Acesso aos Dados
 */
@Repository
public interface MotivoSolicitacaoRepository extends JpaRepository<MotivoSolicitacao, UUID> {
    
    /**
     * Busca todos os motivos ativos
     */
    @Query("SELECT m FROM MotivoSolicitacao m WHERE m.ativo = true ORDER BY m.descricao ASC")
    List<MotivoSolicitacao> findAllAtivos();
    
    /**
     * Busca motivo por descrição (case insensitive)
     */
    @Query("SELECT m FROM MotivoSolicitacao m WHERE UPPER(m.descricao) = UPPER(:descricao)")
    MotivoSolicitacao findByDescricaoIgnoreCase(String descricao);
}