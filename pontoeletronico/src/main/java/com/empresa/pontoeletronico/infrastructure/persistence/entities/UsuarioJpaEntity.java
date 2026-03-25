package com.empresa.pontoeletronico.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade JPA para usuário
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
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "nome", nullable = false)
    private String nome;
    
    @Column(name = "cargo", length = 100)
    private String cargo;
    
    @Column(name = "departamento", length = 100)
    private String departamento;
    
    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id")
    private JornadaTrabalhoJpaEntity jornada;
}