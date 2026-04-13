package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.CriarUsuarioRequest;
import com.empresa.mvcpontoeletronico.dtos.UsuarioResponse;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.entities.RoleType;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Lista todos os usuários
     */
    public List<UsuarioResponse> listarTodos() {
        log.debug("Listando todos os usuários");
        return usuarioRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    /**
     * Cria um novo usuário
     */
    @Transactional
    public UsuarioResponse criarUsuario(CriarUsuarioRequest request) {
        log.debug("Criando usuário: {}", request.getNome());
        
        // Validações de negócio
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        
        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF");
        }
        
        try {
            // Criar usuário no banco com senha hasheada
            Usuario usuario = Usuario.builder()
                    .nome(request.getNome())
                    .email(request.getEmail())
                    .senha(passwordEncoder.encode(request.getSenha()))
                    .cpf(request.getCpf())
                    .role(request.getRole())
                    .build();
            
            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            log.info("Usuário criado com sucesso: {}", usuarioSalvo.getNome());
            
            return toResponse(usuarioSalvo);
            
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }
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
    public UsuarioResponse atualizar(UUID id, CriarUsuarioRequest request) {
        log.debug("Atualizando usuário ID: {}", id);
        
        Usuario usuarioExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Valida email único (exceto para o próprio usuário)
        if (!usuarioExistente.getEmail().equals(request.getEmail()) &&
            usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        
        // Valida CPF único (exceto para o próprio usuário)
        if (!usuarioExistente.getCpf().equals(request.getCpf()) &&
            usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF");
        }
        
        try {
            // Atualizar no banco
            usuarioExistente.setNome(request.getNome());
            usuarioExistente.setEmail(request.getEmail());
            usuarioExistente.setCpf(request.getCpf());
            usuarioExistente.setRole(request.getRole());
            
            // Atualizar senha se fornecida
            if (request.getSenha() != null && !request.getSenha().trim().isEmpty()) {
                usuarioExistente.setSenha(passwordEncoder.encode(request.getSenha()));
            }
            
            Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
            log.info("Usuário atualizado com sucesso: {}", usuarioAtualizado.getNome());
            
            return toResponse(usuarioAtualizado);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
        }
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
        
        try {
            usuarioRepository.deleteById(id);
            log.info("Usuário removido com sucesso: {}", id);
            
        } catch (Exception e) {
            log.error("Erro ao remover usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao remover usuário: " + e.getMessage());
        }
    }
    
    /**
     * Converte Usuario para UsuarioResponse
     */
    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }

    /**
     * Desativa um usuário (soft delete)
     */
    @Transactional
    public void desativarUsuario(UUID id) {
        log.debug("Desativando usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        if (!usuario.getAtivo()) {
            throw new IllegalArgumentException("Usuário já está desativado");
        }
        
        try {
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
            log.info("Usuário desativado com sucesso: {} (ID: {})", usuario.getNome(), id);
            
        } catch (Exception e) {
            log.error("Erro ao desativar usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao desativar usuário: " + e.getMessage());
        }
    }

    /**
     * Reativa um usuário  
     */
    @Transactional
    public void reativarUsuario(UUID id) {
        log.debug("Reativando usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        if (usuario.getAtivo()) {
            throw new IllegalArgumentException("Usuário já está ativo");
        }
        
        try {
            usuario.setAtivo(true);
            usuarioRepository.save(usuario);
            log.info("Usuário reativado com sucesso: {} (ID: {})", usuario.getNome(), id);
        } catch (Exception e) {
            log.error("Erro ao reativar usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao reativar usuário: " + e.getMessage());
        }
    }
}