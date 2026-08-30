package com.empresa.arquiteturalimpa.application.configuracao;

import com.empresa.arquiteturalimpa.application.configuracao.dto.AtualizarConfiguracaoRequest;
import com.empresa.arquiteturalimpa.application.configuracao.dto.ConfiguracaoEmpresaResponse;
import com.empresa.arquiteturalimpa.domain.model.ConfiguracaoEmpresa;
import com.empresa.arquiteturalimpa.domain.repository.ConfiguracaoEmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracaoEmpresaService {

    private final ConfiguracaoEmpresaRepository configuracaoRepository;

    public ConfiguracaoEmpresaResponse obterConfiguracoes() {
        Optional<ConfiguracaoEmpresa> configuracao = configuracaoRepository.findFirstConfiguration();

        if (configuracao.isPresent()) {
            return ConfiguracaoEmpresaResponse.fromDomain(configuracao.get());
        }

        log.debug("Nenhuma configuração encontrada, retornando padrões");
        ConfiguracaoEmpresaResponse defaultConfig = new ConfiguracaoEmpresaResponse();
        defaultConfig.setNomeEmpresa("Mundial Ciclo");
        defaultConfig.setHorarioCheckin(LocalTime.of(8, 0));
        defaultConfig.setHorarioCheckout(LocalTime.of(18, 0));
        defaultConfig.setIntervaloMinimoMinutos(0);
        return defaultConfig;
    }

    @Transactional
    public ConfiguracaoEmpresaResponse salvarConfiguracoes(AtualizarConfiguracaoRequest request) {
        ConfiguracaoEmpresa configuracao = configuracaoRepository.findFirstConfiguration()
                .orElse(ConfiguracaoEmpresa.builder().build());

        configuracao.setNomeEmpresa(request.getNomeEmpresa());
        configuracao.setHorarioCheckin(request.getHorarioCheckin());
        configuracao.setHorarioCheckout(request.getHorarioCheckout());
        configuracao.setIntervaloMinimoMinutos(request.getIntervaloMinimoMinutos() != null ? request.getIntervaloMinimoMinutos() : 0);
        configuracao.setLogoEmpresaNome(request.getLogoEmpresaNome());
        configuracao.setLogoEmpresaTipo(request.getLogoEmpresaTipo());
        configuracao.setLogoEmpresaTamanho(request.getLogoEmpresaTamanho());

        // Valida a regra de negócio antes de persistir (checkout > checkin)
        configuracao.validarHorarios();

        if (request.getFotoEmpresa() != null && !request.getFotoEmpresa().trim().isEmpty()) {
            String base64Data = request.getFotoEmpresa();
            if (base64Data.contains(",")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                configuracao.setFotoEmpresa(imageBytes);
            } catch (IllegalArgumentException e) {
                throw new com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException("Formato de imagem inválido");
            }
        } else {
            configuracao.setFotoEmpresa(null);
            configuracao.setLogoEmpresaNome(null);
            configuracao.setLogoEmpresaTipo(null);
            configuracao.setLogoEmpresaTamanho(null);
        }

        ConfiguracaoEmpresa saved = configuracaoRepository.save(configuracao);
        return ConfiguracaoEmpresaResponse.fromDomain(saved);
    }
}
