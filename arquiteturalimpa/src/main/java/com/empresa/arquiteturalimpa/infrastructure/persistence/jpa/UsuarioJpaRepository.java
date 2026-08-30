package com.empresa.arquiteturalimpa.infrastructure.persistence.jpa;

import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    Optional<UsuarioJpaEntity> findByEmail(String email);

    Optional<UsuarioJpaEntity> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    @Query("SELECT u FROM UsuarioJpaEntity u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<UsuarioJpaEntity> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    List<UsuarioJpaEntity> findAllByOrderByNomeAsc();

    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.role = :funcionarioRole AND u.ativo = true ORDER BY u.nome ASC")
    List<UsuarioJpaEntity> findFuncionariosAtivos(@Param("funcionarioRole") RoleType funcionarioRole);
}
