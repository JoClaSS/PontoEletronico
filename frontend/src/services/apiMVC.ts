import { createApiClient } from './apiClient';
import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos, CriarUsuarioRequest } from '../types';

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

  async criarUsuario(usuario: CriarUsuarioRequest): Promise<Usuario> {
    const response = await apiMVC.post<Usuario>('/api/usuarios', usuario);
    return response.data;
  },

  async desativarUsuario(id: string): Promise<void> {
    await apiMVC.delete(`/api/usuarios/${id}`);
  },

  async reativarUsuario(id: string): Promise<void> {
    await apiMVC.put(`/api/usuarios/${id}/reativar`);
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
  },

  // Solicitações
  async getMotivos(): Promise<any[]> {
    const response = await apiMVC.get<any[]>('/api/solicitacoes/motivos');
    return response.data;
  },

  async getSolicitacoesPorUsuario(usuarioId: string): Promise<any[]> {
    const response = await apiMVC.get<any[]>(`/api/solicitacoes/usuario/${usuarioId}`);
    return response.data;
  },

  async criarSolicitacao(solicitacao: any): Promise<any> {
    const response = await apiMVC.post<any>('/api/solicitacoes', solicitacao);
    return response.data;
  },

  async criarSolicitacaoComAnexo(formData: FormData): Promise<any> {
    const response = await apiMVC.post<any>('/api/solicitacoes/com-anexo', formData);
    return response.data;
  },

  async baixarAnexoSolicitacao(solicitacaoId: string): Promise<Blob> {
    const response = await apiMVC.get(`/api/solicitacoes/${solicitacaoId}/anexo`, {
      responseType: 'blob'
    });
    return response.data;
  },

  async resolverSolicitacao(solicitacaoId: string, resolucaoData: any): Promise<any> {
    const response = await apiMVC.put<any>(`/api/solicitacoes/${solicitacaoId}/resolver`, resolucaoData);
    return response.data;
  },

  async excluirSolicitacao(solicitacaoId: string): Promise<void> {
    await apiMVC.delete(`/api/solicitacoes/${solicitacaoId}`);
  },

  async getPontosPorData(usuarioId: string, data: string): Promise<PontoEletronico[]> {
    const response = await apiMVC.get(`/api/pontos/usuario/${usuarioId}?data=${data}`);
    return response.data.map(mapMVCResponseToFrontend);
  }
};