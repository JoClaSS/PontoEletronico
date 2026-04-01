import { createApiClient } from './apiClient';
import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos } from '../types';

const apiMVC = createApiClient('MVC');

// Função para mapear resposta do MVC para o formato esperado pelo frontend
const mapMVCResponseToFrontend = (mvcResponse: any): PontoEletronico => {
  console.log('[MVC] Mapeando resposta do backend:', mvcResponse);
  
  const mapped = {
    id: mvcResponse.id,
    usuarioId: mvcResponse.usuarioId,
    usuario: mvcResponse.nomeUsuario ? { 
      id: mvcResponse.usuarioId, 
      nome: mvcResponse.nomeUsuario,
      email: '',
      cpf: '' 
    } : undefined,
    dataHora: mvcResponse.dataHora,
    tipo: mvcResponse.tipoPonto, // Mapeia tipoPonto -> tipo
    localizacao: mvcResponse.localizacao,
    observacao: mvcResponse.observacao,
    createdAt: mvcResponse.createdAt
  };
  
  console.log('[MVC] Objeto mapeado:', mapped);
  return mapped;
};

export const apiMVCService = {
  // Usuários
  async getUsuarios(): Promise<Usuario[]> {
    const response = await apiMVC.get<Usuario[]>('/api/usuarios');
    return response.data;
  },

  async getUsuarioById(id: string): Promise<Usuario> {
    const response = await apiMVC.get<Usuario>(`/api/usuarios/${id}`);
    return response.data;
  },

  async getUsuarioByEmail(email: string): Promise<Usuario> {
    const response = await apiMVC.get<Usuario>(`/api/usuarios/email/${email}`);
    return response.data;
  },

  async buscarUsuarios(nome: string): Promise<Usuario[]> {
    const response = await apiMVC.get<Usuario[]>(`/api/usuarios/buscar?nome=${encodeURIComponent(nome)}`);
    return response.data;
  },

  async criarUsuario(usuario: Omit<Usuario, 'id' | 'createdAt' | 'updatedAt'>): Promise<Usuario> {
    const response = await apiMVC.post<Usuario>('/api/usuarios', usuario);
    return response.data;
  },

  // Pontos Eletrônicos
  async registrarPonto(data: RegistrarPontoRequest): Promise<PontoEletronico> {
    console.log('[MVC] Dados recebidos para registrar ponto:', data);
    
    // Adaptação para o formato do backend MVC
    const payload: any = {
      usuarioId: data.usuarioId || data.usuario?.id
    };

    // Adiciona campos opcionais apenas se existirem
    if (data.dataHora) {
      payload.dataHora = data.dataHora;
    }
    if (data.localizacao) {
      payload.localizacao = data.localizacao;
    }
    if (data.observacao) {
      payload.observacao = data.observacao;
    }
    
    console.log('[MVC] Payload final:', payload);
    
    if (!payload.usuarioId) {
      throw new Error('usuarioId é obrigatório para registrar ponto');
    }
    
    const response = await apiMVC.post('/api/pontos', payload);
    return mapMVCResponseToFrontend(response.data);
  },

  async getPontosPorUsuarioEData(usuarioId: string, data?: string): Promise<PontoEletronico[]> {
    let url = `/api/pontos/usuario/${usuarioId}`;
    if (data) {
      url += `?data=${data}`;
    }
    
    console.log('[MVC] Buscando pontos - URL:', url);
    console.log('[MVC] UsuarioId:', usuarioId);
    console.log('[MVC] Data:', data);
    
    const response = await apiMVC.get(url);
    console.log('[MVC] Response data:', response.data);
    
    const mappedData = response.data.map(mapMVCResponseToFrontend);
    console.log('[MVC] Dados mapeados:', mappedData);
    
    return mappedData;
  },

  async getPontosPorPeriodo(usuarioId: string, filtros: FiltrosPontos): Promise<PontoEletronico[]> {
    const params = new URLSearchParams();
    if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio);
    if (filtros.dataFim) params.append('dataFim', filtros.dataFim);
    
    const response = await apiMVC.get(`/api/pontos/usuario/${usuarioId}/periodo?${params}`);
    return response.data.map(mapMVCResponseToFrontend);
  },

  async getRelatorioHoras(usuarioId: string, filtros: FiltrosPontos): Promise<RelatorioHorasResponse> {
    const params = new URLSearchParams();
    if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio);
    if (filtros.dataFim) params.append('dataFim', filtros.dataFim);
    
    const response = await apiMVC.get<RelatorioHorasResponse>(`/api/pontos/usuario/${usuarioId}/relatorio?${params}`);
    return response.data;
  },

  // Jornadas (se necessário)
  async getJornadas(): Promise<any[]> {
    const response = await apiMVC.get<any[]>('/api/jornadas');
    return response.data;
  }
};