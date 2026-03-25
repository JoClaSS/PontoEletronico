package com.empresa.mvcpontoeletronico.repositories;

import com.empresa.mvcpontoeletronico.entities.JornadaTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para entidade JornadaTrabalho
 * Arquitetura MVC: Camada de Acesso aos Dados
 */
@Repository
public interface JornadaTrabalhoRepository extends JpaRepository<JornadaTrabalho, UUID> {
    
    /**
     * Busca jornadas por nome
     */
    @Query("SELECT j FROM JornadaTrabalho j WHERE LOWER(j.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<JornadaTrabalho> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    /**
     * Busca jornadas por horas semanais
     */
    List<JornadaTrabalho> findByHorasSemanais(Integer horasSemanais);
    
    /**
     * Busca jornadas por dias trabalhados
     */
    List<JornadaTrabalho> findByDiasTrabalhados(Integer diasTrabalhados);
    
    /**
     * Lista todas as jornadas ordenadas por horas semanais
     */
    List<JornadaTrabalho> findAllByOrderByHorasSemanaisAsc();
}