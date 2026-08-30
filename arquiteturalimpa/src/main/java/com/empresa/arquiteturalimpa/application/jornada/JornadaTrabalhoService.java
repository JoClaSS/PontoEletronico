package com.empresa.arquiteturalimpa.application.jornada;

import com.empresa.arquiteturalimpa.domain.exception.EntityNotFoundException;
import com.empresa.arquiteturalimpa.domain.model.JornadaTrabalho;
import com.empresa.arquiteturalimpa.domain.repository.JornadaTrabalhoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JornadaTrabalhoService {

    private final JornadaTrabalhoRepository jornadaRepository;

    public List<JornadaTrabalho> listarJornadas() {
        return jornadaRepository.findAllByOrderByHorasSemanaisAsc();
    }

    public JornadaTrabalho buscarPorId(UUID id) {
        return jornadaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jornada não encontrada"));
    }

    public List<JornadaTrabalho> buscarPorNome(String nome) {
        return jornadaRepository.findByNomeContainingIgnoreCase(nome);
    }
}
