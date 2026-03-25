package com.empresa.pontoeletronico.domain.repositories;

import com.empresa.pontoeletronico.domain.entities.PontoEletronico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de PontoEletronico
 * Segue o princípio de inversão de dependência da arquitetura limpa
 */
public interface PontoEletronicoRepository {
    
    /**
     * Salva um registro de ponto eletrônico
     */
    PontoEletronico salvar(PontoEletronico pontoEletronico);
    
    /**
     * Busca registro por ID
     */
    Optional<PontoEletronico> buscarPorId(String id);
    
    /**
     * Busca todos os registros de ponto de um usuário em uma data específica
     */
    List<PontoEletronico> buscarPorUsuarioEData(String usuarioId, LocalDate data);
    
    /**
     * Busca todos os registros de ponto de um usuário em um período
     */
    List<PontoEletronico> buscarPorUsuarioEPeriodo(String usuarioId, LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca o último registro de ponto de um usuário
     */
    Optional<PontoEletronico> buscarUltimoRegistroPorUsuario(String usuarioId);
    
    /**
     * Remove um registro de ponto
     */
    void remover(String id);
    
    /**
     * Lista todos os registros de ponto (com paginação futura)
     */
    List<PontoEletronico> listarTodos();
}