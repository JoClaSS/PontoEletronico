package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.entities.JornadaTrabalho;
import com.empresa.mvcpontoeletronico.repositories.JornadaTrabalhoRepository;
import com.empresa.mvcpontoeletronico.security.JwtRequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Note: o controller depende diretamente do Repository (sem camada de Service),
 * evidenciando o acoplamento direto entre apresentação e acesso a dados no MVC.
 */
@WebMvcTest(controllers = JornadaTrabalhoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class JornadaTrabalhoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JornadaTrabalhoRepository jornadaRepository;

    @Test
    void deveListarJornadas() throws Exception {
        JornadaTrabalho jornada = JornadaTrabalho.builder().nome("40 horas semanais").horasSemanais(40).diasTrabalhados(5).build();
        when(jornadaRepository.findAllByOrderByHorasSemanaisAsc()).thenReturn(List.of(jornada));

        mockMvc.perform(get("/api/jornadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("40 horas semanais"));
    }

    @Test
    void deveRetornar404QuandoJornadaNaoEncontrada() throws Exception {
        UUID id = UUID.randomUUID();
        when(jornadaRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/jornadas/{id}", id))
                .andExpect(status().isNotFound());
    }
}
