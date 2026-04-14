package com.empresa.mvcpontoeletronico.repositories;

import com.empresa.mvcpontoeletronico.entities.ConfiguracaoEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository para entidade ConfiguracaoEmpresa
 * Arquitetura MVC: Camada de Acesso aos Dados
 */
@Repository
public interface ConfiguracaoEmpresaRepository extends JpaRepository<ConfiguracaoEmpresa, UUID> {
    
    /**
     * Busca a primeira (e única) configuração da empresa
     * Como o sistema deve ter apenas uma configuração global,
     * este método sempre retorna a configuração ativa
     */
    @Query("SELECT c FROM ConfiguracaoEmpresa c ORDER BY c.createdAt ASC")
    Optional<ConfiguracaoEmpresa> findFirstConfiguration();
    
    /**
     * Verifica se já existe alguma configuração cadastrada
     */
    boolean existsByNomeEmpresaIsNotNull();
}