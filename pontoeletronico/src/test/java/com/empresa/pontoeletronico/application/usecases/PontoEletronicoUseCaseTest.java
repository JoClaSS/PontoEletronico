package com.empresa.pontoeletronico.application.usecases;

import com.empresa.pontoeletronico.domain.entities.JornadaTrabalho;
import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import com.empresa.pontoeletronico.domain.entities.TipoPonto;
import com.empresa.pontoeletronico.domain.entities.Usuario;
import com.empresa.pontoeletronico.domain.repositories.PontoEletronicoRepository;
import com.empresa.pontoeletronico.domain.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para casos de uso do sistema de ponto eletrônico
 * Usa mocks para isolamento dos testes e melhor performance
 */

/* 
@ExtendWith(MockitoExtension.class)
class PontoEletronicoUseCaseTest {
    
    @Mock
    private PontoEletronicoRepository pontoRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    private RegistrarPontoUseCase registrarPontoUseCase;
    private ConsultarPontosUseCase consultarPontosUseCase;
    private CalcularHorasTrabalhadasUseCase calcularHorasUseCase;
    
    private final String USUARIO_ID = "f1e2d3c4-b5a6-7890-cdef-123456789012";
    private Usuario usuarioMock;
    
    @BeforeEach
    void setUp() {
        registrarPontoUseCase = new RegistrarPontoUseCase(pontoRepository, usuarioRepository);
        consultarPontosUseCase = new ConsultarPontosUseCase(pontoRepository, usuarioRepository);
        calcularHorasUseCase = new CalcularHorasTrabalhadasUseCase(pontoRepository, usuarioRepository);
        
        // Setup usuario mock
        JornadaTrabalho jornada = JornadaTrabalho.builder()
            .id(UUID.randomUUID().toString())
            .nome("Jornada Teste")
            .inicioExpediente(LocalTime.of(8, 0))
            .fimExpediente(LocalTime.of(17, 0))
            .inicioIntervalo(LocalTime.of(12, 0))
            .fimIntervalo(LocalTime.of(13, 0))
            .cargaHorariaDiaria(Duration.ofHours(8))
            .permiteHoraExtra(true)
            .limiteHoraExtra(Duration.ofHours(2))
            .build();
        
        usuarioMock = Usuario.builder()
            .id(USUARIO_ID)
            .email("test@empresa.com")
            .nome("Usuário Teste")
            .cargo("Testador")
            .departamento("TI")
            .ativo(true)
            .jornada(jornada)
            .build();
        
        // Mock padrão para buscar usuário
        when(usuarioRepository.buscarPorId(USUARIO_ID))
            .thenReturn(Optional.of(usuarioMock));
    }
    
    @Test
    void deveRegistrarSequenciaDePontosCorretamente() {
        LocalDate dataHoje = LocalDate.now();
        
        // Mock: primeiro registro (não há último registro)
        when(pontoRepository.buscarUltimoRegistroPorUsuario(USUARIO_ID))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(criarPontoMock(TipoPonto.ENTRADA, LocalDateTime.of(dataHoje, LocalTime.of(8, 0)))))
            .thenReturn(Optional.of(criarPontoMock(TipoPonto.SAIDA_ALMOCO, LocalDateTime.of(dataHoje, LocalTime.of(12, 0)))))
            .thenReturn(Optional.of(criarPontoMock(TipoPonto.ENTRADA_ALMOCO, LocalDateTime.of(dataHoje, LocalTime.of(13, 0)))));
        
        // Mock salvar ponto - retorna o mesmo ponto com ID
        when(pontoRepository.salvar(any(PontoEletronico.class)))
            .thenAnswer(invocation -> {
                PontoEletronico ponto = invocation.getArgument(0);
                return PontoEletronico.builder()
                    .id(UUID.randomUUID().toString())
                    .usuarioId(ponto.getUsuarioId())
                    .dataHora(ponto.getDataHora())
                    .tipo(ponto.getTipo())
                    .localizacao(ponto.getLocalizacao())
                    .observacao(ponto.getObservacao())
                    .dispositivoId(ponto.getDispositivoId())
                    .build();
            });
        
        // 1. Registra entrada 1
        RegistrarPontoRequest requestEntrada = RegistrarPontoRequest.builder()
            .usuarioId(USUARIO_ID)
            .dataHora(LocalDateTime.of(dataHoje, LocalTime.of(8, 0)))
            .localizacao("Escritório")
            .build();
        
        PontoEletronico entrada = registrarPontoUseCase.executar(requestEntrada);
        assertEquals(TipoPonto.ENTRADA_1, entrada.getTipo());
        
        // 2. Registra saída 1
        RegistrarPontoRequest requestSaida1 = RegistrarPontoRequest.builder()
            .usuarioId(USUARIO_ID)
            .dataHora(LocalDateTime.of(dataHoje, LocalTime.of(12, 0)))
            .localizacao("Escritório")
            .build();
        
        PontoEletronico saida1 = registrarPontoUseCase.executar(requestSaida1);
        assertEquals(TipoPonto.SAIDA_1, saida1.getTipo());
        
        // 3. Registra entrada 2
        RegistrarPontoRequest requestEntrada2 = RegistrarPontoRequest.builder()
            .usuarioId(USUARIO_ID)
            .dataHora(LocalDateTime.of(dataHoje, LocalTime.of(13, 0)))
            .localizacao("Escritório")
            .build();
        
        PontoEletronico entrada2 = registrarPontoUseCase.executar(requestEntrada2);
        assertEquals(TipoPonto.ENTRADA_2, entrada2.getTipo());
        
        // 4. Registra saída 2
        RegistrarPontoRequest requestSaida2 = RegistrarPontoRequest.builder()
            .usuarioId(USUARIO_ID)
            .dataHora(LocalDateTime.of(dataHoje, LocalTime.of(17, 0)))
            .localizacao("Escritório")
            .build();
        
        PontoEletronico saida2 = registrarPontoUseCase.executar(requestSaida2);
        assertEquals(TipoPonto.SAIDA_2, saida2.getTipo());
        
        // Verifica se salvar foi chamado 4 vezes
        verify(pontoRepository, times(4)).salvar(any(PontoEletronico.class));
    }
    
    @Test
    void deveCalcularHorasTrabalhadasCorretamente() {
        LocalDate dataHoje = LocalDate.now();
        
        // Mock pontos de jornada completa
        List<PontoEletronico> pontosDia = criarJornadaCompleta(dataHoje);
        when(pontoRepository.buscarPorUsuarioEData(USUARIO_ID, dataHoje))
            .thenReturn(pontosDia);
        
        // Calcula horas trabalhadas
        RelatorioHoras relatorio = calcularHorasUseCase.calcularHorasDia(USUARIO_ID, dataHoje);
        
        // Verifica se calculou 8 horas (4 de manhã + 4 de tarde)
        assertEquals(8 * 60, relatorio.getHorasTrabalhadas().toMinutes());
        assertEquals("08:00", relatorio.getHorasTrabalhadasFormatadas());
        assertEquals("+00:00", relatorio.getSaldoFormatado()); // Saldo zero (cumpriu a jornada)
        assertFalse(relatorio.temHorasDebito());
        assertFalse(relatorio.temHorasExcedentes());
        
        // Verifica se o repositório foi consultado
        verify(pontoRepository).buscarPorUsuarioEData(USUARIO_ID, dataHoje);
        verify(usuarioRepository).buscarPorId(USUARIO_ID);
    }
    
    @Test
    void deveDetectarHorasExtras() {
        LocalDate dataHoje = LocalDate.now();
        
        // Mock pontos com hora extra
        List<PontoEletronico> pontosComExtra = criarJornadaComHoraExtra(dataHoje);
        when(pontoRepository.buscarPorUsuarioEData(USUARIO_ID, dataHoje))
            .thenReturn(pontosComExtra);
        
        // Calcula horas trabalhadas
        RelatorioHoras relatorio = calcularHorasUseCase.calcularHorasDia(USUARIO_ID, dataHoje);
        
        // Verifica se detectou horas extras (9 horas trabalhadas)
        assertEquals(9 * 60, relatorio.getHorasTrabalhadas().toMinutes());
        assertEquals("09:00", relatorio.getHorasTrabalhadasFormatadas());
        assertEquals("+01:00", relatorio.getSaldoFormatado()); // 1 hora extra
        assertTrue(relatorio.temHorasExcedentes());
        assertFalse(relatorio.temHorasDebito());
        
        verify(pontoRepository).buscarPorUsuarioEData(USUARIO_ID, dataHoje);
    }
    
    @Test
    void deveRejeitarRegistroDeUsuarioInativo() {
        // Mock usuário inativo
        Usuario usuarioInativo = Usuario.builder()
            .id(USUARIO_ID)
            .email("inativo@empresa.com")
            .nome("Usuário Inativo")
            .ativo(false) // Usuário desativado
            .build();
        
        when(usuarioRepository.buscarPorId(USUARIO_ID))
            .thenReturn(Optional.of(usuarioInativo));
        
        RegistrarPontoRequest request = RegistrarPontoRequest.builder()
            .usuarioId(USUARIO_ID)
            .dataHora(LocalDateTime.now())
            .build();
        
        // Deve lançar exceção para usuário inativo
        assertThrows(IllegalStateException.class, () -> {
            registrarPontoUseCase.executar(request);
        });
        
        // Verifica que não tentou salvar ponto
        verify(pontoRepository, never()).salvar(any(PontoEletronico.class));
    }
    
    @Test 
    void deveConsultarPontosCorretamente() {
        LocalDate dataHoje = LocalDate.now();
        List<PontoEletronico> pontosMock = criarJornadaCompleta(dataHoje);
        
        when(pontoRepository.buscarPorUsuarioEData(USUARIO_ID, dataHoje))
            .thenReturn(pontosMock);
        
        List<PontoEletronico> pontos = consultarPontosUseCase.consultarPorData(USUARIO_ID, dataHoje);
        
        assertEquals(4, pontos.size());
        assertEquals(TipoPonto.ENTRADA, pontos.get(0).getTipo());
        assertEquals(TipoPonto.SAIDA_ALMOCO, pontos.get(1).getTipo());
        assertEquals(TipoPonto.ENTRADA_ALMOCO, pontos.get(2).getTipo());
        assertEquals(TipoPonto.SAIDA, pontos.get(3).getTipo());
        
        verify(pontoRepository).buscarPorUsuarioEData(USUARIO_ID, dataHoje);
        verify(usuarioRepository).buscarPorId(USUARIO_ID);
    }
    
    // Métodos auxiliares para criar mocks
    private PontoEletronico criarPontoMock(TipoPonto tipo, LocalDateTime dataHora) {
        return PontoEletronico.builder()
            .id(UUID.randomUUID().toString())
            .usuarioId(USUARIO_ID)
            .dataHora(dataHora)
            .tipo(tipo)
            .localizacao("Escritório")
            .build();
    }
    
    private List<PontoEletronico> criarJornadaCompleta(LocalDate data) {
        return List.of(
            criarPontoMock(TipoPonto.ENTRADA_1, LocalDateTime.of(data, LocalTime.of(8, 0))),
            criarPontoMock(TipoPonto.SAIDA_1, LocalDateTime.of(data, LocalTime.of(12, 0))),
            criarPontoMock(TipoPonto.ENTRADA_2, LocalDateTime.of(data, LocalTime.of(13, 0))),
            criarPontoMock(TipoPonto.SAIDA_2, LocalDateTime.of(data, LocalTime.of(17, 0)))
        );
    }
    
    private List<PontoEletronico> criarJornadaComHoraExtra(LocalDate data) {
        return List.of(
            criarPontoMock(TipoPonto.ENTRADA_1, LocalDateTime.of(data, LocalTime.of(8, 0))),
            criarPontoMock(TipoPonto.SAIDA_1, LocalDateTime.of(data, LocalTime.of(12, 0))),
            criarPontoMock(TipoPonto.ENTRADA_2, LocalDateTime.of(data, LocalTime.of(13, 0))),
            criarPontoMock(TipoPonto.SAIDA_2, LocalDateTime.of(data, LocalTime.of(18, 0))) // 1 hora extra
        );
    }
} */