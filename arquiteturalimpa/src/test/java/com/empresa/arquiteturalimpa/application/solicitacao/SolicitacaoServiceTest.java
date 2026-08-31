package com.empresa.arquiteturalimpa.application.solicitacao;

import com.empresa.arquiteturalimpa.application.solicitacao.dto.CriarSolicitacaoRequest;
import com.empresa.arquiteturalimpa.application.solicitacao.dto.SolicitacaoResponse;
import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import com.empresa.arquiteturalimpa.domain.model.MotivoSolicitacao;
import com.empresa.arquiteturalimpa.domain.model.Solicitacao;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.MotivoSolicitacaoRepository;
import com.empresa.arquiteturalimpa.domain.repository.SolicitacaoRepository;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private MotivoSolicitacaoRepository motivoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SolicitacaoService solicitacaoService;

    @Test
    void deveCriarSolicitacaoAbertaQuandoMotivoNaoExigirAnexo() {
        UUID usuarioId = UUID.randomUUID();
        UUID motivoId = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(usuarioId).nome("Ana").role(RoleType.FUNCIONARIO).build();
        MotivoSolicitacao motivo = MotivoSolicitacao.builder().id(motivoId).descricao("Ajuste de ponto").requerAnexo(false).build();
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
                .usuarioId(usuarioId)
                .motivoId(motivoId)
                .dataReferencia(LocalDate.of(2026, 8, 20))
                .descricao("Esqueci de registrar a saída")
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(solicitacaoRepository.existsByUsuarioIdAndDataReferenciaAndStatus(
                usuarioId, request.getDataReferencia(), com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao.ABERTO)).thenReturn(false);
        when(motivoRepository.findById(motivoId)).thenReturn(Optional.of(motivo));
        when(solicitacaoRepository.save(org.mockito.ArgumentMatchers.any(Solicitacao.class))).thenAnswer(invocation -> {
            Solicitacao solicitacao = invocation.getArgument(0);
            solicitacao.setId(UUID.randomUUID());
            return solicitacao;
        });

        SolicitacaoResponse response = solicitacaoService.criarSolicitacao(request);

        assertThat(response.getStatus()).isEqualTo(com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao.ABERTO);
        assertThat(response.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(response.getMotivo().getDescricao()).isEqualTo("Ajuste de ponto");
        verify(solicitacaoRepository).save(org.mockito.ArgumentMatchers.any(Solicitacao.class));
    }

    @Test
    void naoDeveCriarSolicitacaoSemAnexoQuandoMotivoExigirDocumento() {
        UUID usuarioId = UUID.randomUUID();
        UUID motivoId = UUID.randomUUID();
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
                .usuarioId(usuarioId)
                .motivoId(motivoId)
                .dataReferencia(LocalDate.of(2026, 8, 20))
                .descricao("Atestado")
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(Usuario.builder().id(usuarioId).build()));
        when(solicitacaoRepository.existsByUsuarioIdAndDataReferenciaAndStatus(
                usuarioId, request.getDataReferencia(), com.empresa.arquiteturalimpa.domain.enums.StatusSolicitacao.ABERTO)).thenReturn(false);
        when(motivoRepository.findById(motivoId)).thenReturn(Optional.of(MotivoSolicitacao.builder().requerAnexo(true).build()));

        assertThatThrownBy(() -> solicitacaoService.criarSolicitacao(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Este motivo requer anexo obrigatório");

        verify(solicitacaoRepository, never()).save(org.mockito.ArgumentMatchers.any(Solicitacao.class));
    }
}