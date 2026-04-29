import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos, CriarUsuarioRequest, ConfiguracaoEmpresa, AtualizarConfiguracaoRequest } from '../types';
import { apiMVCService } from './apiMVC';

// Serviço simplificado usando apenas MVC
export class ApiService {
  // Usuários
  async getUsuarios(): Promise<Usuario[]> {
    return apiMVCService.getUsuarios();
  }
  
  async getFuncionarios(): Promise<Usuario[]> {
    return apiMVCService.getFuncionarios();
  }

  async getUsuarioById(id: string): Promise<Usuario> {
    return apiMVCService.getUsuarioById(id);
  }

  async getUsuarioByEmail(email: string): Promise<Usuario> {
    return apiMVCService.getUsuarioByEmail(email);
  }

  async criarUsuario(usuario: CriarUsuarioRequest): Promise<Usuario> {
    return apiMVCService.criarUsuario(usuario);
  }

  async desativarUsuario(id: string): Promise<void> {
    return apiMVCService.desativarUsuario(id);
  }

  async reativarUsuario(id: string): Promise<void> {
    return apiMVCService.reativarUsuario(id);
  }

  // Pontos Eletrônicos
  async registrarPonto(data: RegistrarPontoRequest): Promise<PontoEletronico> {
    return apiMVCService.registrarPonto(data);
  }

  async getPontosDeHoje(usuarioId: string): Promise<PontoEletronico[]> {
    const hoje = new Date().toISOString().split('T')[0];
    return apiMVCService.getPontosPorUsuarioEData(usuarioId, hoje);
  }

  async getPontosPorData(usuarioId: string, data: string): Promise<PontoEletronico[]> {
    return apiMVCService.getPontosPorUsuarioEData(usuarioId, data);
  }

  async getPontosPorPeriodo(usuarioId: string, filtros: FiltrosPontos): Promise<PontoEletronico[]> {
    return apiMVCService.getPontosPorPeriodo(usuarioId, filtros);
  }

  async getRelatorioHoras(usuarioId: string, filtros: FiltrosPontos): Promise<RelatorioHorasResponse> {
    return apiMVCService.getRelatorioHoras(usuarioId, filtros);
  }

  async buscarUsuarios(nome: string): Promise<Usuario[]> {
    return apiMVCService.buscarUsuarios(nome);
  }

  async getUsuariosAtivos(): Promise<Usuario[]> {
    // MVC não tem conceito de ativo/inativo, retorna todos
    return apiMVCService.getUsuarios();
  }

  async getListaPresencaDodia(data?: string): Promise<Usuario[]> {
    return apiMVCService.getListaPresencaDodia(data);
  }

  // Configurações da Empresa
  async getConfiguracoes(): Promise<ConfiguracaoEmpresa> {
    return apiMVCService.getConfiguracoes();
  }

  async salvarConfiguracoes(configuracoes: AtualizarConfiguracaoRequest): Promise<ConfiguracaoEmpresa> {
    return apiMVCService.salvarConfiguracoes(configuracoes);
  }

  // Informações do Sistema
  async getSystemInfo(): Promise<{ version: string; name: string; footerCompany: string }> {
    return apiMVCService.getSystemInfo();
  }
}

// Instância singleton
export const apiService = new ApiService();