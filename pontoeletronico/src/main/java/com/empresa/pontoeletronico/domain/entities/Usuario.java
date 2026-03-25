package com.empresa.pontoeletronico.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

/**
 * Entidade de domínio que representa um usuário/funcionário do sistema
 */
@Data
@Builder
public class Usuario {
    
    private String id;
    private String email;
    private String nome;
    private String cargo;
    private String departamento;
    private boolean ativo;
    private JornadaTrabalho jornada;
    
    /**
     * Valida se o usuário é válido
     */
    public boolean isValido() {
        return id != null && !id.trim().isEmpty()
            && email != null && !email.trim().isEmpty()
            && nome != null && !nome.trim().isEmpty();
    }
    
    /**
     * Verifica se o usuário está ativo e pode registrar ponto
     */
    public boolean podeRegistrarPonto() {
        return ativo;
    }
}