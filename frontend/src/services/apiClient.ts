import axios from 'axios';
import { BACKEND_CONFIGS } from '../types';

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

  // Interceptor para logs (desenvolvimento)
  client.interceptors.request.use(
    (config) => {
      console.log(`[${backendType}] ${config.method?.toUpperCase()} ${config.url}`);
      console.log(`[${backendType}] Request data:`, config.data);
      return config;
    },
    (error) => {
      console.error(`[${backendType}] Request error:`, error);
      return Promise.reject(error);
    }
  );

  client.interceptors.response.use(
    (response) => {
      console.log(`[${backendType}] Response:`, response.status);
      return response;
    },
    (error) => {
      console.error(`[${backendType}] Response error:`, error.response?.status, error.message);
      console.error(`[${backendType}] Response data:`, error.response?.data);
      console.error(`[${backendType}] URL:`, error.config?.url);
      
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