package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dtos.CriarUsuarioRequest;
import com.empresa.mvcpontoeletronico.dtos.UsuarioResponse;
import com.empresa.mvcpontoeletronico.entities.RoleType;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.security.JwtRequestFilter;
import com.empresa.mvcpontoeletronico.services.UsuarioService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de fatia web (camada Controller) com o filtro JWT excluído da fatia.
 */
@WebMvcTest(controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void deveListarUsuarios() throws Exception {
        UsuarioResponse usuario = UsuarioResponse.builder()
                .id(UUID.randomUUID())
                .nome("Ana")
                .email("ana@empresa.com")
                .build();
        when(usuarioService.listarTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
        UUID id = UUID.randomUUID();
        when(usuarioService.buscarPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarUsuarioQuandoEncontrado() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(id).nome("Ana").email("ana@empresa.com").build();
        when(usuarioService.buscarPorId(id)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    @Test
    void deveRetornar400QuandoCriarUsuarioComCamposInvalidos() throws Exception {
        CriarUsuarioRequest request = new CriarUsuarioRequest();
        request.setNome("");
        request.setEmail("email-invalido");
        request.setCpf("123");
        request.setRole(RoleType.FUNCIONARIO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarUsuarioComSucesso() throws Exception {
        CriarUsuarioRequest request = new CriarUsuarioRequest();
        request.setNome("Ana");
        request.setEmail("ana@empresa.com");
        request.setCpf("123.456.789-00");
        request.setRole(RoleType.FUNCIONARIO);

        UsuarioResponse response = UsuarioResponse.builder()
                .id(UUID.randomUUID())
                .nome("Ana")
                .email("ana@empresa.com")
                .build();
        when(usuarioService.criarUsuario(any(CriarUsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ana"));
    }
}
