package com.empresa.pontoeletronico.infrastructure.persistence.mappers;

import com.empresa.pontoeletronico.domain.entities.JornadaTrabalho;
import com.empresa.pontoeletronico.domain.entities.PontoEletronico;
import com.empresa.pontoeletronico.domain.entities.Usuario;
import com.empresa.pontoeletronico.infrastructure.persistence.entities.JornadaTrabalhoJpaEntity;
import com.empresa.pontoeletronico.infrastructure.persistence.entities.PontoEletronicoJpaEntity;
import com.empresa.pontoeletronico.infrastructure.persistence.entities.UsuarioJpaEntity;

/**
 * Mapper para converter entre entidades de domínio e entidades JPA
 */
public class PersistenceMapper {
    
    // === JornadaTrabalho ===
    
    public static JornadaTrabalho toDomain(JornadaTrabalhoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return JornadaTrabalho.builder()
            .id(entity.getId().toString())
            .nome(entity.getNome())
            .inicioExpediente(entity.getInicioExpediente())
            .fimExpediente(entity.getFimExpediente())
            .inicioIntervalo(entity.getInicioIntervalo())
            .fimIntervalo(entity.getFimIntervalo())
            .cargaHorariaDiaria(entity.getCargaHorariaDiaria())
            .permiteHoraExtra(entity.getPermiteHoraExtra())
            .limiteHoraExtra(entity.getLimiteHoraExtra())
            .build();
    }
    
    public static JornadaTrabalhoJpaEntity toJpaEntity(JornadaTrabalho domain) {
        if (domain == null) {
            return null;
        }
        
        return JornadaTrabalhoJpaEntity.builder()
            .id(domain.getId() != null ? java.util.UUID.fromString(domain.getId()) : null)
            .nome(domain.getNome())
            .inicioExpediente(domain.getInicioExpediente())
            .fimExpediente(domain.getFimExpediente())
            .inicioIntervalo(domain.getInicioIntervalo())
            .fimIntervalo(domain.getFimIntervalo())
            .cargaHorariaDiaria(domain.getCargaHorariaDiaria())
            .permiteHoraExtra(domain.isPermiteHoraExtra())
            .limiteHoraExtra(domain.getLimiteHoraExtra())
            .build();
    }
    
    // === Usuario ===
    
    public static Usuario toDomain(UsuarioJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Usuario.builder()
            .id(entity.getId().toString())
            .email(entity.getEmail())
            .nome(entity.getNome())
            .cargo(entity.getCargo())
            .departamento(entity.getDepartamento())
            .ativo(entity.getAtivo())
            .jornada(toDomain(entity.getJornada()))
            .build();
    }
    
    public static UsuarioJpaEntity toJpaEntity(Usuario domain) {
        if (domain == null) {
            return null;
        }
        
        return UsuarioJpaEntity.builder()
            .id(domain.getId() != null ? java.util.UUID.fromString(domain.getId()) : null)
            .email(domain.getEmail())
            .nome(domain.getNome())
            .cargo(domain.getCargo())
            .departamento(domain.getDepartamento())
            .ativo(domain.isAtivo())
            .jornada(toJpaEntity(domain.getJornada()))
            .build();
    }
    
    // === PontoEletronico ===
    
    public static PontoEletronico toDomain(PontoEletronicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return PontoEletronico.builder()
            .id(entity.getId().toString())
            .usuarioId(entity.getUsuario().getId().toString())
            .dataHora(entity.getDataHora())
            .tipo(entity.getTipo())
            .localizacao(entity.getLocalizacao())
            .observacao(entity.getObservacao())
            .dispositivoId(entity.getDispositivoId())
            .build();
    }
    
    public static PontoEletronicoJpaEntity toJpaEntity(PontoEletronico domain, UsuarioJpaEntity usuarioEntity) {
        if (domain == null) {
            return null;
        }
        
        return PontoEletronicoJpaEntity.builder()
            .id(domain.getId() != null ? java.util.UUID.fromString(domain.getId()) : null)
            .usuario(usuarioEntity)
            .dataHora(domain.getDataHora())
            .tipo(domain.getTipo())
            .localizacao(domain.getLocalizacao())
            .observacao(domain.getObservacao())
            .dispositivoId(domain.getDispositivoId())
            .build();
    }
}