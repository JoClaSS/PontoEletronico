import { createApiClient } from './apiClient';

const apiClient = createApiClient('MVC');

export class JornadaService {
  /**
   * Busca todas as jornadas de trabalho disponíveis
   */
  async getJornadas(): Promise<any[]> {
    const response = await apiClient.get<any[]>('/api/jornadas');
    return response.data;
  }
}

// Instância singleton
export const jornadaService = new JornadaService();