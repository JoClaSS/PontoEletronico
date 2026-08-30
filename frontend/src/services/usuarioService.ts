import { createApiClient } from './apiClient';
import type { Usuario, CriarUsuarioRequest } from '../types';

const apiClient = createApiClient('MVC');

export class UsuarioService {
  /**
   * Busca todos os usuários
   */
  async getUsuarios(): Promise<Usuario[]> {
    const response = await apiClient.get<Usuario[]>('/api/usuarios');
    return response.data;
  }

  /**
   * Lista apenas funcionários ativos para seleção
   */
  async getFuncionarios(): Promise<Usuario[]> {
    const response = await apiClient.get<Usuario[]>('/api/usuarios/funcionarios');
    return response.data;
  }

  /**
   * Busca usuário por ID
   */
  async getUsuarioById(id: string): Promise<Usuario> {
    const response = await apiClient.get<Usuario>(`/api/usuarios/${id}`);
    return response.data;
  }

  /**
   * Busca usuário por email
   */
  async getUsuarioByEmail(email: string): Promise<Usuario> {
    const response = await apiClient.get<Usuario>(`/api/usuarios/email/${email}`);
    return response.data;
  }

  /**
   * Busca usuários por nome
   */
  async buscarUsuarios(nome: string): Promise<Usuario[]> {
    const response = await apiClient.get<Usuario[]>(`/api/usuarios/buscar?nome=${encodeURIComponent(nome)}`);
    return response.data;
  }

  /**
   * Cria um novo usuário
   */
  async criarUsuario(usuario: CriarUsuarioRequest): Promise<Usuario> {
    const response = await apiClient.post<Usuario>('/api/usuarios', usuario);
    return response.data;
  }

  /**
   * Desativa um usuário (soft delete)
   */
  async desativarUsuario(id: string): Promise<void> {
    await apiClient.delete(`/api/usuarios/${id}`);
  }

  /**
   * Reativa um usuário
   */
  async reativarUsuario(id: string): Promise<void> {
    await apiClient.put(`/api/usuarios/${id}/reativar`);
  }
}

// Instância singleton
export const usuarioService = new UsuarioService();