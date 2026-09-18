package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.infrastructure.security.JwtRequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SystemController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarInformacoesDoSistema() throws Exception {
        mockMvc.perform(get("/api/system/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.name").value("Sistema de Ponto Eletrônico"));
    }
}
