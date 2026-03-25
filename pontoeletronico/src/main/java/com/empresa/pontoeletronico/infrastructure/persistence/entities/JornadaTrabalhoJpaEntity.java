package com.empresa.pontoeletronico.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidade JPA para jornada de trabalho
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
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "inicio_expediente", nullable = false)
    private LocalTime inicioExpediente;
    
    @Column(name = "fim_expediente", nullable = false)
    private LocalTime fimExpediente;
    
    @Column(name = "inicio_intervalo")
    private LocalTime inicioIntervalo;
    
    @Column(name = "fim_intervalo")
    private LocalTime fimIntervalo;
    
    @Column(name = "carga_horaria_diaria", nullable = false)
    private Duration cargaHorariaDiaria;
    
    @Column(name = "permite_hora_extra", nullable = false)
    @Builder.Default
    private Boolean permiteHoraExtra = false;
    
    @Column(name = "limite_hora_extra")
    private Duration limiteHoraExtra;
}