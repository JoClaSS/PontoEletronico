package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dto.LoginRequest;
import com.empresa.mvcpontoeletronico.dto.LoginResponse;
import com.empresa.mvcpontoeletronico.entities.RoleType;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import com.empresa.mvcpontoeletronico.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveAutenticarUsuarioEEmitirTokenQuandoAuthenticationManagerAceitarCredenciais() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Ana")
                .email("ana@empresa.com")
                .senha("hash-salvo")
                .role(RoleType.FUNCIONARIO)
                .primeiroLogin(true)
                .build();

        when(usuarioRepository.findByEmail("ana@empresa.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("Senha123")).thenReturn("hash-tentativa");
        when(passwordEncoder.matches("Senha123", "hash-salvo")).thenReturn(true);
        when(jwtUtil.generateToken("ana@empresa.com", usuarioId, "FUNCIONARIO")).thenReturn("token-jwt");

        LoginResponse response = authService.login(new LoginRequest("ana@empresa.com", "Senha123"));

        assertThat(response.getToken()).isEqualTo("token-jwt");
        assertThat(response.getUsuario()).isSameAs(usuario);
        assertThat(response.isPrimeiroLogin()).isTrue();
        verify(authenticationManager).authenticate(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void naoDeveGerarTokenQuandoAuthenticationManagerRejeitarCredenciais() {
        Usuario usuario = Usuario.builder()
                .email("ana@empresa.com")
                .senha("hash-salvo")
                .build();
        when(usuarioRepository.findByEmail("ana@empresa.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha-incorreta")).thenReturn("hash-tentativa");
        when(passwordEncoder.matches("senha-incorreta", "hash-salvo")).thenReturn(false);
        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BadCredentialsException("rejeitada"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("ana@empresa.com", "senha-incorreta")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(jwtUtil, never()).generateToken(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString());
    }
}