package com.empresa.mvcpontoeletronico.repositories;

import com.empresa.mvcpontoeletronico.entities.PontoEletronico;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
     * Busca o registro de ponto de um usuário em uma data específica
     * Retorna o registro único do dia (pode ter várias colunas preenchidas)
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND p.data = :data")
    Optional<PontoEletronico> findByUsuarioIdAndData(@Param("usuarioId") UUID usuarioId, 
                                                   @Param("data") LocalDate data);
    
    /**
     * Busca registros por usuário em um período
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND p.data BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY p.data ASC")
    List<PontoEletronico> findByUsuarioIdAndPeriodo(@Param("usuarioId") UUID usuarioId,
                                                    @Param("dataInicio") LocalDate dataInicio,
                                                    @Param("dataFim") LocalDate dataFim);
    
    /**
     * Busca o último registro por usuário (mais recente) - usando Pageable para LIMIT
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "ORDER BY p.createdAt DESC")
    List<PontoEletronico> findUltimoRegistroPorUsuarioList(@Param("usuarioId") UUID usuarioId);
    
    /**
     * Método padrão que busca o último registro (usar first() no service)
     */
    default Optional<PontoEletronico> findUltimoRegistroPorUsuario(UUID usuarioId) {
        List<PontoEletronico> result = findUltimoRegistroPorUsuarioList(usuarioId);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
    
    /**
     * Busca registros de hoje por usuário
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.createdAt AS date) = CAST(CURRENT_TIMESTAMP AS date) ORDER BY p.createdAt ASC")
    List<PontoEletronico> findPontosHojePorUsuario(@Param("usuarioId") UUID usuarioId);
    
    /**
     * Busca registros por usuário
     */
    List<PontoEletronico> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);
    
    /**
     * Verifica se existe registro para um usuário em uma data específica
     */
    @Query("SELECT COUNT(p) > 0 FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.createdAt AS date) = :data")
    boolean existsByUsuarioIdAndData(@Param("usuarioId") UUID usuarioId, @Param("data") LocalDate data);
    
    /**
     * Conta registros por usuário em uma data (sempre será 0 ou 1 com a nova estrutura)
     */
    @Query("SELECT COUNT(p) FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND CAST(p.createdAt AS date) = :data")
    Long countByUsuarioIdAndData(@Param("usuarioId") UUID usuarioId, @Param("data") LocalDate data);
    
    /**
     * Busca registros que têm entrada mas ainda não têm todas as saídas preenchidas (registro incompleto)
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.usuario.id = :usuarioId " +
           "AND (p.entrada1 IS NOT NULL AND (p.saida1 IS NULL OR p.entrada2 IS NULL OR p.saida2 IS NULL OR p.entrada3 IS NULL OR p.saida3 IS NULL)) " +
           "ORDER BY p.createdAt DESC")
    List<PontoEletronico> findRegistrosIncompletos(@Param("usuarioId") UUID usuarioId);

    /**
     * Busca todos os pontos registrados em uma data específica
     * Usado para lista de presença
     */
    @Query("SELECT p FROM PontoEletronico p WHERE p.data = :data ORDER BY p.data ASC")
    List<PontoEletronico> findByDataOrderByDataAsc(@Param("data") LocalDate data);
}
