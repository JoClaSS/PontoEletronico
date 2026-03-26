import { Backend } from '../types';
import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos } from '../types';
import { apiMVCService } from './apiMVC';
import { apiCleanService } from './apiClean';

// Serviço unificado que decide qual backend usar
export class ApiService {
  private backend: Backend;

  constructor(backend: Backend) {
    this.backend = backend;
  }

  // Método para trocar o backend
  setBackend(backend: Backend): void {
    this.backend = backend;
  }

  // Usuários
  async getUsuarios(): Promise<Usuario[]> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.getUsuarios();
    }
    return apiCleanService.getUsuarios();
  }

  async getUsuarioById(id: string): Promise<Usuario> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.getUsuarioById(id);
    }
    return apiCleanService.getUsuarioById(id);
  }

  async getUsuarioByEmail(email: string): Promise<Usuario> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.getUsuarioByEmail(email);
    }
    return apiCleanService.getUsuarioByEmail(email);
  }

  async criarUsuario(usuario: Omit<Usuario, 'id' | 'createdAt' | 'updatedAt'>): Promise<Usuario> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.criarUsuario(usuario);
    }
    return apiCleanService.criarUsuario(usuario);
  }

  // Pontos Eletrônicos
  async registrarPonto(data: RegistrarPontoRequest): Promise<PontoEletronico> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.registrarPonto(data);
    }
    return apiCleanService.registrarPonto(data);
  }

  async getPontosDeHoje(usuarioId: string): Promise<PontoEletronico[]> {
    if (this.backend === Backend.MVC) {
      const hoje = new Date().toISOString().split('T')[0];
      return apiMVCService.getPontosPorUsuarioEData(usuarioId, hoje);
    }
    return apiCleanService.getPontosDeHoje(usuarioId);
  }

  async getPontosPorData(usuarioId: string, data: string): Promise<PontoEletronico[]> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.getPontosPorUsuarioEData(usuarioId, data);
    }
    return apiCleanService.getPontosPorData(usuarioId, data);
  }

  async getPontosPorPeriodo(usuarioId: string, filtros: FiltrosPontos): Promise<PontoEletronico[]> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.getPontosPorPeriodo(usuarioId, filtros);
    }
    return apiCleanService.getPontosPorPeriodo(usuarioId, filtros);
  }

  async getRelatorioHoras(usuarioId: string, filtros: FiltrosPontos): Promise<RelatorioHorasResponse> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.getRelatorioHoras(usuarioId, filtros);
    }
    return apiCleanService.getHorasTrabalhadasPeriodo(usuarioId, filtros);
  }

  // Métodos específicos por backend
  async buscarUsuarios(nome: string): Promise<Usuario[]> {
    if (this.backend === Backend.MVC) {
      return apiMVCService.buscarUsuarios(nome);
    }
    // Clean não tem busca por nome, retorna todos e filtra
    const usuarios = await apiCleanService.getUsuarios();
    return usuarios.filter(u => u.nome.toLowerCase().includes(nome.toLowerCase()));
  }

  async getUsuariosAtivos(): Promise<Usuario[]> {
    if (this.backend === Backend.MVC) {
      // MVC não tem conceito de ativo/inativo, retorna todos
      return apiMVCService.getUsuarios();
    }
    return apiCleanService.getUsuariosAtivos();
  }
}

// Instância singleton (será atualizada via contexto)
export const apiService = new ApiService(Backend.MVC);