package com.empresa.arquiteturalimpa.infrastructure.persistence.adapter;

import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.jpa.UsuarioJpaRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapta o output port UsuarioRepository (domínio) para o Spring Data JPA (infraestrutura).
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario save(Usuario usuario) {
        return UsuarioMapper.toDomain(jpaRepository.save(UsuarioMapper.toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return jpaRepository.findById(id).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByCpf(String cpf) {
        return jpaRepository.findByCpf(cpf).map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Usuario> findByNomeContainingIgnoreCase(String nome) {
        return jpaRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(UsuarioMapper::toDomain)
                .toList();
    }

    @Override
    public List<Usuario> findAllByOrderByNomeAsc() {
        return jpaRepository.findAllByOrderByNomeAsc().stream()
                .map(UsuarioMapper::toDomain)
                .toList();
    }

    @Override
    public List<Usuario> findFuncionariosAtivos(RoleType role) {
        return jpaRepository.findFuncionariosAtivos(role).stream()
                .map(UsuarioMapper::toDomain)
                .toList();
    }

    @Override
    public List<Usuario> findAllById(Collection<UUID> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(UsuarioMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
