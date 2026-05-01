import { createApiClient } from './apiClient';

const apiClient = createApiClient('MVC');

export class SolicitacaoService {
  /**
   * Busca motivos disponíveis para solicitações
   */
  async getMotivos(): Promise<any[]> {
    const response = await apiClient.get<any[]>('/api/solicitacoes/motivos');
    return response.data;
  }

  /**
   * Busca solicitações de um usuário específico
   */
  async getSolicitacoesPorUsuario(usuarioId: string): Promise<any[]> {
    const response = await apiClient.get<any[]>(`/api/solicitacoes/usuario/${usuarioId}`);
    return response.data;
  }

  /**
   * Cria uma nova solicitação
   */
  async criarSolicitacao(solicitacao: any): Promise<any> {
    const response = await apiClient.post<any>('/api/solicitacoes', solicitacao);
    return response.data;
  }

  /**
   * Cria uma nova solicitação com anexo
   */
  async criarSolicitacaoComAnexo(formData: FormData): Promise<any> {
    const response = await apiClient.post<any>('/api/solicitacoes/com-anexo', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  /**
   * Baixa anexo de uma solicitação
   */
  async baixarAnexoSolicitacao(solicitacaoId: string): Promise<Blob> {
    const response = await apiClient.get(`/api/solicitacoes/${solicitacaoId}/anexo`, {
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * Resolve uma solicitação (aprovar/rejeitar)
   */
  async resolverSolicitacao(solicitacaoId: string, resolucaoData: any): Promise<any> {
    const response = await apiClient.put<any>(`/api/solicitacoes/${solicitacaoId}/resolver`, resolucaoData);
    return response.data;
  }

  /**
   * Exclui uma solicitação
   */
  async excluirSolicitacao(solicitacaoId: string): Promise<void> {
    await apiClient.delete(`/api/solicitacoes/${solicitacaoId}`);
  }

  /**
   * Conta solicitações em aberto
   */
  async contarSolicitacoesEmAberto(): Promise<{ quantidade: number }> {
    const response = await apiClient.get<{ quantidade: number }>('/api/solicitacoes/contagem/abertas');
    return response.data;
  }

  /**
   * Busca a solicitação mais recente em aberto
   */
  async buscarSolicitacaoMaisRecenteAberta(): Promise<{ solicitacao: any | null }> {
    const response = await apiClient.get<{ solicitacao: any | null }>('/api/solicitacoes/recente/aberta');
    return response.data;
  }
}

// Instância singleton
export const solicitacaoService = new SolicitacaoService();