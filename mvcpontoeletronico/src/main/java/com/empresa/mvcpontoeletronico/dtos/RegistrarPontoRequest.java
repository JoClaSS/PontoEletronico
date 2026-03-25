package com.empresa.mvcpontoeletronico.dtos;

import com.empresa.mvcpontoeletronico.entities.TipoPonto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para criação de registro de ponto
 * Arquitetura MVC: Camada de Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPontoRequest {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private UUID usuarioId;
    
    private TipoPonto tipoPonto; // Opcional - pode ser determinado automaticamente
    
    private String localizacao;
    
    private String observacao;
    
    private LocalDateTime dataHora; // Opcional - usa current timestamp se não informado
}