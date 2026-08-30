package com.empresa.arquiteturalimpa.infrastructure.persistence.adapter;

import com.empresa.arquiteturalimpa.domain.model.PontoEletronico;
import com.empresa.arquiteturalimpa.domain.repository.PontoEletronicoRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.PontoEletronicoJpaEntity;
import com.empresa.arquiteturalimpa.infrastructure.persistence.jpa.PontoEletronicoJpaRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapta o output port PontoEletronicoRepository (domínio) para o Spring Data JPA (infraestrutura).
 */
@Component
@RequiredArgsConstructor
public class PontoEletronicoRepositoryAdapter implements PontoEletronicoRepository {

    private final PontoEletronicoJpaRepository jpaRepository;

    @Override
    public PontoEletronico save(PontoEletronico ponto) {
        return toDomain(jpaRepository.save(toEntity(ponto)));
    }

    @Override
    public Optional<PontoEletronico> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PontoEletronico> findByUsuarioIdAndData(UUID usuarioId, LocalDate data) {
        return jpaRepository.findByUsuarioIdAndData(usuarioId, data).map(this::toDomain);
    }

    @Override
    public List<PontoEletronico> findByUsuarioIdAndPeriodo(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        return jpaRepository.findByUsuarioIdAndPeriodo(usuarioId, dataInicio, dataFim).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<PontoEletronico> findByDataOrderByDataAsc(LocalDate data) {
        return jpaRepository.findByDataOrderByDataAsc(data).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PontoEletronico toDomain(PontoEletronicoJpaEntity entity) {
        return PontoEletronico.builder()
                .id(entity.getId())
                .usuario(UsuarioMapper.toDomain(entity.getUsuario()))
                .data(entity.getData())
                .entrada1(entity.getEntrada1())
                .saida1(entity.getSaida1())
                .entrada2(entity.getEntrada2())
                .saida2(entity.getSaida2())
                .entrada3(entity.getEntrada3())
                .saida3(entity.getSaida3())
                .localizacao(entity.getLocalizacao())
                .observacao(entity.getObservacao())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private PontoEletronicoJpaEntity toEntity(PontoEletronico ponto) {
        return PontoEletronicoJpaEntity.builder()
                .id(ponto.getId())
                .usuario(UsuarioMapper.toEntity(ponto.getUsuario()))
                .data(ponto.getData())
                .entrada1(ponto.getEntrada1())
                .saida1(ponto.getSaida1())
                .entrada2(ponto.getEntrada2())
                .saida2(ponto.getSaida2())
                .entrada3(ponto.getEntrada3())
                .saida3(ponto.getSaida3())
                .localizacao(ponto.getLocalizacao())
                .observacao(ponto.getObservacao())
                .createdAt(ponto.getCreatedAt())
                .build();
    }
}
