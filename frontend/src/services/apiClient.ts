import axios from 'axios';
import { BACKEND_CONFIGS } from '../types';
import authService from './authService';

// Função para criar instância do axios para um backend específico
export const createApiClient = (backendType: keyof typeof BACKEND_CONFIGS) => {
  const config = BACKEND_CONFIGS[backendType];
  
  const client = axios.create({
    baseURL: config.baseURL,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Interceptor para adicionar token JWT automaticamente
  client.interceptors.request.use(
    (config) => {
      // Debug da autenticação
      //console.log(`[${backendType}] Usuário autenticado:`, authService.isAuthenticated());
      
      // Adicionar token de autenticação se disponível
      if (authService.isAuthenticated()) {
        const token = authService.getToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
          //console.log(`[${backendType}] Token JWT enviado:`, token.substring(0, 50) + '...');
        } else {
          console.warn(`[${backendType}] Token não disponível mesmo com usuário autenticado!`);
        }
      } else {
        console.warn(`[${backendType}] Usuário não autenticado - token não será enviado`);
      }

      //console.log(`[${backendType}] ${config.method?.toUpperCase()} ${config.url}`);
      //console.log(`[${backendType}] Headers:`, config.headers);
      //console.log(`[${backendType}] Request data:`, config.data);
      return config;
    },
    (error) => {
      console.error(`[${backendType}] Request error:`, error);
      return Promise.reject(error);
    }
  );

  client.interceptors.response.use(
    (response) => {
      //console.log(`[${backendType}] Response:`, response.status);
      return response;
    },
    (error) => {
      console.error(`[${backendType}] Response error:`, error.response?.status, error.message);
      console.error(`[${backendType}] Response data:`, error.response?.data);
      console.error(`[${backendType}] URL:`, error.config?.url);
      
      // Verifica se é um erro de autenticação
      if (error.response?.status === 401) {
        console.error(`[${backendType}] Token inválido ou expirado (401)`);
        // O authService já faz logout automático em caso de 401
      }
      
      // Verifica se é um erro de conexão
      if (error.code === 'ECONNREFUSED' || error.message.includes('Network Error')) {
        console.error(`[${backendType}] CONEXÃO RECUSADA! Backend não está rodando em ${config.baseURL}`);
      }
      
      // Extrai a mensagem de erro do backend, se disponível
      if (error.response?.data?.error) {
        error.userMessage = error.response.data.error;
      } else if (error.response?.data?.message) {
        error.userMessage = error.response.data.message;
      } else {
        error.userMessage = `Erro ${error.response?.status || 'desconhecido'}`;
      }
      
      return Promise.reject(error);
    }
  );

  return client;
};