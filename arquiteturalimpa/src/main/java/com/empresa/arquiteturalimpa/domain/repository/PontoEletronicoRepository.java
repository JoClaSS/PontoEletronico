package com.empresa.arquiteturalimpa.domain.repository;

import com.empresa.arquiteturalimpa.domain.model.PontoEletronico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída (output port) para persistência de PontoEletronico.
 */
public interface PontoEletronicoRepository {

    PontoEletronico save(PontoEletronico ponto);

    Optional<PontoEletronico> findById(UUID id);

    Optional<PontoEletronico> findByUsuarioIdAndData(UUID usuarioId, LocalDate data);

    List<PontoEletronico> findByUsuarioIdAndPeriodo(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim);

    List<PontoEletronico> findByDataOrderByDataAsc(LocalDate data);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
