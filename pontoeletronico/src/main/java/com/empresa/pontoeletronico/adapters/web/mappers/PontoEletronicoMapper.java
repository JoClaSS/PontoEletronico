package com.empresa.pontoeletronico.adapters.web.mappers;

import com.empresa.pontoeletronico.adapters.web.dto.PontoEletronicoResponseDto;
import com.empresa.pontoeletronico.adapters.web.dto.RegistrarPontoRequestDto;
import com.empresa.pontoeletronico.adapters.web.dto.RelatorioHorasResponseDto;
import com.empresa.pontoeletronico.application.usecases.RegistrarPontoRequest;
import com.empresa.pontoeletronico.application.usecases.RelatorioHoras;
import com.empresa.pontoeletronico.domain.entities.PontoEletronico;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversão entre entidades e DTOs
 */
public class PontoEletronicoMapper {
    
    public static RegistrarPontoRequest toRequest(RegistrarPontoRequestDto dto) {
        return RegistrarPontoRequest.builder()
            .usuarioId(dto.getUsuarioId())
            .dataHora(dto.getDataHora())
            .tipo(dto.getTipo())
            .localizacao(dto.getLocalizacao())
            .observacao(dto.getObservacao())
            .dispositivoId(dto.getDispositivoId())
            .build();
    }
    
    public static PontoEletronicoResponseDto toResponseDto(PontoEletronico entity) {
        PontoEletronicoResponseDto dto = new PontoEletronicoResponseDto();
        dto.setId(entity.getId());
        dto.setUsuarioId(entity.getUsuarioId());
        dto.setDataHora(entity.getDataHora());
        dto.setTipo(entity.getTipo());
        dto.setTipoDescricao(entity.getTipo() != null ? entity.getTipo().getDescricao() : null);
        dto.setLocalizacao(entity.getLocalizacao());
        dto.setObservacao(entity.getObservacao());
        dto.setDispositivoId(entity.getDispositivoId());
        return dto;
    }
    
    public static RelatorioHorasResponseDto toResponseDto(RelatorioHoras relatorio) {
        RelatorioHorasResponseDto dto = new RelatorioHorasResponseDto();
        dto.setUsuarioId(relatorio.getUsuarioId());
        dto.setData(relatorio.getData());
        dto.setDataInicio(relatorio.getDataInicio());
        dto.setDataFim(relatorio.getDataFim());
        dto.setHorasTrabalhadasFormatadas(relatorio.getHorasTrabalhadasFormatadas());
        dto.setCargaHorariaEsperadaFormatada(formatarDuracao(relatorio.getCargaHorariaEsperada()));
        dto.setSaldoFormatado(relatorio.getSaldoFormatado());
        dto.setTemHorasDebito(relatorio.temHorasDebito());
        dto.setTemHorasExcedentes(relatorio.temHorasExcedentes());
        
        if (relatorio.getPontos() != null) {
            List<PontoEletronicoResponseDto> pontosDto = relatorio.getPontos()
                .stream()
                .map(PontoEletronicoMapper::toResponseDto)
                .collect(Collectors.toList());
            dto.setPontos(pontosDto);
        }
        
        return dto;
    }
    
    private static String formatarDuracao(Duration duracao) {
        if (duracao == null) {
            return "00:00";
        }
        long horas = duracao.toHours();
        long minutos = duracao.toMinutes() % 60;
        return String.format("%02d:%02d", horas, minutos);
    }
}