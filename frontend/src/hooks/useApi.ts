import { useState, useEffect } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { apiService } from '../services/apiService';
import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos, ApiState } from '../types';

export const useApi = () => {
  const { selectedBackend, setLoading } = useAppContext();

  // Atualiza o backend no serviço quando mudar no contexto
  useEffect(() => {
    apiService.setBackend(selectedBackend);
  }, [selectedBackend]);

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
      } catch (error) {
        setState({ 
          data: null, 
          loading: false, 
          error: error instanceof Error ? error.message : 'Erro ao carregar usuários' 
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
      } catch (error) {
        setState(prev => ({ 
          ...prev, 
          error: error instanceof Error ? error.message : 'Erro ao criar usuário' 
        }));
        throw error; // Re-propaga o erro para o componente
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
        // Recarrega a lista após registrar
        if (usuarioId) {
          await loadPontosDeHoje(usuarioId);
        }
        return ponto;
      } catch (error) {
        setState(prev => ({ 
          ...prev, 
          error: error instanceof Error ? error.message : 'Erro ao registrar ponto' 
        }));
        return null;
      } finally {
        setLoading(false);
      }
    };

    const loadPontosDeHoje = async (userId: string) => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const pontos = await apiService.getPontosDeHoje(userId);
        setState({ data: pontos, loading: false, error: null });
      } catch (error) {
        setState({ 
          data: null, 
          loading: false, 
          error: error instanceof Error ? error.message : 'Erro ao carregar pontos' 
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
      } catch (error) {
        setState({ 
          data: null, 
          loading: false, 
          error: error instanceof Error ? error.message : 'Erro ao carregar histórico' 
        });
      } finally {
        setLoading(false);
      }
    };

    return { 
      ...state, 
      registrarPonto, 
      loadPontosDeHoje, 
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
      } catch (error) {
        setState({ 
          data: null, 
          loading: false, 
          error: error instanceof Error ? error.message : 'Erro ao gerar relatório' 
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