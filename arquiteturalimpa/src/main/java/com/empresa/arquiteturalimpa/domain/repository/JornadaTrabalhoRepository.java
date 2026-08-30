package com.empresa.arquiteturalimpa.domain.repository;

import com.empresa.arquiteturalimpa.domain.model.JornadaTrabalho;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída (output port) para persistência de JornadaTrabalho.
 */
public interface JornadaTrabalhoRepository {

    List<JornadaTrabalho> findAllByOrderByHorasSemanaisAsc();

    Optional<JornadaTrabalho> findById(UUID id);

    List<JornadaTrabalho> findByNomeContainingIgnoreCase(String nome);
}
