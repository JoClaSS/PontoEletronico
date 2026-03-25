package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service para gerenciamento de usuários
 * Arquitetura MVC: Camada de Negócio (Service)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Lista todos os usuários
     */
    public List<Usuario> listarTodos() {
        log.debug("Listando todos os usuários");
        return usuarioRepository.findAllByOrderByNomeAsc();
    }
    
    /**
     * Busca usuário por ID
     */
    public Optional<Usuario> buscarPorId(UUID id) {
        log.debug("Buscando usuário por ID: {}", id);
        return usuarioRepository.findById(id);
    }
    
    /**
     * Busca usuário por email
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        log.debug("Buscando usuário por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }
    
    /**
     * Busca usuário por CPF
     */
    public Optional<Usuario> buscarPorCpf(String cpf) {
        log.debug("Buscando usuário por CPF: {}", cpf);
        return usuarioRepository.findByCpf(cpf);
    }
    
    /**
     * Busca usuários por nome (busca parcial)
     */
    public List<Usuario> buscarPorNome(String nome) {
        log.debug("Buscando usuários por nome: {}", nome);
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Salva um usuário
     */
    @Transactional
    public Usuario salvar(Usuario usuario) {
        log.debug("Salvando usuário: {}", usuario.getNome());
        
        // Validações de negócio
        if (usuario.getId() == null && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        
        if (usuario.getId() == null && usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF");
        }
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Atualiza um usuário
     */
    @Transactional
    public Usuario atualizar(UUID id, Usuario usuarioAtualizado) {
        log.debug("Atualizando usuário ID: {}", id);
        
        Usuario usuarioExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Valida email único (exceto para o próprio usuário)
        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail()) &&
            usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        
        // Valida CPF único (exceto para o próprio usuário)
        if (!usuarioExistente.getCpf().equals(usuarioAtualizado.getCpf()) &&
            usuarioRepository.existsByCpf(usuarioAtualizado.getCpf())) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF");
        }
        
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setCpf(usuarioAtualizado.getCpf());
        
        return usuarioRepository.save(usuarioExistente);
    }
    
    /**
     * Remove um usuário
     */
    @Transactional
    public void remover(UUID id) {
        log.debug("Removendo usuário ID: {}", id);
        
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        
        usuarioRepository.deleteById(id);
    }
}