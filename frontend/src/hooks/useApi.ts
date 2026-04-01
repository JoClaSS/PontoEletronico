import { useState } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { apiService } from '../services/apiService';
import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos, ApiState } from '../types';

export const useApi = () => {
  const { setLoading } = useAppContext();

  // Hook para carregar usuários
  const useUsuarios = () => {
    const [state, setState] = useState<ApiState<Usuario[]>>({
      data: null,
      loading: false,
      error: null
    });

    const loadUsuarios = async () => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const usuarios = await apiService.getUsuarios();
        setState({ data: usuarios, loading: false, error: null });
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao carregar usuários';
        setState({ 
          data: null, 
          loading: false, 
          error: errorMessage
        });
      } finally {
        setLoading(false);
      }
    };

    const criarUsuario = async (usuario: Omit<Usuario, 'id' | 'createdAt' | 'updatedAt'>): Promise<Usuario | null> => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const novoUsuario = await apiService.criarUsuario(usuario);
        // Recarrega a lista após criar
        await loadUsuarios();
        return novoUsuario;
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao criar usuário';
        setState(prev => ({ 
          ...prev, 
          error: errorMessage
        }));
        throw new Error(errorMessage); // Re-propaga com mensagem personalizada
      } finally {
        setLoading(false);
      }
    };

    return { ...state, loadUsuarios, criarUsuario };
  };

  // Hook para operações de ponto
  const usePontos = (usuarioId?: string) => {
    const [state, setState] = useState<ApiState<PontoEletronico[]>>({
      data: null,
      loading: false,
      error: null
    });

    const registrarPonto = async (data: RegistrarPontoRequest): Promise<PontoEletronico | null> => {
      setLoading(true);
      
      try {
        const ponto = await apiService.registrarPonto(data);
        // Recarrega a lista após registrar usando o usuarioId do request
        if (data.usuarioId) {
          const hoje = new Date().toISOString().split('T')[0];
          await loadPontosPorData(data.usuarioId, hoje);
        }
        return ponto;
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao registrar ponto';
        setState(prev => ({ 
          ...prev, 
          error: errorMessage
        }));
        throw error; // Re-propaga o erro para que o componente possa capturar
      } finally {
        setLoading(false);
      }
    };

    const loadPontosDeHoje = async (userId: string) => {
      console.log('[useApi] Carregando pontos para usuário:', userId);
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const pontos = await apiService.getPontosDeHoje(userId);
        console.log('[useApi] Pontos carregados:', pontos);
        setState({ data: pontos, loading: false, error: null });
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao carregar pontos';
        console.error('[useApi] Erro ao carregar pontos:', error);
        setState({ 
          data: null, 
          loading: false, 
          error: errorMessage
        });
      } finally {
        setLoading(false);
      }
    };

    const loadPontosPorData = async (userId: string, data: string) => {
      console.log('[useApi] Carregando pontos para usuário:', userId, 'data:', data);
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const pontos = await apiService.getPontosPorData(userId, data);
        console.log('[useApi] Pontos carregados por data:', pontos);
        setState({ data: pontos, loading: false, error: null });
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao carregar pontos por data';
        console.error('[useApi] Erro ao carregar pontos por data:', error);
        setState({ 
          data: null, 
          loading: false, 
          error: errorMessage
        });
      } finally {
        setLoading(false);
      }
    };

    const loadPontosPorPeriodo = async (userId: string, filtros: FiltrosPontos) => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const pontos = await apiService.getPontosPorPeriodo(userId, filtros);
        setState({ data: pontos, loading: false, error: null });
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao carregar pontos por período';
        setState({ 
          data: null, 
          loading: false, 
          error: errorMessage
        });
      } finally {
        setLoading(false);
      }
    };

    return { 
      ...state, 
      registrarPonto, 
      loadPontosDeHoje, 
      loadPontosPorData,
      loadPontosPorPeriodo 
    };
  };

  // Hook para relatórios
  const useRelatorios = () => {
    const [state, setState] = useState<ApiState<RelatorioHorasResponse>>({
      data: null,
      loading: false,
      error: null
    });

    const loadRelatorioHoras = async (usuarioId: string, filtros: FiltrosPontos) => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const relatorio = await apiService.getRelatorioHoras(usuarioId, filtros);
        setState({ data: relatorio, loading: false, error: null });
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao gerar relatório';
        setState({ 
          data: null, 
          loading: false, 
          error: errorMessage
        });
      } finally {
        setLoading(false);
      }
    };

    return { ...state, loadRelatorioHoras };
  };

  return {
    useUsuarios,
    usePontos,
    useRelatorios
  };
};