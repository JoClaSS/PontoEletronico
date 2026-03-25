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
            return TipoPonto.ENTRADA;
        }
        
        return switch (ultimoTipo) {
            case ENTRADA -> TipoPonto.SAIDA_ALMOCO;
            case SAIDA_ALMOCO -> TipoPonto.RETORNO_ALMOCO;
            case RETORNO_ALMOCO -> TipoPonto.SAIDA;
            case SAIDA -> TipoPonto.ENTRADA; // Para o próximo dia
        };
    }
    
    /**
     * Verifica se é um registro de saída
     */
    public boolean isSaida() {
        return tipoPonto == TipoPonto.SAIDA || tipoPonto == TipoPonto.SAIDA_ALMOCO;
    }
    
    /**
     * Verifica se é um registro de entrada
     */
    public boolean isEntrada() {
        return tipoPonto == TipoPonto.ENTRADA || tipoPonto == TipoPonto.RETORNO_ALMOCO;
    }
}