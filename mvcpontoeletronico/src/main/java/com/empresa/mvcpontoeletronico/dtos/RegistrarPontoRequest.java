package com.empresa.mvcpontoeletronico.dtos;

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
 * 
 * Nova estrutura: O tipo é determinado automaticamente pela sequência (entrada1 -> saida1 -> entrada2...)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPontoRequest {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private UUID usuarioId;
    
    // Tipo não é mais necessário - determinado automaticamente pela sequência
    
    private String localizacao;
    
    private String observacao;
    
    private LocalDateTime dataHora; // Opcional - usa current timestamp se não informado
}