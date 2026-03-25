package com.empresa.pontoeletronico.infrastructure.config;

import com.empresa.pontoeletronico.application.usecases.CalcularHorasTrabalhadasUseCase;
import com.empresa.pontoeletronico.application.usecases.ConsultarPontosUseCase;
import com.empresa.pontoeletronico.application.usecases.RegistrarPontoUseCase;
import com.empresa.pontoeletronico.domain.repositories.PontoEletronicoRepository;
import com.empresa.pontoeletronico.domain.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuração Spring para injeção de dependências
 * Conecta os casos de uso com suas dependências seguindo os princípios da arquitetura limpa
 */
@Configuration
public class UseCaseConfiguration {
    
    @Bean
    public RegistrarPontoUseCase registrarPontoUseCase(
            PontoEletronicoRepository pontoRepository,
            UsuarioRepository usuarioRepository) {
        return new RegistrarPontoUseCase(pontoRepository, usuarioRepository);
    }
    
    @Bean
    public ConsultarPontosUseCase consultarPontosUseCase(
            PontoEletronicoRepository pontoRepository,
            UsuarioRepository usuarioRepository) {
        return new ConsultarPontosUseCase(pontoRepository, usuarioRepository);
    }
    
    @Bean
    public CalcularHorasTrabalhadasUseCase calcularHorasTrabalhadasUseCase(
            PontoEletronicoRepository pontoRepository,
            UsuarioRepository usuarioRepository) {
        return new CalcularHorasTrabalhadasUseCase(pontoRepository, usuarioRepository);
    }
}