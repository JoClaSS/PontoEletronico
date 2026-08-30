import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos, CriarUsuarioRequest, ConfiguracaoEmpresa, AtualizarConfiguracaoRequest } from '../types';
import { usuarioService } from './usuarioService';
import { pontoService } from './pontoService';
import { configuracaoService } from './configuracaoService';
import { systemService } from './systemService';

// Serviço centralizado que utiliza os services específicos
export class ApiService {
  // Usuários
  async getUsuarios(): Promise<Usuario[]> {
    return usuarioService.getUsuarios();
  }
  
  async getFuncionarios(): Promise<Usuario[]> {
    return usuarioService.getFuncionarios();
  }

  async getUsuarioById(id: string): Promise<Usuario> {
    return usuarioService.getUsuarioById(id);
  }

  async getUsuarioByEmail(email: string): Promise<Usuario> {
    return usuarioService.getUsuarioByEmail(email);
  }

  async criarUsuario(usuario: CriarUsuarioRequest): Promise<Usuario> {
    return usuarioService.criarUsuario(usuario);
  }

  async desativarUsuario(id: string): Promise<void> {
    return usuarioService.desativarUsuario(id);
  }

  async reativarUsuario(id: string): Promise<void> {
    return usuarioService.reativarUsuario(id);
  }

  async buscarUsuarios(nome: string): Promise<Usuario[]> {
    return usuarioService.buscarUsuarios(nome);
  }

  async getUsuariosAtivos(): Promise<Usuario[]> {
    // Mantém compatibilidade - MVC não tem conceito de ativo/inativo
    return usuarioService.getUsuarios();
  }

  // Pontos Eletrônicos
  async registrarPonto(data: RegistrarPontoRequest): Promise<PontoEletronico> {
    return pontoService.registrarPonto(data);
  }

  async getPontosDeHoje(usuarioId: string): Promise<PontoEletronico[]> {
    return pontoService.getPontosDeHoje(usuarioId);
  }

  async getPontosPorData(usuarioId: string, data: string): Promise<PontoEletronico[]> {
    return pontoService.getPontosPorUsuarioEData(usuarioId, data);
  }

  async getPontosPorPeriodo(usuarioId: string, filtros: FiltrosPontos): Promise<PontoEletronico[]> {
    return pontoService.getPontosPorPeriodo(usuarioId, filtros);
  }

  async getRelatorioHoras(usuarioId: string, filtros: FiltrosPontos): Promise<RelatorioHorasResponse> {
    return pontoService.getRelatorioHoras(usuarioId, filtros);
  }

  async getListaPresencaDodia(data?: string): Promise<Usuario[]> {
    return pontoService.getListaPresencaDodia(data);
  }

  // Configurações da Empresa
  async getConfiguracoes(): Promise<ConfiguracaoEmpresa> {
    return configuracaoService.getConfiguracoes();
  }

  async salvarConfiguracoes(configuracoes: AtualizarConfiguracaoRequest): Promise<ConfiguracaoEmpresa> {
    return configuracaoService.salvarConfiguracoes(configuracoes);
  }

  // Informações do Sistema
  async getSystemInfo(): Promise<{ version: string; name: string; footerCompany: string }> {
    return systemService.getSystemInfo();
  }
}

// Instância singleton
export const apiService = new ApiService();