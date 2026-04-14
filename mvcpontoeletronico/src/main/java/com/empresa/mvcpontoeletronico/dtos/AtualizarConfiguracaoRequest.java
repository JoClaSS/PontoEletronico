package com.empresa.mvcpontoeletronico.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarConfiguracaoRequest {
    
    @NotBlank(message = "Nome da empresa é obrigatório")
    @Size(max = 200, message = "Nome da empresa deve ter no máximo 200 caracteres")
    private String nomeEmpresa;
    
    @NotNull(message = "Horário de check-in é obrigatório")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioCheckin;
    
    @NotNull(message = "Horário de checkout é obrigatório")  
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioCheckout;
    
    private String fotoEmpresa; // Base64 string
    
    @Size(max = 255, message = "Nome do arquivo deve ter no máximo 255 caracteres")
    private String logoEmpresaNome;
    
    @Size(max = 100, message = "Tipo MIME deve ter no máximo 100 caracteres")
    private String logoEmpresaTipo;
    
    private Integer logoEmpresaTamanho;
}