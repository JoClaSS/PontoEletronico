package com.empresa.arquiteturalimpa.infrastructure.persistence.adapter;

import com.empresa.arquiteturalimpa.domain.model.ConfiguracaoEmpresa;
import com.empresa.arquiteturalimpa.domain.repository.ConfiguracaoEmpresaRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.ConfiguracaoEmpresaJpaEntity;
import com.empresa.arquiteturalimpa.infrastructure.persistence.jpa.ConfiguracaoEmpresaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapta o output port ConfiguracaoEmpresaRepository (domínio) para o Spring Data JPA (infraestrutura).
 */
@Component
@RequiredArgsConstructor
public class ConfiguracaoEmpresaRepositoryAdapter implements ConfiguracaoEmpresaRepository {

    private final ConfiguracaoEmpresaJpaRepository jpaRepository;

    @Override
    public Optional<ConfiguracaoEmpresa> findFirstConfiguration() {
        return jpaRepository.findFirstConfiguration().map(this::toDomain);
    }

    @Override
    public ConfiguracaoEmpresa save(ConfiguracaoEmpresa configuracao) {
        return toDomain(jpaRepository.save(toEntity(configuracao)));
    }

    private ConfiguracaoEmpresa toDomain(ConfiguracaoEmpresaJpaEntity entity) {
        return ConfiguracaoEmpresa.builder()
                .id(entity.getId())
                .nomeEmpresa(entity.getNomeEmpresa())
                .horarioCheckin(entity.getHorarioCheckin())
                .horarioCheckout(entity.getHorarioCheckout())
                .fotoEmpresa(entity.getFotoEmpresa())
                .logoEmpresaNome(entity.getLogoEmpresaNome())
                .logoEmpresaTipo(entity.getLogoEmpresaTipo())
                .logoEmpresaTamanho(entity.getLogoEmpresaTamanho())
                .intervaloMinimoMinutos(entity.getIntervaloMinimoMinutos())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ConfiguracaoEmpresaJpaEntity toEntity(ConfiguracaoEmpresa configuracao) {
        return ConfiguracaoEmpresaJpaEntity.builder()
                .id(configuracao.getId())
                .nomeEmpresa(configuracao.getNomeEmpresa())
                .horarioCheckin(configuracao.getHorarioCheckin())
                .horarioCheckout(configuracao.getHorarioCheckout())
                .fotoEmpresa(configuracao.getFotoEmpresa())
                .logoEmpresaNome(configuracao.getLogoEmpresaNome())
                .logoEmpresaTipo(configuracao.getLogoEmpresaTipo())
                .logoEmpresaTamanho(configuracao.getLogoEmpresaTamanho())
                .intervaloMinimoMinutos(configuracao.getIntervaloMinimoMinutos())
                .createdAt(configuracao.getCreatedAt())
                .updatedAt(configuracao.getUpdatedAt())
                .build();
    }
}
