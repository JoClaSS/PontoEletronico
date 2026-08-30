package com.empresa.arquiteturalimpa.domain.repository;

import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.domain.model.Usuario;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída (output port) para persistência de Usuario.
 * Implementada pela camada de infraestrutura.
 */
public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(UUID id);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsById(UUID id);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    List<Usuario> findAllByOrderByNomeAsc();

    List<Usuario> findFuncionariosAtivos(RoleType role);

    List<Usuario> findAllById(Collection<UUID> ids);

    void deleteById(UUID id);
}
