package com.empresa.arquiteturalimpa.application.usuario;

import com.empresa.arquiteturalimpa.application.usuario.dto.CriarUsuarioRequest;
import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import com.empresa.arquiteturalimpa.domain.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComCpfComoSenhaTemporariaQuandoSenhaNaoForInformada() {
        CriarUsuarioRequest request = new CriarUsuarioRequest(
                "Ana", "ana@empresa.com", null, "123.456.789-01", RoleType.FUNCIONARIO);
        UUID usuarioId = UUID.randomUUID();

        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(passwordHasher.hash("12345678901")).thenReturn("hash-cpf");
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
        assertThat(response.getEmail()).isEqualTo("ana@empresa.com");
        verify(passwordHasher).hash("12345678901");
    }

    @Test
    void naoDeveTrocarSenhaQuandoSenhaAtualForIncorreta() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(usuarioId).senha("hash-atual").build();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordHasher.matches("senha-errada", "hash-atual")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.trocarSenha(usuarioId, "senha-errada", "NovaSenha1", "NovaSenha1"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Senha atual incorreta");

        verify(passwordHasher, never()).hash("NovaSenha1");
        verify(usuarioRepository, never()).save(usuario);
    }
}