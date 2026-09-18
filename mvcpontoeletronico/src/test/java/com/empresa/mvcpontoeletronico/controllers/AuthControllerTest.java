package com.empresa.mvcpontoeletronico.controllers;

import com.empresa.mvcpontoeletronico.dto.LoginRequest;
import com.empresa.mvcpontoeletronico.dto.LoginResponse;
import com.empresa.mvcpontoeletronico.entities.RoleType;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.security.JwtRequestFilter;
import com.empresa.mvcpontoeletronico.services.AuthService;
import com.empresa.mvcpontoeletronico.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de fatia web do AuthController. Evidencia que exceções não tratadas
 * explicitamente pelo controller caem no catch genérico e retornam 400 com corpo vazio.
 */
@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void deveAutenticarComSucesso() throws Exception {
        LoginRequest request = new LoginRequest("ana@empresa.com", "Senha123");
        Usuario usuario = Usuario.builder().nome("Ana").email("ana@empresa.com").role(RoleType.FUNCIONARIO).build();
        LoginResponse response = LoginResponse.builder().token("token-jwt").usuario(usuario).primeiroLogin(false).build();
        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    @Test
    void deveRetornar400ComCorpoVazioQuandoFalhaAutenticacao() throws Exception {
        LoginRequest request = new LoginRequest("ana@empresa.com", "senha-errada");
        when(authService.login(request)).thenThrow(new UsernameNotFoundException("Usuário não encontrado"));

        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        org.assertj.core.api.Assertions.assertThat(body).isEmpty();
    }
}
