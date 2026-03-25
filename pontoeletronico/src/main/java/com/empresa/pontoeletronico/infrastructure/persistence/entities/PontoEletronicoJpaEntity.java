package com.empresa.pontoeletronico.infrastructure.persistence.entities;

import com.empresa.pontoeletronico.domain.entities.TipoPonto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para ponto eletrônico
 */
@Entity
@Table(name = "pontos_eletronicos", indexes = {
    @Index(name = "idx_pontos_usuario_data", columnList = "usuario_id, data_hora"),
    @Index(name = "idx_pontos_data_hora", columnList = "data_hora")
})
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
    
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoPonto tipo;
    
    @Column(name = "localizacao", columnDefinition = "TEXT")
    private String localizacao;
    
    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;
    
    @Column(name = "dispositivo_id", length = 100)
    private String dispositivoId;
}