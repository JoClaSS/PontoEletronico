package com.empresa.pontoeletronico.infrastructure.persistence.repositories;

import com.empresa.pontoeletronico.domain.entities.Usuario;
import com.empresa.pontoeletronico.domain.repositories.UsuarioRepository;
import com.empresa.pontoeletronico.infrastructure.persistence.entities.UsuarioJpaEntity;
import com.empresa.pontoeletronico.infrastructure.persistence.mappers.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação JPA do repositório de Usuario
 * Faz a ponte entre o domínio e o JPA, respeitando a inversão de dependências
 */
@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryJpaImpl implements UsuarioRepository {
    
    private final UsuarioJpaRepository jpaRepository;
    
    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entity = PersistenceMapper.toJpaEntity(usuario);
        UsuarioJpaEntity savedEntity = jpaRepository.save(entity);
        return PersistenceMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Usuario> buscarPorId(String id) {
        UUID uuid = UUID.fromString(id);
        return jpaRepository.findByIdWithJornada(uuid)
            .map(PersistenceMapper::toDomain);
    }
    
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(PersistenceMapper::toDomain);
    }
    
    @Override
    public List<Usuario> listarAtivos() {
        return jpaRepository.findByAtivoTrue()
            .stream()
            .map(PersistenceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Usuario> listarTodos() {
        return jpaRepository.findAll()
            .stream()
            .map(PersistenceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void remover(String id) {
        UUID uuid = UUID.fromString(id);
        jpaRepository.deleteById(uuid);
    }
    
    @Override
    public boolean existe(String id) {
        UUID uuid = UUID.fromString(id);
        return jpaRepository.existsById(uuid);
    }
}