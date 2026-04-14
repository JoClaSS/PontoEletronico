import React, { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';
import type { Usuario, AppContextType } from '../types/index';

// Context padrão
const AppContext = createContext<AppContextType | undefined>(undefined);

// Provider props
interface AppProviderProps {
  children: ReactNode;
}

// Provider do contexto
export const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
  const [selectedUser, setSelectedUser] = useState<Usuario | null>(null);
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  
  // Sistema de notificação para atualizações de pontos
  const [pontosUpdateTrigger, setPontosUpdateTrigger] = useState<number>(0);
  
  // Estados de autenticação
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  const [loggedUser, setLoggedUser] = useState<Usuario | null>(null);

  // Métodos de autenticação
  const handleLogin = (user: Usuario) => {
    setLoggedUser(user);
    setIsLoggedIn(true);
    // Salva no localStorage para persistir login (simulado)
    localStorage.setItem('pontoeletronico_user', JSON.stringify(user));
    localStorage.setItem('pontoeletronico_logged', 'true');
  };

  const handleLogout = () => {
    setLoggedUser(null);
    setIsLoggedIn(false);
    setSelectedUser(null); // Limpa usuário selecionado
    setUsuarios([]); // Limpa lista de usuários
    // Remove do localStorage
    localStorage.removeItem('pontoeletronico_user');
    localStorage.removeItem('pontoeletronico_logged');
  };

  // Recupera login do localStorage na inicialização
  React.useEffect(() => {
    const savedUser = localStorage.getItem('pontoeletronico_user');
    const savedLoggedStatus = localStorage.getItem('pontoeletronico_logged');
    
    if (savedUser && savedLoggedStatus === 'true') {
      try {
        const user = JSON.parse(savedUser);
        setLoggedUser(user);
        setIsLoggedIn(true);
      } catch (error) {
        console.error('Erro ao recuperar login salvo:', error);
        handleLogout(); // Limpa dados inválidos
      }
    }
  }, []);

  // Função para notificar que pontos foram atualizados
  const notifyPontosUpdate = () => {
    setPontosUpdateTrigger(prev => prev + 1);
  };

  const value: AppContextType = {
    selectedUser,
    setSelectedUser,
    usuarios,
    setUsuarios,
    loading,
    setLoading,
    // Autenticação
    isLoggedIn,
    loggedUser,
    handleLogin,
    handleLogout,
    // Sistema de notificação de pontos
    pontosUpdateTrigger,
    notifyPontosUpdate,
  };

  return (
    <AppContext.Provider value={value}>
      {children}
    </AppContext.Provider>
  );
};

// Hook para usar o contexto
export const useAppContext = (): AppContextType => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext deve ser usado dentro de um AppProvider');
  }
  return context;
};