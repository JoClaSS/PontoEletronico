package com.empresa.mvcpontoeletronico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade PontoEletronico - representa um registro de ponto diário
 * Arquitetura MVC: Camada Model
 * 
 * Nova estrutura: Um registro por dia por usuário com até 6 registros de entrada/saída
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
    
    // Data de referência do ponto (independente da data de criação)
    @Column(name = "data", nullable = false)
    @NotNull(message = "Data de referência é obrigatória")
    private LocalDate data;
    
    // Colunas de entrada e saída - até 3 pares por dia
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
    
    /**
     * Determina a próxima coluna disponível para registro de ponto
     * @return nome da próxima coluna (entrada1, saida1, entrada2, saida2, entrada3, saida3) ou null se lotado
     */
    public String getProximaColunaDisponivel() {
        if (entrada1 == null) return "entrada1";
        if (saida1 == null) return "saida1";
        if (entrada2 == null) return "entrada2";
        if (saida2 == null) return "saida2";
        if (entrada3 == null) return "entrada3";
        if (saida3 == null) return "saida3";
        return null; // Todas as colunas preenchidas
    }
    
    /**
     * Registra um ponto na próxima coluna disponível
     * @param dataHora o timestamp do registro
     * @return true se conseguiu registrar, false se todas as colunas estão preenchidas
     */
    public boolean registrarPonto(LocalDateTime dataHora) {
        String proximaColuna = getProximaColunaDisponivel();
        if (proximaColuna == null) {
            return false; // Não há mais colunas disponíveis
        }
        
        switch (proximaColuna) {
            case "entrada1" -> this.entrada1 = dataHora;
            case "saida1" -> this.saida1 = dataHora;
            case "entrada2" -> this.entrada2 = dataHora;
            case "saida2" -> this.saida2 = dataHora;
            case "entrada3" -> this.entrada3 = dataHora;
            case "saida3" -> this.saida3 = dataHora;
        }
        return true;
    }
    
    /**
     * Conta quantos pontos já foram registrados (colunas preenchidas)
     */
    public int contarPontosRegistrados() {
        int count = 0;
        if (entrada1 != null) count++;
        if (saida1 != null) count++;
        if (entrada2 != null) count++;
        if (saida2 != null) count++;
        if (entrada3 != null) count++;
        if (saida3 != null) count++;
        return count;
    }
    
    /**
     * Verifica se o registro está completo (6 pontos registrados)
     */
    public boolean isCompleto() {
        return contarPontosRegistrados() == 6;
    }
}