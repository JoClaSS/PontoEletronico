package com.empresa.pontoeletronico.domain.repositories;

import com.empresa.pontoeletronico.domain.entities.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de Usuario
 * Segue o princípio de inversão de dependência da arquitetura limpa
 */
public interface UsuarioRepository {
    
    /**
     * Salva um usuário
     */
    Usuario salvar(Usuario usuario);
    
    /**
     * Busca usuário por ID
     */
    Optional<Usuario> buscarPorId(String id);
    
    /**
     * Busca usuário por email
     */
    Optional<Usuario> buscarPorEmail(String email);
    
    /**
     * Lista todos os usuários ativos
     */
    List<Usuario> listarAtivos();
    
    /**
     * Lista todos os usuários
     */
    List<Usuario> listarTodos();
    
    /**
     * Remove um usuário
     */
    void remover(String id);
    
    /**
     * Verifica se um usuário existe
     */
    boolean existe(String id);
}