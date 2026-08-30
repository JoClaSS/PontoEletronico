package com.empresa.arquiteturalimpa.interfaces.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para informações do sistema.
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.footer.company}")
    private String footerCompany;

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getSystemInfo() {
        Map<String, String> systemInfo = new HashMap<>();
        systemInfo.put("version", appVersion);
        systemInfo.put("name", "Sistema de Ponto Eletrônico");
        systemInfo.put("footerCompany", footerCompany);
        return ResponseEntity.ok(systemInfo);
    }
}
