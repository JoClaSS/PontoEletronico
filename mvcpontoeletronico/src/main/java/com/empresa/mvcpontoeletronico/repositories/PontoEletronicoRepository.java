package com.empresa.mvcpontoeletronico.repositories;

import com.empresa.mvcpontoeletronico.entities.PontoEletronico;
import com.empresa.mvcpontoeletronico.entities.TipoPonto;
import com.empresa.mvcpontoeletronico.entities.Usuario;
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
 * Repository para entidade PontoEletronico
 * Arquitetura MVC: Camada de Acesso aos Dados  
 */
@Repository
public interface PontoEletronicoRepository extends JpaRepository<PontoEletronico, UUID> {
    
    /**
     * Busca pontos por usuário em uma data específica
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) = :data ORDER BY p.dataHora ASC")
    List<PontoEletronico> findByUsuarioIdAndData(@Param("usuarioId") UUID usuarioId, 
                                                 @Param("data") LocalDate data);
    
    /**
     * Busca pontos por usuário em um período
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY p.dataHora ASC")
    List<PontoEletronico> findByUsuarioIdAndPeriodo(@Param("usuarioId") UUID usuarioId,
                                                    @Param("dataInicio") LocalDate dataInicio,
                                                    @Param("dataFim") LocalDate dataFim);
    
    /**
     * Busca o último ponto registrado por um usuário
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "ORDER BY p.dataHora DESC LIMIT 1")
    Optional<PontoEletronico> findUltimoRegistroPorUsuario(@Param("usuarioId") UUID usuarioId);
    
    /**
     * Busca pontos de hoje por usuário
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) = CURRENT_DATE ORDER BY p.dataHora ASC")
    List<PontoEletronico> findPontosHojePorUsuario(@Param("usuarioId") UUID usuarioId);
    
    /**
     * Busca pontos por tipo
     */
    List<PontoEletronico> findByTipoPonto(TipoPonto tipoPonto);
    
    /**
     * Busca pontos por usuário
     */
    List<PontoEletronico> findByUsuarioOrderByDataHoraDesc(Usuario usuario);
    
    /**
     * Verifica se existe ponto em um horário específico para um usuário
     */
    @Query("SELECT COUNT(p) > 0 FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND p.dataHora BETWEEN :inicio AND :fim")
    boolean existsByUsuarioIdAndDataHoraBetween(@Param("usuarioId") UUID usuarioId,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fim") LocalDateTime fim);
    
    /**
     * Conta registros por usuário em uma data
     */
    @Query("SELECT COUNT(p) FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.dataHora AS date) = :data")
    Long countByUsuarioIdAndData(@Param("usuarioId") UUID usuarioId, @Param("data") LocalDate data);
}