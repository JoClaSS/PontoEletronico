package com.empresa.arquiteturalimpa.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA (modelo de persistência) para JornadaTrabalho.
 */
@Entity
@Table(name = "jornadas_trabalho")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JornadaTrabalhoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "horas_semanais", nullable = false)
    private Integer horasSemanais;

    @Column(name = "dias_trabalhados", nullable = false)
    private Integer diasTrabalhados;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
