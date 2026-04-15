import { useState, useCallback } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { apiService } from '../services/apiService';
import type { Usuario, PontoEletronico, RegistrarPontoRequest, RelatorioHorasResponse, FiltrosPontos, ApiState, CriarUsuarioRequest, ConfiguracaoEmpresa, AtualizarConfiguracaoRequest } from '../types';

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
    
    const loadFuncionarios = async () => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const funcionarios = await apiService.getFuncionarios();
        setState({ data: funcionarios, loading: false, error: null });
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao carregar funcionários';
        setState({ 
          data: null, 
          loading: false, 
          error: errorMessage
        });
      } finally {
        setLoading(false);
      }
    };

    const criarUsuario = async (usuario: CriarUsuarioRequest): Promise<Usuario | null> => {
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

    const desativarUsuario = async (id: string): Promise<void> => {
      setLoading(true);
      try {
        await apiService.desativarUsuario(id);
        // Recarrega a lista após desativar
        await loadUsuarios();
      } catch (error: any) {
        console.error('[useApi] Erro ao desativar usuário:', error);
        
        // Extrair mensagem mais específica do erro
        let errorMessage = 'Erro ao desativar usuário';
        
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        } else if (error.userMessage) {
          errorMessage = error.userMessage;
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        // Se contém "Keycloak", dar uma mensagem mais amigável
        if (errorMessage.includes('Keycloak')) {
          errorMessage = 'Usuário foi desativado localmente, mas houve problema com o sistema de autenticação. Por favor, verifique com o administrador.';
        }
        
        setState(prev => ({ 
          ...prev, 
          error: errorMessage
        }));
        throw new Error(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    const reativarUsuario = async (id: string): Promise<void> => {
      setLoading(true);
      try {
        await apiService.reativarUsuario(id);
        // Recarrega a lista após reativar
        await loadUsuarios();
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao reativar usuário';
        setState(prev => ({ 
          ...prev, 
          error: errorMessage
        }));
        throw new Error(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    return { ...state, loadUsuarios, loadFuncionarios, criarUsuario, desativarUsuario, reativarUsuario };
  };

  // Hook para operações de ponto
  const usePontos = (_usuarioId?: string) => {
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

  // Hook para configurações da empresa
  const useConfiguracoes = () => {
    const [state, setState] = useState<ApiState<ConfiguracaoEmpresa>>({
      data: null,
      loading: false,
      error: null
    });

    const loadConfiguracoes = useCallback(async () => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const configuracoes = await apiService.getConfiguracoes();
        setState({ data: configuracoes, loading: false, error: null });
        return configuracoes;
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao carregar configurações';
        setState({ 
          data: null, 
          loading: false, 
          error: errorMessage
        });
        throw new Error(errorMessage);
      } finally {
        setLoading(false);
      }
    }, []); // Removida dependência de setLoading para evitar loop

    const salvarConfiguracoes = useCallback(async (configuracoes: AtualizarConfiguracaoRequest): Promise<ConfiguracaoEmpresa | null> => {
      setState(prev => ({ ...prev, loading: true, error: null }));
      setLoading(true);
      
      try {
        const configuracoesSalvas = await apiService.salvarConfiguracoes(configuracoes);
        setState({ data: configuracoesSalvas, loading: false, error: null });
        return configuracoesSalvas;
      } catch (error: any) {
        const errorMessage = error.userMessage || error.message || 'Erro ao salvar configurações';
        setState(prev => ({ 
          ...prev, 
          error: errorMessage
        }));
        throw new Error(errorMessage);
      } finally {
        setLoading(false);
      }
    }, []); // Removida dependência de setLoading para evitar loop

    return { ...state, loadConfiguracoes, salvarConfiguracoes };
  };

  return {
    useUsuarios,
    usePontos,
    useRelatorios,
    useConfiguracoes
  };
};