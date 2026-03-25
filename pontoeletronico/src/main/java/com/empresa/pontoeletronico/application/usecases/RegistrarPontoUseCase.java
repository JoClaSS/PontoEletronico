package com.empresa.pontoeletronico.application.usecases;

import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import com.empresa.pontoeletronico.domain.entities.TipoPonto;
import com.empresa.pontoeletronico.domain.entities.Usuario;
import com.empresa.pontoeletronico.domain.repositories.PontoEletronicoRepository;
import com.empresa.pontoeletronico.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso para registrar ponto eletrônico
 */
@RequiredArgsConstructor
public class RegistrarPontoUseCase {
    
    private final PontoEletronicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public PontoEletronico executar(RegistrarPontoRequest request) {
        // Valida se o usuário existe e está ativo
        Usuario usuario = usuarioRepository.buscarPorId(request.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        if (!usuario.podeRegistrarPonto()) {
            throw new IllegalStateException("Usuário não está ativo para registrar ponto");
        }
        
        // Valida o tipo de ponto baseado no último registro
        TipoPonto tipoProximoPonto = determinarProximoTipoPonto(request.getUsuarioId());
        
        if (request.getTipo() != null && !request.getTipo().equals(tipoProximoPonto)) {
            throw new IllegalArgumentException(
                String.format("Tipo de ponto inválido. Esperado: %s, Informado: %s", 
                    tipoProximoPonto.getDescricao(), request.getTipo().getDescricao())
            );
        }
        
        // Cria o registro de ponto
        PontoEletronico ponto = PontoEletronico.builder()
            .id(UUID.randomUUID().toString())
            .usuarioId(request.getUsuarioId())
            .dataHora(request.getDataHora() != null ? request.getDataHora() : LocalDateTime.now())
            .tipo(tipoProximoPonto)
            .localizacao(request.getLocalizacao())
            .observacao(request.getObservacao())
            .dispositivoId(request.getDispositivoId())
            .build();
        
        // Valida se o registro é válido
        if (!ponto.isValido()) {
            throw new IllegalArgumentException("Registro de ponto inválido");
        }
        
        // Salva o registro
        return pontoRepository.salvar(ponto);
    }
    
    private TipoPonto determinarProximoTipoPonto(String usuarioId) {
        return pontoRepository.buscarUltimoRegistroPorUsuario(usuarioId)
            .map(ultimoPonto -> {
                switch (ultimoPonto.getTipo()) {
                    case ENTRADA -> {
                        return TipoPonto.SAIDA_ALMOCO;
                    }
                    case SAIDA_ALMOCO -> {
                        return TipoPonto.ENTRADA_ALMOCO;
                    }
                    case ENTRADA_ALMOCO -> {
                        return TipoPonto.SAIDA;
                    }
                    case SAIDA -> {
                        return TipoPonto.ENTRADA;
                    }
                    default -> {
                        return TipoPonto.ENTRADA;
                    }
                }
            })
            .orElse(TipoPonto.ENTRADA); // Primeiro registro do dia
    }
}