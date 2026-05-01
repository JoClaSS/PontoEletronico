import { createApiClient } from './apiClient';

const apiClient = createApiClient('MVC');

export interface SystemInfo {
  version: string;
  name: string;
  footerCompany: string;
}

export class SystemService {
  /**
   * Busca informações do sistema
   */
  async getSystemInfo(): Promise<SystemInfo> {
    const response = await apiClient.get<SystemInfo>('/api/system/info');
    return response.data;
  }
}

// Instância singleton
export const systemService = new SystemService();