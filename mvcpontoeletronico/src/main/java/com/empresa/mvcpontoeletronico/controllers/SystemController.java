package com.empresa.mvcpontoeletronico.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para informações do sistema
 * Arquitetura MVC: Camada Controller
 */
@RestController
@RequestMapping("/api/system")
@Slf4j
public class SystemController {

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.footer.company}")
    private String footerCompany;

    /**
     * Obtém informações básicas do sistema
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getSystemInfo() {
        log.debug("Consultando informações do sistema");
        
        Map<String, String> systemInfo = new HashMap<>();
        systemInfo.put("version", appVersion);
        systemInfo.put("name", "Sistema de Ponto Eletrônico");
        systemInfo.put("footerCompany", footerCompany);
        
        return ResponseEntity.ok(systemInfo);
    }
}