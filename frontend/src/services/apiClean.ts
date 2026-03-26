import { createApiClient } from './apiClient';
import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos } from '../types';

const apiClean = createApiClient('CLEAN');

export const apiCleanService = {
  // Usuários
  async getUsuarios(): Promise<Usuario[]> {
    const response = await apiClean.get<Usuario[]>('/api/usuarios');
    return response.data;
  },

  async getUsuariosAtivos(): Promise<Usuario[]> {
    const response = await apiClean.get<Usuario[]>('/api/usuarios/ativos');
    return response.data;
  },

  async getUsuarioById(id: string): Promise<Usuario> {
    const response = await apiClean.get<Usuario>(`/api/usuarios/${id}`);
    return response.data;
  },

  async getUsuarioByEmail(email: string): Promise<Usuario> {
    const response = await apiClean.get<Usuario>(`/api/usuarios/email/${email}`);
    return response.data;
  },

  async criarUsuario(usuario: Omit<Usuario, 'id' | 'createdAt' | 'updatedAt'>): Promise<Usuario> {
    const response = await apiClean.post<Usuario>('/api/usuarios', usuario);
    return response.data;
  },

  // Pontos Eletrônicos
  async registrarPonto(data: RegistrarPontoRequest): Promise<PontoEletronico> {
    // Adaptação para o formato do backend Clean
    const payload = {
      usuarioId: data.usuarioId || data.usuario?.id,
      dataHora: data.dataHora,
      localizacao: data.localizacao,
      observacao: data.observacao,
      dispositivoId: data.dispositivoId
    };
    
    const response = await apiClean.post<PontoEletronico>('/api/pontos', payload);
    return response.data;
  },

  async getPontosDeHoje(usuarioId: string): Promise<PontoEletronico[]> {
    const response = await apiClean.get<PontoEletronico[]>(`/api/pontos/usuario/${usuarioId}`);
    return response.data;
  },

  async getPontosPorData(usuarioId: string, data: string): Promise<PontoEletronico[]> {
    const response = await apiClean.get<PontoEletronico[]>(`/api/pontos/usuario/${usuarioId}/data/${data}`);
    return response.data;
  },

  async getPontosPorPeriodo(usuarioId: string, filtros: FiltrosPontos): Promise<PontoEletronico[]> {
    const params = new URLSearchParams();
    if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio);
    if (filtros.dataFim) params.append('dataFim', filtros.dataFim);
    
    const response = await apiClean.get<PontoEletronico[]>(`/api/pontos/usuario/${usuarioId}/periodo?${params}`);
    return response.data;
  },

  async getHorasTrabalhadas(usuarioId: string, data: string): Promise<{ horas: number; minutos: number }> {
    const response = await apiClean.get<{ horas: number; minutos: number }>(`/api/pontos/usuario/${usuarioId}/horas/data/${data}`);
    return response.data;
  },

  async getHorasTrabalhadasPeriodo(usuarioId: string, filtros: FiltrosPontos): Promise<RelatorioHorasResponse> {
    const params = new URLSearchParams();
    if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio);
    if (filtros.dataFim) params.append('dataFim', filtros.dataFim);
    
    const response = await apiClean.get<RelatorioHorasResponse>(`/api/pontos/usuario/${usuarioId}/horas/periodo?${params}`);
    return response.data;
  }
};