package com.empresa.arquiteturalimpa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio PontoEletronico - representa um registro de ponto diário.
 * Um registro por dia por usuário, com até 6 marcações de entrada/saída.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PontoEletronico {

    private UUID id;
    private Usuario usuario;
    private LocalDate data;

    private LocalDateTime entrada1;
    private LocalDateTime saida1;
    private LocalDateTime entrada2;
    private LocalDateTime saida2;
    private LocalDateTime entrada3;
    private LocalDateTime saida3;

    private String localizacao;
    private String observacao;

    private LocalDateTime createdAt;

    /**
     * Determina a próxima coluna disponível para registro de ponto.
     */
    public String getProximaColunaDisponivel() {
        if (entrada1 == null) return "entrada1";
        if (saida1 == null) return "saida1";
        if (entrada2 == null) return "entrada2";
        if (saida2 == null) return "saida2";
        if (entrada3 == null) return "entrada3";
        if (saida3 == null) return "saida3";
        return null;
    }

    /**
     * Registra um ponto na próxima coluna disponível.
     */
    public boolean registrarPonto(LocalDateTime dataHora) {
        String proximaColuna = getProximaColunaDisponivel();
        if (proximaColuna == null) {
            return false;
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
     * Conta quantos pontos já foram registrados.
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
     * Verifica se o registro está completo (6 pontos registrados).
     */
    public boolean isCompleto() {
        return contarPontosRegistrados() == 6;
    }

    /**
     * Retorna o último timestamp não-nulo dentre as colunas de entrada/saída.
     */
    public LocalDateTime getUltimoRegistro() {
        if (saida3 != null) return saida3;
        if (entrada3 != null) return entrada3;
        if (saida2 != null) return saida2;
        if (entrada2 != null) return entrada2;
        if (saida1 != null) return saida1;
        if (entrada1 != null) return entrada1;
        return null;
    }
}
