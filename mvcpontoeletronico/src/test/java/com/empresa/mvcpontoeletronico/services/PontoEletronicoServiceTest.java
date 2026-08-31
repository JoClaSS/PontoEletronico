package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dtos.ConfiguracaoEmpresaResponse;
import com.empresa.mvcpontoeletronico.dtos.PontoEletronicoResponse;
import com.empresa.mvcpontoeletronico.dtos.RegistrarPontoRequest;
import com.empresa.mvcpontoeletronico.entities.PontoEletronico;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.PontoEletronicoRepository;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PontoEletronicoServiceTest {

    @Mock
    private PontoEletronicoRepository pontoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConfiguracaoEmpresaService configuracaoService;

    @InjectMocks
    private PontoEletronicoService pontoService;

    @Test
    void deveRegistrarPrimeiroPontoDoDiaComoEntradaUm() {
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime dataHora = LocalDateTime.of(2026, 8, 28, 9, 0);
        Usuario usuario = Usuario.builder().id(usuarioId).nome("Ana").build();
        RegistrarPontoRequest request = RegistrarPontoRequest.builder()
                .usuarioId(usuarioId)
                .dataHora(dataHora)
                .localizacao("Matriz")
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(configuracaoService.obterConfiguracoes()).thenReturn(configuracao(0));
        when(pontoRepository.findByUsuarioIdAndData(usuarioId, dataHora.toLocalDate())).thenReturn(Optional.empty());
        when(pontoRepository.save(org.mockito.ArgumentMatchers.any(PontoEletronico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PontoEletronicoResponse response = pontoService.registrarPonto(request);

        assertThat(response.getDataHora()).isEqualTo(dataHora);
        assertThat(response.getTipoPonto()).isEqualTo("ENTRADA_1");
        assertThat(response.getLocalizacao()).isEqualTo("Matriz");
    }

    @Test
    void naoDeveRegistrarPontoAntesDoIntervaloMinimo() {
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime primeiraEntrada = LocalDateTime.of(2026, 8, 28, 9, 0);
        LocalDateTime novaMarcacao = primeiraEntrada.plusMinutes(10);
        PontoEletronico registroExistente = PontoEletronico.builder()
                .usuario(Usuario.builder().id(usuarioId).build())
                .data(primeiraEntrada.toLocalDate())
                .entrada1(primeiraEntrada)
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(Usuario.builder().id(usuarioId).build()));
        when(configuracaoService.obterConfiguracoes()).thenReturn(configuracao(15));
        when(pontoRepository.findByUsuarioIdAndData(usuarioId, primeiraEntrada.toLocalDate())).thenReturn(Optional.of(registroExistente));

        assertThatThrownBy(() -> pontoService.registrarPonto(RegistrarPontoRequest.builder()
                .usuarioId(usuarioId)
                .dataHora(novaMarcacao)
                .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Deve haver um intervalo mínimo de 15 minuto(s) entre registros");

        verify(pontoRepository, never()).save(org.mockito.ArgumentMatchers.any(PontoEletronico.class));
    }

    private ConfiguracaoEmpresaResponse configuracao(int intervaloMinimoMinutos) {
        ConfiguracaoEmpresaResponse configuracao = new ConfiguracaoEmpresaResponse();
        configuracao.setHorarioCheckin(LocalTime.of(8, 0));
        configuracao.setHorarioCheckout(LocalTime.of(18, 0));
        configuracao.setIntervaloMinimoMinutos(intervaloMinimoMinutos);
        return configuracao;
    }
}