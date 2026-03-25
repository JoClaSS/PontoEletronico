package com.empresa.pontoeletronico.adapters.web.controllers;

import com.empresa.pontoeletronico.adapters.web.dto.PontoEletronicoResponseDto;
import com.empresa.pontoeletronico.adapters.web.dto.RegistrarPontoRequestDto;
import com.empresa.pontoeletronico.adapters.web.dto.RelatorioHorasResponseDto;
import com.empresa.pontoeletronico.adapters.web.mappers.PontoEletronicoMapper;
import com.empresa.pontoeletronico.application.usecases.CalcularHorasTrabalhadasUseCase;
import com.empresa.pontoeletronico.application.usecases.ConsultarPontosUseCase;
import com.empresa.pontoeletronico.application.usecases.RegistrarPontoUseCase;
import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de ponto eletrônico
 */
@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
public class PontoEletronicoController {
    
    private final RegistrarPontoUseCase registrarPontoUseCase;
    private final ConsultarPontosUseCase consultarPontosUseCase;
    private final CalcularHorasTrabalhadasUseCase calcularHorasUseCase;
    
    @PostMapping
    public ResponseEntity<PontoEletronicoResponseDto> registrarPonto(@RequestBody RegistrarPontoRequestDto request) {
        try {
            PontoEletronico ponto = registrarPontoUseCase.executar(
                PontoEletronicoMapper.toRequest(request)
            );
            
            PontoEletronicoResponseDto response = PontoEletronicoMapper.toResponseDto(ponto);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PontoEletronicoResponseDto>> consultarPontosHoje(@PathVariable String usuarioId) {
        try {
            List<PontoEletronico> pontos = consultarPontosUseCase.consultarPontosHoje(usuarioId);
            
            List<PontoEletronicoResponseDto> response = pontos.stream()
                .map(PontoEletronicoMapper::toResponseDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/usuario/{usuarioId}/data/{data}")
    public ResponseEntity<List<PontoEletronicoResponseDto>> consultarPontosPorData(
            @PathVariable String usuarioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            List<PontoEletronico> pontos = consultarPontosUseCase.consultarPorData(usuarioId, data);
            
            List<PontoEletronicoResponseDto> response = pontos.stream()
                .map(PontoEletronicoMapper::toResponseDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<PontoEletronicoResponseDto>> consultarPontosPorPeriodo(
            @PathVariable String usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<PontoEletronico> pontos = consultarPontosUseCase.consultarPorPeriodo(usuarioId, dataInicio, dataFim);
            
            List<PontoEletronicoResponseDto> response = pontos.stream()
                .map(PontoEletronicoMapper::toResponseDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/usuario/{usuarioId}/horas/data/{data}")
    public ResponseEntity<RelatorioHorasResponseDto> calcularHorasDia(
            @PathVariable String usuarioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            var relatorio = calcularHorasUseCase.calcularHorasDia(usuarioId, data);
            RelatorioHorasResponseDto response = PontoEletronicoMapper.toResponseDto(relatorio);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/usuario/{usuarioId}/horas/periodo")
    public ResponseEntity<RelatorioHorasResponseDto> calcularHorasPeriodo(
            @PathVariable String usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            var relatorio = calcularHorasUseCase.calcularHorasPeriodo(usuarioId, dataInicio, dataFim);
            RelatorioHorasResponseDto response = PontoEletronicoMapper.toResponseDto(relatorio);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}