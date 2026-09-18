package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.PontoEletronicoResponse;
import com.empresa.mvcpontoeletronico.dtos.RegistrarPontoRequest;
import com.empresa.mvcpontoeletronico.security.JwtRequestFilter;
import com.empresa.mvcpontoeletronico.services.PontoEletronicoService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PontoEletronicoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class PontoEletronicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PontoEletronicoService pontoService;

    @Test
    void deveRegistrarPontoComSucesso() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        RegistrarPontoRequest request = RegistrarPontoRequest.builder()
                .usuarioId(usuarioId)
                .dataHora(LocalDateTime.of(2026, 9, 17, 9, 0))
                .build();
        PontoEletronicoResponse response = PontoEletronicoResponse.builder()
                .id(UUID.randomUUID())
                .usuarioId(usuarioId)
                .tipoPonto("ENTRADA_1")
                .build();
        when(pontoService.registrarPonto(any(RegistrarPontoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/pontos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoPonto").value("ENTRADA_1"));
    }

    @Test
    void deveRetornar400QuandoRequisicaoSemUsuarioId() throws Exception {
        RegistrarPontoRequest request = RegistrarPontoRequest.builder().build();

        mockMvc.perform(post("/api/pontos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveConsultarPontosPorData() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        when(pontoService.consultarPontosPorData(eq(usuarioId), any())).thenReturn(
                List.of(PontoEletronicoResponse.builder().usuarioId(usuarioId).tipoPonto("ENTRADA_1").build()));

        mockMvc.perform(get("/api/pontos/usuario/{usuarioId}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoPonto").value("ENTRADA_1"));
    }
}
