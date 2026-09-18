package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.CriarSolicitacaoRequest;
import com.empresa.mvcpontoeletronico.dtos.MotivoSolicitacaoResponse;
import com.empresa.mvcpontoeletronico.dtos.SolicitacaoResponse;
import com.empresa.mvcpontoeletronico.security.JwtRequestFilter;
import com.empresa.mvcpontoeletronico.services.PontoEletronicoService;
import com.empresa.mvcpontoeletronico.services.SolicitacaoService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SolicitacaoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class SolicitacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SolicitacaoService solicitacaoService;

    @MockBean
    private PontoEletronicoService pontoEletronicoService;

    @Test
    void deveCriarSolicitacaoComSucesso() throws Exception {
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
                .dataReferencia(LocalDate.of(2026, 9, 17))
                .usuarioId(UUID.randomUUID())
                .motivoId(UUID.randomUUID())
                .descricao("Esqueci de bater o ponto")
                .build();
        SolicitacaoResponse response = SolicitacaoResponse.builder()
                .id(UUID.randomUUID())
                .descricao(request.getDescricao())
                .build();
        when(solicitacaoService.criarSolicitacao(any(CriarSolicitacaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/solicitacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Esqueci de bater o ponto"));
    }

    @Test
    void deveRetornar400QuandoDescricaoEmBranco() throws Exception {
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
                .dataReferencia(LocalDate.of(2026, 9, 17))
                .usuarioId(UUID.randomUUID())
                .motivoId(UUID.randomUUID())
                .descricao("")
                .build();

        mockMvc.perform(post("/api/solicitacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarMotivosAtivos() throws Exception {
        when(solicitacaoService.listarMotivosAtivos()).thenReturn(
                List.of(MotivoSolicitacaoResponse.builder().id(UUID.randomUUID()).descricao("Esquecimento").build()));

        mockMvc.perform(get("/api/solicitacoes/motivos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Esquecimento"));
    }
}
