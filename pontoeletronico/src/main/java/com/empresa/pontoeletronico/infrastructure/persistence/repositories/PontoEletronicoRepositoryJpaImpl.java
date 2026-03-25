package com.empresa.pontoeletronico.infrastructure.persistence.repositories;

import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import com.empresa.pontoeletronico.domain.repositories.PontoEletronicoRepository;
import com.empresa.pontoeletronico.infrastructure.persistence.entities.PontoEletronicoJpaEntity;
import com.empresa.pontoeletronico.infrastructure.persistence.entities.UsuarioJpaEntity;
import com.empresa.pontoeletronico.infrastructure.persistence.mappers.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação JPA do repositório de PontoEletronico
 * Faz a ponte entre o domínio e o JPA, respeitando a inversão de dependências
 */
@Repository
@RequiredArgsConstructor
public class PontoEletronicoRepositoryJpaImpl implements PontoEletronicoRepository {
    
    private final PontoEletronicoJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    
    @Override
    public PontoEletronico salvar(PontoEletronico pontoEletronico) {
        // Busca o usuário JPA para associação
        UUID usuarioId = UUID.fromString(pontoEletronico.getUsuarioId());
        UsuarioJpaEntity usuarioEntity = usuarioJpaRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        
        // Converte para entidade JPA
        PontoEletronicoJpaEntity entity = PersistenceMapper.toJpaEntity(pontoEletronico, usuarioEntity);
        
        // Salva e retorna o domínio
        PontoEletronicoJpaEntity savedEntity = jpaRepository.save(entity);
        return PersistenceMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<PontoEletronico> buscarPorId(String id) {
        UUID uuid = UUID.fromString(id);
        return jpaRepository.findById(uuid)
            .map(PersistenceMapper::toDomain);
    }
    
    @Override
    public List<PontoEletronico> buscarPorUsuarioEData(String usuarioId, LocalDate data) {
        UUID uuid = UUID.fromString(usuarioId);
        return jpaRepository.findByUsuarioIdAndData(uuid, data)
            .stream()
            .map(PersistenceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PontoEletronico> buscarPorUsuarioEPeriodo(String usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        UUID uuid = UUID.fromString(usuarioId);
        return jpaRepository.findByUsuarioIdAndPeriodo(uuid, dataInicio, dataFim)
            .stream()
            .map(PersistenceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<PontoEletronico> buscarUltimoRegistroPorUsuario(String usuarioId) {
        UUID uuid = UUID.fromString(usuarioId);
        return jpaRepository.findUltimoRegistroPorUsuario(uuid)
            .map(PersistenceMapper::toDomain);
    }
    
    @Override
    public void remover(String id) {
        UUID uuid = UUID.fromString(id);
        jpaRepository.deleteById(uuid);
    }
    
    @Override
    public List<PontoEletronico> listarTodos() {
        return jpaRepository.findAll()
            .stream()
            .map(PersistenceMapper::toDomain)
            .collect(Collectors.toList());
    }
}