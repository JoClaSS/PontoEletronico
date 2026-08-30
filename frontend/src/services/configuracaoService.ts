import { createApiClient } from './apiClient';
import type { ConfiguracaoEmpresa, AtualizarConfiguracaoRequest } from '../types';

const apiClient = createApiClient('MVC');

export class ConfiguracaoService {
  /**
   * Busca as configurações da empresa
   */
  async getConfiguracoes(): Promise<ConfiguracaoEmpresa> {
    try {
      const response = await apiClient.get<ConfiguracaoEmpresa>('/api/configuracoes');
      return response.data;
    } catch (error: any) {
      // Se não existir configuração, retorna valores padrão
      if (error?.response?.status === 404) {
        return {
          nomeEmpresa: 'Mundial Ciclo',
          horarioCheckin: '08:00',
          horarioCheckout: '18:00'
        };
      }
      throw error;
    }
  }

  /**
   * Salva/atualiza as configurações da empresa
   */
  async salvarConfiguracoes(configuracoes: AtualizarConfiguracaoRequest): Promise<ConfiguracaoEmpresa> {
    const response = await apiClient.post<ConfiguracaoEmpresa>('/api/configuracoes', configuracoes);
    return response.data;
  }
}

// Instância singleton
export const configuracaoService = new ConfiguracaoService();