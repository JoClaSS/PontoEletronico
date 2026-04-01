package com.empresa.mvcpontoeletronico.repositories;

import com.empresa.mvcpontoeletronico.entities.Solicitacao;
import com.empresa.mvcpontoeletronico.entities.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository para entidade Solicitacao
 * Arquitetura MVC: Camada de Acesso aos Dados
 */
@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {
    
    /**
     * Busca todas as solicitações de um usuário ordenadas por data de criação (mais recente primeiro)
     */
    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id = :usuarioId ORDER BY s.createdAt DESC")
    List<Solicitacao> findByUsuarioIdOrderByCreatedAtDesc(@Param("usuarioId") UUID usuarioId);
    
    /**
     * Busca solicitações de um usuário por status
     */
    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id = :usuarioId AND s.status = :status ORDER BY s.createdAt DESC")
    List<Solicitacao> findByUsuarioIdAndStatus(@Param("usuarioId") UUID usuarioId, @Param("status") StatusSolicitacao status);
    
    /**
     * Busca solicitações de um usuário em um período
     */
    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id = :usuarioId " +
           "AND s.dataReferencia BETWEEN :dataInicio AND :dataFim ORDER BY s.dataReferencia DESC")
    List<Solicitacao> findByUsuarioIdAndDataReferenciaBetween(@Param("usuarioId") UUID usuarioId,
                                                              @Param("dataInicio") LocalDate dataInicio,
                                                              @Param("dataFim") LocalDate dataFim);
    
    /**
     * Conta solicitações por status
     */
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.status = :status")
    Long countByStatus(@Param("status") StatusSolicitacao status);
    
    /**
     * Busca todas as solicitações ordenadas por data de criação
     */
    @Query("SELECT s FROM Solicitacao s ORDER BY s.createdAt DESC")
    List<Solicitacao> findAllOrderByCreatedAtDesc();
}