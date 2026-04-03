import React, { createContext, useContext, useEffect, useState } from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';
import keycloakService, { type UserProfile } from '../services/keycloakService';

interface KeycloakContextType {
  isAuthenticated: boolean;
  userProfile: UserProfile | null;
  login: () => void;
  logout: () => void;
  hasRole: (role: string) => boolean;
  isAdmin: () => boolean;
  isFuncionario: () => boolean;
  isMaster: () => boolean;
  getToken: () => string | undefined;
}

const KeycloakContext = createContext<KeycloakContextType | undefined>(undefined);

export const useKeycloak = () => {
  const context = useContext(KeycloakContext);
  if (!context) {
    throw new Error('useKeycloak deve ser usado dentro de um KeycloakProvider');
  }
  return context;
};

interface KeycloakProviderProps {
  children: React.ReactNode;
}

export const KeycloakProvider: React.FC<KeycloakProviderProps> = ({ children }) => {
  const [isInitialized, setIsInitialized] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const initKeycloak = async () => {
      try {
        const authenticated = await keycloakService.init();
        setIsAuthenticated(authenticated);
        
        if (authenticated) {
          const profile = keycloakService.getUserProfile();
          setUserProfile(profile);
        }
      } catch (error) {
        console.error('Erro ao inicializar Keycloak:', error);
        setError('Erro ao conectar com o sistema de autenticação');
      } finally {
        setIsInitialized(true);
      }
    };

    initKeycloak();
  }, []);

  const login = () => {
    keycloakService.login();
  };

  const logout = () => {
    keycloakService.logout();
  };

  const hasRole = (role: string) => {
    return keycloakService.hasRole(role);
  };

  const isAdmin = () => {
    return keycloakService.isAdmin();
  };

  const isFuncionario = () => {
    return keycloakService.isFuncionario();
  };

  const isMaster = () => {
    return keycloakService.isMaster();
  };

  const getToken = () => {
    return keycloakService.getToken();
  };

  if (!isInitialized) {
    return (
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        minHeight="100vh"
        gap={2}
      >
        <CircularProgress size={60} />
        <Typography variant="h6" color="textSecondary">
          Iniciando sistema de autenticação...
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        minHeight="100vh"
        gap={2}
      >
        <Typography variant="h6" color="error">
          {error}
        </Typography>
        <Typography variant="body2" color="textSecondary">
          Por favor, recarregue a página ou contate o administrador.
        </Typography>
      </Box>
    );
  }

  const contextValue: KeycloakContextType = {
    isAuthenticated,
    userProfile,
    login,
    logout,
    hasRole,
    isAdmin,
    isFuncionario,
    isMaster,
    getToken
  };

  return (
    <KeycloakContext.Provider value={contextValue}>
      {children}
    </KeycloakContext.Provider>
  );
};