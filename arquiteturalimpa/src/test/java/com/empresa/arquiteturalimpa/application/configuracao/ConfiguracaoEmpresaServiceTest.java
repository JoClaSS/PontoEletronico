package com.empresa.arquiteturalimpa.application.configuracao;

import com.empresa.arquiteturalimpa.application.configuracao.dto.AtualizarConfiguracaoRequest;
import com.empresa.arquiteturalimpa.application.configuracao.dto.ConfiguracaoEmpresaResponse;
import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import com.empresa.arquiteturalimpa.domain.model.ConfiguracaoEmpresa;
import com.empresa.arquiteturalimpa.domain.repository.ConfiguracaoEmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfiguracaoEmpresaServiceTest {

    @Mock
    private ConfiguracaoEmpresaRepository configuracaoRepository;

    @InjectMocks
    private ConfiguracaoEmpresaService configuracaoService;

    @Test
    void deveRetornarConfiguracaoPadraoQuandoNaoExisteConfiguracaoSalva() {
        when(configuracaoRepository.findFirstConfiguration()).thenReturn(Optional.empty());

        ConfiguracaoEmpresaResponse response = configuracaoService.obterConfiguracoes();

        assertThat(response.getNomeEmpresa()).isEqualTo("Mundial Ciclo");
        assertThat(response.getHorarioCheckin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(response.getHorarioCheckout()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void naoDeveSalvarConfiguracaoQuandoCheckoutNaoForPosteriorAoCheckin() {
        AtualizarConfiguracaoRequest request = new AtualizarConfiguracaoRequest();
        request.setNomeEmpresa("Mundial Ciclo");
        request.setHorarioCheckin(LocalTime.of(18, 0));
        request.setHorarioCheckout(LocalTime.of(8, 0));

        when(configuracaoRepository.findFirstConfiguration()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> configuracaoService.salvarConfiguracoes(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Horário de checkout deve ser posterior ao horário de check-in");

        verify(configuracaoRepository, never()).save(any(ConfiguracaoEmpresa.class));
    }

    @Test
    void deveSalvarConfiguracaoComSucesso() {
        AtualizarConfiguracaoRequest request = new AtualizarConfiguracaoRequest();
        request.setNomeEmpresa("Mundial Ciclo");
        request.setHorarioCheckin(LocalTime.of(8, 0));
        request.setHorarioCheckout(LocalTime.of(18, 0));
        request.setIntervaloMinimoMinutos(15);

        when(configuracaoRepository.findFirstConfiguration()).thenReturn(Optional.empty());
        when(configuracaoRepository.save(any(ConfiguracaoEmpresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfiguracaoEmpresaResponse response = configuracaoService.salvarConfiguracoes(request);

        assertThat(response.getNomeEmpresa()).isEqualTo("Mundial Ciclo");
        assertThat(response.getIntervaloMinimoMinutos()).isEqualTo(15);
    }
}
