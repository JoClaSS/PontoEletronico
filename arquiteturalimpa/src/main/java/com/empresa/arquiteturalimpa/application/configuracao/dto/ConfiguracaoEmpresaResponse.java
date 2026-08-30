package com.empresa.arquiteturalimpa.application.configuracao.dto;

import com.empresa.arquiteturalimpa.domain.model.ConfiguracaoEmpresa;
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

    public static ConfiguracaoEmpresaResponse fromDomain(ConfiguracaoEmpresa entity) {
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

        if (entity.getFotoEmpresa() != null) {
            String mimeType = entity.getLogoEmpresaTipo() != null ? entity.getLogoEmpresaTipo() : "image/jpeg";
            String base64Image = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(entity.getFotoEmpresa());
            response.setFotoEmpresa(base64Image);
        }

        return response;
    }
}
