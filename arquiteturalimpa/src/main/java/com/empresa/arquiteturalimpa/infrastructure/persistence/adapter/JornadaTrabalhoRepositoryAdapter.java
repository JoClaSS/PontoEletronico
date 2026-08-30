package com.empresa.arquiteturalimpa.infrastructure.persistence.adapter;

import com.empresa.arquiteturalimpa.domain.model.JornadaTrabalho;
import com.empresa.arquiteturalimpa.domain.repository.JornadaTrabalhoRepository;
import com.empresa.arquiteturalimpa.infrastructure.persistence.entity.JornadaTrabalhoJpaEntity;
import com.empresa.arquiteturalimpa.infrastructure.persistence.jpa.JornadaTrabalhoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapta o output port JornadaTrabalhoRepository (domínio) para o Spring Data JPA (infraestrutura).
 */
@Component
@RequiredArgsConstructor
public class JornadaTrabalhoRepositoryAdapter implements JornadaTrabalhoRepository {

    private final JornadaTrabalhoJpaRepository jpaRepository;

    @Override
    public List<JornadaTrabalho> findAllByOrderByHorasSemanaisAsc() {
        return jpaRepository.findAllByOrderByHorasSemanaisAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<JornadaTrabalho> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<JornadaTrabalho> findByNomeContainingIgnoreCase(String nome) {
        return jpaRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toDomain)
                .toList();
    }

    private JornadaTrabalho toDomain(JornadaTrabalhoJpaEntity entity) {
        return JornadaTrabalho.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .horasSemanais(entity.getHorasSemanais())
                .diasTrabalhados(entity.getDiasTrabalhados())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
