import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';
import authService from '../services/authService';
import type { Usuario } from '../types';

interface AuthContextType {
  isAuthenticated: boolean;
  user: Usuario | null;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => void;
  hasRole: (role: string) => boolean;
  isAdmin: () => boolean;
  isFuncionario: () => boolean;
  getToken: () => string | null;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<Usuario | null>(null);
  const [loading, setLoading] = useState(true);
  const hasInitialized = useRef(false);

  useEffect(() => {
    if (hasInitialized.current) return;
    hasInitialized.current = true;

    const initAuth = async () => {
      console.log('🔐 Iniciando autenticação...');
      try {
        // Verificar se há token no localStorage (inicialização simples)
        const isAuthenticated = authService.isAuthenticated();
        console.log('🔐 Token encontrado:', isAuthenticated);
        
        if (isAuthenticated) {
          const userData = authService.getUser();
          console.log('🔐 Dados do usuário:', userData);
          setUser(userData);
          setIsAuthenticated(true);
        } else {
          console.log('🔐 Usuário não autenticado');
          setIsAuthenticated(false);
          setUser(null);
        }
      } catch (error) {
        console.error('❌ Erro na inicialização da autenticação:', error);
        setIsAuthenticated(false);
        setUser(null);
      } finally {
        console.log('🔐 Autenticação inicializada, loading:', false);
        setLoading(false);
      }
    };

    initAuth();
  }, []);

  const login = async (email: string, senha: string): Promise<void> => {
    try {
      const response = await authService.login(email, senha);
      setUser(response.usuario);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Erro no login:', error);
      throw error;
    }
  };

  const logout = (): void => {
    authService.logout();
    setIsAuthenticated(false);
    setUser(null);
  };

  const hasRole = (role: string): boolean => {
    return authService.hasRole(role);
  };

  const isAdmin = (): boolean => {
    return authService.isAdmin();
  };

  const isFuncionario = (): boolean => {
    return authService.isFuncionario();
  };

  const getToken = (): string | null => {
    return authService.getToken();
  };

  const contextValue: AuthContextType = {
    isAuthenticated,
    user,
    login,
    logout,
    hasRole,
    isAdmin,
    isFuncionario,
    getToken,
    loading,
  };

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
        flexDirection="column"
        gap={2}
      >
        <CircularProgress size={60} />
        <Typography variant="h6" color="textSecondary">
          Carregando...
        </Typography>
      </Box>
    );
  }

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};