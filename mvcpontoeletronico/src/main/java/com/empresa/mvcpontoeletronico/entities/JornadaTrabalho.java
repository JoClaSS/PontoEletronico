package com.empresa.mvcpontoeletronico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JornadaTrabalho - representa tipos de jornada de trabalho
 * Arquitetura MVC: Camada Model
 */
@Entity
@Table(name = "jornadas_trabalho")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JornadaTrabalho {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Nome da jornada é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;
    
    @NotNull(message = "Horas semanais é obrigatório")
    @Min(value = 1, message = "Horas semanais deve ser maior que 0")
    @Max(value = 60, message = "Horas semanais deve ser menor ou igual a 60")
    @Column(name = "horas_semanais", nullable = false)
    private Integer horasSemanais;
    
    @NotNull(message = "Dias trabalhados é obrigatório")
    @Min(value = 1, message = "Dias trabalhados deve ser maior que 0")
    @Max(value = 7, message = "Dias trabalhados deve ser menor ou igual a 7")
    @Column(name = "dias_trabalhados", nullable = false)
    private Integer diasTrabalhados;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Calcula a média de horas por dia
     */
    public double calcularHorasPorDia() {
        if (diasTrabalhados == 0) return 0;
        return (double) horasSemanais / diasTrabalhados;
    }
}