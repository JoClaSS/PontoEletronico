import { createApiClient } from './apiClient';
import type { PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos, Usuario } from '../types';

const apiClient = createApiClient('MVC');

// Função para mapear resposta do MVC para o formato esperado pelo frontend
const mapMVCResponseToFrontend = (mvcResponse: any): PontoEletronico => {
  return {
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
};

export class PontoService {
  /**
   * Registra um novo ponto eletrônico
   */
  async registrarPonto(data: RegistrarPontoRequest): Promise<PontoEletronico> {
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
    
    if (!payload.usuarioId) {
      throw new Error('usuarioId é obrigatório para registrar ponto');
    }
    
    const response = await apiClient.post('/api/pontos', payload);
    return mapMVCResponseToFrontend(response.data);
  }

  /**
   * Busca pontos de um usuário em uma data específica
   */
  async getPontosPorUsuarioEData(usuarioId: string, data?: string): Promise<PontoEletronico[]> {
    let url = `/api/pontos/usuario/${usuarioId}`;
    if (data) {
      url += `?data=${data}`;
    }
    
    const response = await apiClient.get(url);
    return response.data.map(mapMVCResponseToFrontend);
  }

  /**
   * Busca pontos de hoje para um usuário
   */
  async getPontosDeHoje(usuarioId: string): Promise<PontoEletronico[]> {
    const hoje = new Date().toISOString().split('T')[0];
    return this.getPontosPorUsuarioEData(usuarioId, hoje);
  }

  /**
   * Busca pontos por período
   */
  async getPontosPorPeriodo(usuarioId: string, filtros: FiltrosPontos): Promise<PontoEletronico[]> {
    const params = new URLSearchParams();
    if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio);
    if (filtros.dataFim) params.append('dataFim', filtros.dataFim);
    
    const response = await apiClient.get(`/api/pontos/usuario/${usuarioId}/periodo?${params}`);
    return response.data.map(mapMVCResponseToFrontend);
  }

  /**
   * Gera relatório de horas trabalhadas
   */
  async getRelatorioHoras(usuarioId: string, filtros: FiltrosPontos): Promise<RelatorioHorasResponse> {
    const params = new URLSearchParams();
    if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio);
    if (filtros.dataFim) params.append('dataFim', filtros.dataFim);
    
    const response = await apiClient.get<RelatorioHorasResponse>(`/api/pontos/usuario/${usuarioId}/relatorio?${params}`);
    return response.data;
  }

  /**
   * Lista de presença - funcionários que registraram ponto na data
   */
  async getListaPresencaDodia(data?: string): Promise<Usuario[]> {
    let url = '/api/pontos/presenca';
    if (data) {
      url += `?data=${data}`;
    }
    const response = await apiClient.get<Usuario[]>(url);
    return response.data;
  }
}

// Instância singleton
export const pontoService = new PontoService();