package com.empresa.arquiteturalimpa.application.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * O tipo do ponto é determinado automaticamente pela sequência (entrada1 -> saida1 -> entrada2...).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPontoRequest {

    @NotNull(message = "ID do usuário é obrigatório")
    private UUID usuarioId;

    private String localizacao;
    private String observacao;

    private LocalDateTime dataHora; // Opcional - usa o instante atual se não informado
}
