package com.empresa.arquiteturalimpa.application.ponto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioHorasResponse {

    private UUID usuarioId;
    private String nomeUsuario;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long totalHorasMinutos;
    private String totalHorasFormatado;
    private List<RegistroDiario> registrosDiarios;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistroDiario {
        private LocalDate data;
        private List<PontoEletronicoResponse> pontos;
        private Long horasTrabalhadasMinutos;
        private String horasTrabalhadasFormatado;
        private boolean diaCompleto;
    }
}
