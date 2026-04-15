package com.empresa.mvcpontoeletronico.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoEmpresaResponse {
    
    private UUID id;
    private String nomeEmpresa;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioCheckin;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioCheckout;
    
    private String fotoEmpresa; // Base64 string para o frontend
    private String logoEmpresaNome;
    private String logoEmpresaTipo;
    private Integer logoEmpresaTamanho;
    private Integer intervaloMinimoMinutos;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * Método estático para converter de entidade para DTO
     */
    public static ConfiguracaoEmpresaResponse fromEntity(com.empresa.mvcpontoeletronico.entities.ConfiguracaoEmpresa entity) {
        ConfiguracaoEmpresaResponse response = new ConfiguracaoEmpresaResponse();
        response.setId(entity.getId());
        response.setNomeEmpresa(entity.getNomeEmpresa());
        response.setHorarioCheckin(entity.getHorarioCheckin());
        response.setHorarioCheckout(entity.getHorarioCheckout());
        response.setLogoEmpresaNome(entity.getLogoEmpresaNome());
        response.setLogoEmpresaTipo(entity.getLogoEmpresaTipo());
        response.setLogoEmpresaTamanho(entity.getLogoEmpresaTamanho());
        response.setIntervaloMinimoMinutos(entity.getIntervaloMinimoMinutos());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        
        // Converter byte[] para Base64 string para o frontend
        if (entity.getFotoEmpresa() != null) {
            String mimeType = entity.getLogoEmpresaTipo() != null ? entity.getLogoEmpresaTipo() : "image/jpeg";
            String base64Image = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(entity.getFotoEmpresa());
            response.setFotoEmpresa(base64Image);
        }
        
        return response;
    }
}