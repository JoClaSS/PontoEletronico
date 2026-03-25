package com.empresa.mvcpontoeletronico.repositories;

import com.empresa.mvcpontoeletronico.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para entidade Usuario
 * Arquitetura MVC: Camada de Acesso aos Dados
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    /**
     * Busca usuário por email
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Busca usuário por CPF
     */
    Optional<Usuario> findByCpf(String cpf);
    
    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica se existe usuário com o CPF
     */
    boolean existsByCpf(String cpf);
    
    /**
     * Busca usuários por nome (busca parcial)
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Usuario> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    /**
     * Lista usuários ordenados por nome
     */
    List<Usuario> findAllByOrderByNomeAsc();
}