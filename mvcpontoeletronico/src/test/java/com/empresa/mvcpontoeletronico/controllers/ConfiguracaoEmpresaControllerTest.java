package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.AtualizarConfiguracaoRequest;
import com.empresa.mvcpontoeletronico.dtos.ConfiguracaoEmpresaResponse;
import com.empresa.mvcpontoeletronico.security.JwtRequestFilter;
import com.empresa.mvcpontoeletronico.services.ConfiguracaoEmpresaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConfiguracaoEmpresaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ConfiguracaoEmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConfiguracaoEmpresaService configuracaoService;

    @Test
    void deveObterConfiguracoes() throws Exception {
        ConfiguracaoEmpresaResponse response = new ConfiguracaoEmpresaResponse();
        response.setNomeEmpresa("Mundial Ciclo");
        response.setHorarioCheckin(LocalTime.of(8, 0));
        response.setHorarioCheckout(LocalTime.of(18, 0));
        when(configuracaoService.obterConfiguracoes()).thenReturn(response);

        mockMvc.perform(get("/api/configuracoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeEmpresa").value("Mundial Ciclo"));
    }

    @Test
    void deveRetornar400QuandoCheckoutAnteriorAoCheckin() throws Exception {
        AtualizarConfiguracaoRequest request = new AtualizarConfiguracaoRequest();
        request.setNomeEmpresa("Mundial Ciclo");
        request.setHorarioCheckin(LocalTime.of(18, 0));
        request.setHorarioCheckout(LocalTime.of(8, 0));
        when(configuracaoService.salvarConfiguracoes(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new IllegalArgumentException("Horário de checkout deve ser posterior ao horário de check-in"));

        mockMvc.perform(post("/api/configuracoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
