package com.empresa.pontoeletronico.application.usecases;

import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import com.empresa.pontoeletronico.domain.repositories.PontoEletronicoRepository;
import com.empresa.pontoeletronico.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso para consultar registros de ponto
 */
@RequiredArgsConstructor
public class ConsultarPontosUseCase {
    
    private final PontoEletronicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public List<PontoEletronico> consultarPorData(String usuarioId, LocalDate data) {
        // Valida se o usuário existe
        usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        return pontoRepository.buscarPorUsuarioEData(usuarioId, data);
    }
    
    public List<PontoEletronico> consultarPorPeriodo(String usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        // Valida se o usuário existe
        usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Valida o período
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
        }
        
        return pontoRepository.buscarPorUsuarioEPeriodo(usuarioId, dataInicio, dataFim);
    }
    
    public List<PontoEletronico> consultarPontosHoje(String usuarioId) {
        return consultarPorData(usuarioId, LocalDate.now());
    }
}