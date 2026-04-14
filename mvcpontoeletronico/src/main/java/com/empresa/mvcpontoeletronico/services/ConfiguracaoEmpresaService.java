package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.AtualizarConfiguracaoRequest;
import com.empresa.mvcpontoeletronico.dtos.ConfiguracaoEmpresaResponse;
import com.empresa.mvcpontoeletronico.entities.ConfiguracaoEmpresa;
import com.empresa.mvcpontoeletronico.repositories.ConfiguracaoEmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Optional;

/**
 * Service para gerenciamento das configurações da empresa
 * Arquitetura MVC: Camada de Negócio (Service)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracaoEmpresaService {
    
    private final ConfiguracaoEmpresaRepository configuracaoRepository;
    
    /**
     * Busca as configurações da empresa
     * Retorna a configuração existente ou cria uma padrão se não existir
     */
    public ConfiguracaoEmpresaResponse obterConfiguracoes() {
        log.debug("Buscando configurações da empresa");
        
        Optional<ConfiguracaoEmpresa> configuracao = configuracaoRepository.findFirstConfiguration();
        
        if (configuracao.isPresent()) {
            return ConfiguracaoEmpresaResponse.fromEntity(configuracao.get());
        }
        
        // Se não existir configuração, retorna configuração padrão
        // (será criada quando o usuário salvar pela primeira vez)
        log.debug("Nenhuma configuração encontrada, retornando padrões");
        ConfiguracaoEmpresaResponse defaultConfig = new ConfiguracaoEmpresaResponse();
        defaultConfig.setNomeEmpresa("Mundial Ciclo");
        defaultConfig.setHorarioCheckin(java.time.LocalTime.of(8, 0));
        defaultConfig.setHorarioCheckout(java.time.LocalTime.of(18, 0));
        return defaultConfig;
    }
    
    /**
     * Salva ou atualiza as configurações da empresa
     * Como deve haver apenas uma configuração, sempre atualiza a existente
     * ou cria uma nova se não existir
     */
    @Transactional
    public ConfiguracaoEmpresaResponse salvarConfiguracoes(AtualizarConfiguracaoRequest request) {
        log.debug("Salvando configurações da empresa: {}", request.getNomeEmpresa());
        log.debug("Horários recebidos: {} - {}", request.getHorarioCheckin(), request.getHorarioCheckout());
        
        try {
            // Validações de negócio
            if (request.getHorarioCheckout().isBefore(request.getHorarioCheckin()) || 
                request.getHorarioCheckout().equals(request.getHorarioCheckin())) {
                throw new IllegalArgumentException("Horário de checkout deve ser posterior ao horário de check-in");
            }
            
            log.debug("Buscando configuração existente...");
            // Busca configuração existente ou cria nova
            ConfiguracaoEmpresa configuracao = configuracaoRepository.findFirstConfiguration()
                    .orElse(new ConfiguracaoEmpresa());
            
            log.debug("Configuração encontrada: {}", configuracao.getId());
            
            // Atualiza os dados
            configuracao.setNomeEmpresa(request.getNomeEmpresa());
            configuracao.setHorarioCheckin(request.getHorarioCheckin());
            configuracao.setHorarioCheckout(request.getHorarioCheckout());
            configuracao.setLogoEmpresaNome(request.getLogoEmpresaNome());
            configuracao.setLogoEmpresaTipo(request.getLogoEmpresaTipo());
            configuracao.setLogoEmpresaTamanho(request.getLogoEmpresaTamanho());
            
            log.debug("Dados básicos atualizados. Processando imagem...");
            
            // Processa a imagem se fornecida
            if (request.getFotoEmpresa() != null && !request.getFotoEmpresa().trim().isEmpty()) {
                try {
                    log.debug("Processando imagem base64...");
                    // Remove o prefixo "data:mime/type;base64," se existir
                    String base64Data = request.getFotoEmpresa();
                    if (base64Data.contains(",")) {
                        base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
                    }
                    
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    configuracao.setFotoEmpresa(imageBytes);
                    
                    log.debug("Imagem processada com sucesso: {} bytes", imageBytes.length);
                } catch (IllegalArgumentException e) {
                    log.error("Erro ao decodificar imagem base64: {}", e.getMessage());
                    throw new IllegalArgumentException("Formato de imagem inválido");
                }
            } else if (request.getFotoEmpresa() == null || request.getFotoEmpresa().trim().isEmpty()) {
                // Se não foi fornecida imagem, remove a existente
                log.debug("Removendo imagem existente...");
                configuracao.setFotoEmpresa(null);
                configuracao.setLogoEmpresaNome(null);
                configuracao.setLogoEmpresaTipo(null);
                configuracao.setLogoEmpresaTamanho(null);
            }
            
            log.debug("Salvando no banco de dados...");
            ConfiguracaoEmpresa saved = configuracaoRepository.save(configuracao);
            log.debug("Configurações salvas com ID: {}", saved.getId());
            
            return ConfiguracaoEmpresaResponse.fromEntity(saved);
            
        } catch (Exception e) {
            log.error("Erro detalhado ao salvar configurações: ", e);
            throw e;
        }
    }
}