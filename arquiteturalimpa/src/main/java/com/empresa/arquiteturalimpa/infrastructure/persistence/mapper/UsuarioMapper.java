package com.empresa.arquiteturalimpa.infrastructure.persistence.mapper;

import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.UsuarioJpaEntity;

/**
 * Converte entre o modelo de domínio Usuario e a entidade de persistência UsuarioJpaEntity.
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toDomain(UsuarioJpaEntity entity) {
        if (entity == null) return null;
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .senha(entity.getSenha())
                .cpf(entity.getCpf())
                .role(entity.getRole())
                .ativo(entity.getAtivo())
                .primeiroLogin(entity.getPrimeiroLogin())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UsuarioJpaEntity toEntity(Usuario usuario) {
        if (usuario == null) return null;
        return UsuarioJpaEntity.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .cpf(usuario.getCpf())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .primeiroLogin(usuario.getPrimeiroLogin())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
