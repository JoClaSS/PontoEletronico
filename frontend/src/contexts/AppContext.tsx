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

  const value: AppContextType = {
    selectedUser,
    setSelectedUser,
    usuarios,
    setUsuarios,
    loading,
    setLoading,
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