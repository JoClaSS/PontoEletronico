package com.empresa.arquiteturalimpa.application.auth;

import com.empresa.arquiteturalimpa.application.auth.dto.LoginRequest;
import com.empresa.arquiteturalimpa.application.auth.dto.LoginResponse;
import com.empresa.arquiteturalimpa.application.usuario.UsuarioService;
import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.domain.exception.AuthenticationFailedException;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import com.empresa.arquiteturalimpa.domain.security.PasswordHasher;
import com.empresa.arquiteturalimpa.domain.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveAutenticarUsuarioAtivoComCredenciaisValidas() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuario(usuarioId, true);
        UsuarioResponse usuarioResponse = UsuarioResponse.builder()
                .id(usuarioId)
                .nome("Ana")
                .email("ana@empresa.com")
                .build();

        when(usuarioRepository.findByEmail("ana@empresa.com")).thenReturn(Optional.of(usuario));
        when(passwordHasher.matches("Senha123", "hash-salvo")).thenReturn(true);
        when(tokenProvider.generateToken("ana@empresa.com", usuarioId, "FUNCIONARIO")).thenReturn("token-jwt");
        when(usuarioService.toResponse(usuario)).thenReturn(usuarioResponse);

        LoginResponse response = authService.login(new LoginRequest("ana@empresa.com", "Senha123"));

        assertThat(response.getToken()).isEqualTo("token-jwt");
        assertThat(response.getUsuario()).isSameAs(usuarioResponse);
        assertThat(response.isPrimeiroLogin()).isTrue();
        verify(tokenProvider).generateToken("ana@empresa.com", usuarioId, "FUNCIONARIO");
    }

    @Test
    void naoDeveGerarTokenQuandoSenhaForInvalida() {
        Usuario usuario = usuario(UUID.randomUUID(), true);
        when(usuarioRepository.findByEmail("ana@empresa.com")).thenReturn(Optional.of(usuario));
        when(passwordHasher.matches("senha-incorreta", "hash-salvo")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("ana@empresa.com", "senha-incorreta")))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Credenciais inválidas");

        verify(tokenProvider, never()).generateToken(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString());
        verify(usuarioService, never()).toResponse(usuario);
    }

    private Usuario usuario(UUID id, boolean ativo) {
        return Usuario.builder()
                .id(id)
                .nome("Ana")
                .email("ana@empresa.com")
                .senha("hash-salvo")
                .role(RoleType.FUNCIONARIO)
                .ativo(ativo)
                .primeiroLogin(true)
                .build();
    }
}