package com.empresa.arquiteturalimpa.domain.repository;

import com.empresa.arquiteturalimpa.domain.model.ConfiguracaoEmpresa;

import java.util.Optional;

/**
 * Porta de saída (output port) para persistência de ConfiguracaoEmpresa.
 */
public interface ConfiguracaoEmpresaRepository {

    Optional<ConfiguracaoEmpresa> findFirstConfiguration();

    ConfiguracaoEmpresa save(ConfiguracaoEmpresa configuracao);
}
