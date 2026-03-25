package com.empresa.mvcpontoeletronico.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO para resposta de relatório de horas trabalhadas
 * Arquitetura MVC: Camada de Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioHorasResponse {
    
    private UUID usuarioId;
    private String nomeUsuario;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long totalHorasMinutos; // Total em minutos
    private String totalHorasFormatado; // Ex: "40h 30min"
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
        private boolean diaCompleto; // Se tem todos os 4 pontos
    }
}