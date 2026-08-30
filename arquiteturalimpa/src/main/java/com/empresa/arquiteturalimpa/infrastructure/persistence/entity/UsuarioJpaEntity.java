package com.empresa.arquiteturalimpa.infrastructure.persistence.entity;

import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA (modelo de persistência) para Usuario.
 * Camada de infraestrutura: mantém os detalhes de framework (JPA/Hibernate) fora do domínio.
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nome", nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Size(max = 150)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "senha", nullable = false)
    private String senha;

    @NotBlank
    @Size(max = 14)
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private RoleType role = RoleType.FUNCIONARIO;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "primeiro_login", nullable = false)
    @Builder.Default
    private Boolean primeiroLogin = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
