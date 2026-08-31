package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.CriarUsuarioRequest;
import com.empresa.mvcpontoeletronico.dtos.UsuarioResponse;
import com.empresa.mvcpontoeletronico.entities.RoleType;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComCpfComoSenhaTemporariaQuandoSenhaNaoForInformada() {
        CriarUsuarioRequest request = new CriarUsuarioRequest(
                "Ana", "ana@empresa.com", null, "123.456.789-01", RoleType.FUNCIONARIO);
        UUID usuarioId = UUID.randomUUID();

        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(passwordEncoder.encode("12345678901")).thenReturn("hash-cpf");
        when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(usuarioId);
            return usuario;
        });

        UsuarioResponse response = usuarioService.criarUsuario(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue())
                .extracting(Usuario::getSenha, Usuario::getAtivo, Usuario::getPrimeiroLogin)
                .containsExactly("hash-cpf", true, true);
        assertThat(response.getId()).isEqualTo(usuarioId);
        verify(passwordEncoder).encode("12345678901");
    }

    @Test
    void deveEncapsularFalhaDeSenhaAtualEmRuntimeException() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(usuarioId).senha("hash-atual").build();
        when(usuarioRepository.findById(usuarioId)).thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("senha-errada", "hash-atual")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.trocarSenha(usuarioId, "senha-errada", "NovaSenha1", "NovaSenha1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao trocar senha: Senha atual incorreta");

        verify(passwordEncoder, never()).encode("NovaSenha1");
        verify(usuarioRepository, never()).save(usuario);
    }
}