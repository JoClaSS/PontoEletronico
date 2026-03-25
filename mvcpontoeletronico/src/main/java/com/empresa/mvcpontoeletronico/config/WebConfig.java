package com.empresa.mvcpontoeletronico.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurações gerais da aplicação MVC
 * Arquitetura MVC: Camada de Configuração
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * Configuração CORS para permitir acesso de frontends
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configurando CORS para API");
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}