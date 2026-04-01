package com.empresa.mvcpontoeletronico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade PontoEletronico - representa um registro de ponto
 * Arquitetura MVC: Camada Model
 */
@Entity
@Table(name = "pontos_eletronicos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PontoEletronico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private Usuario usuario;
    
    @NotNull(message = "Data e hora é obrigatório")
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo de ponto é obrigatório")
    @Column(name = "tipo_ponto", nullable = false)
    private TipoPonto tipoPonto;
    
    @Column(name = "localizacao")
    private String localizacao;
    
    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Determina o próximo tipo de ponto baseado na sequência
     */
    public static TipoPonto determinarProximoTipo(TipoPonto ultimoTipo) {
        if (ultimoTipo == null) {
            return TipoPonto.ENTRADA_1;
        }
        
        return switch (ultimoTipo) {
            case ENTRADA_1 -> TipoPonto.SAIDA_1;
            case SAIDA_1 -> TipoPonto.ENTRADA_2;
            case ENTRADA_2 -> TipoPonto.SAIDA_2;
            case SAIDA_2 -> TipoPonto.ENTRADA_3;
            case ENTRADA_3 -> TipoPonto.SAIDA_3;
            case SAIDA_3 -> TipoPonto.ENTRADA_1; // Para o próximo dia
        };
    }
    
    /**
     * Verifica se é um registro de saída
     */
    public boolean isSaida() {
        return tipoPonto == TipoPonto.SAIDA_1 || 
               tipoPonto == TipoPonto.SAIDA_2 || 
               tipoPonto == TipoPonto.SAIDA_3;
    }
    
    /**
     * Verifica se é um registro de entrada
     */
    public boolean isEntrada() {
        return tipoPonto == TipoPonto.ENTRADA_1 || 
               tipoPonto == TipoPonto.ENTRADA_2 || 
               tipoPonto == TipoPonto.ENTRADA_3;
    }
}