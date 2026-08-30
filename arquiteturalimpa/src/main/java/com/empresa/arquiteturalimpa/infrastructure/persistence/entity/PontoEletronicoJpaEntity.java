package com.empresa.arquiteturalimpa.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA (modelo de persistência) para PontoEletronico.
 */
@Entity
@Table(name = "pontos_eletronicos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PontoEletronicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "entrada1")
    private LocalDateTime entrada1;

    @Column(name = "saida1")
    private LocalDateTime saida1;

    @Column(name = "entrada2")
    private LocalDateTime entrada2;

    @Column(name = "saida2")
    private LocalDateTime saida2;

    @Column(name = "entrada3")
    private LocalDateTime entrada3;

    @Column(name = "saida3")
    private LocalDateTime saida3;

    @Column(name = "localizacao")
    private String localizacao;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
