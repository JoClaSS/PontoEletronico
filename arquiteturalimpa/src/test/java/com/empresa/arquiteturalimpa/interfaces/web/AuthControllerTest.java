package com.empresa.arquiteturalimpa.interfaces.web;

import com.empresa.arquiteturalimpa.application.auth.AuthService;
import com.empresa.arquiteturalimpa.application.auth.dto.LoginRequest;
import com.empresa.arquiteturalimpa.application.auth.dto.LoginResponse;
import com.empresa.arquiteturalimpa.application.usuario.UsuarioService;
import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import com.empresa.arquiteturalimpa.domain.exception.AuthenticationFailedException;
import com.empresa.arquiteturalimpa.infrastructure.security.JwtRequestFilter;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de fatia web do AuthController. Evidencia que a exceção de domínio
 * é traduzida pelo GlobalExceptionHandler em 401 com corpo estruturado
 * (diferente do catch genérico de 400 vazio na versão MVC).
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
        UsuarioResponse usuario = UsuarioResponse.builder().nome("Ana").email("ana@empresa.com").build();
        LoginResponse response = LoginResponse.builder().token("token-jwt").usuario(usuario).primeiroLogin(false).build();
        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    @Test
    void deveRetornar401ComMensagemQuandoFalhaAutenticacao() throws Exception {
        LoginRequest request = new LoginRequest("ana@empresa.com", "senha-errada");
        when(authService.login(request)).thenThrow(new AuthenticationFailedException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }
}
