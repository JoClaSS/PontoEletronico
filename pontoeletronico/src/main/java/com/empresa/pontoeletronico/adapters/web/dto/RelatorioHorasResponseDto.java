package com.empresa.pontoeletronico.adapters.web.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para resposta do relatório de horas trabalhadas
 */
@Data
public class RelatorioHorasResponseDto {
    
    private String usuarioId;
    private LocalDate data;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String horasTrabalhadasFormatadas;
    private String cargaHorariaEsperadaFormatada;
    private String saldoFormatado;
    private boolean temHorasDebito;
    private boolean temHorasExcedentes;
    private List<PontoEletronicoResponseDto> pontos;
}