// Tipos para entidades do sistema de ponto eletrônico

// Tipos de ponto (comum aos dois backends)
export const TipoPonto = {
  ENTRADA: 'ENTRADA',
  SAIDA_ALMOCO: 'SAIDA_ALMOCO',
  RETORNO_ALMOCO: 'RETORNO_ALMOCO',  
  SAIDA: 'SAIDA'
} as const;

export type TipoPonto = typeof TipoPonto[keyof typeof TipoPonto];

// Usuario - estrutura comum
export interface Usuario {
  id: string;
  nome: string;
  email: string;
  cpf?: string; // apenas no backend MVC
  cargo?: string; // apenas no backend Clean
  departamento?: string; // apenas no backend Clean
  ativo?: boolean; // apenas no backend Clean
  createdAt?: string;
  updatedAt?: string;
}

// PontoEletronico - estrutura comum
export interface PontoEletronico {
  id: string;
  usuarioId?: string; // Clean architecture
  usuario?: Usuario; // MVC
  dataHora: string;
  tipo: TipoPonto;
  localizacao?: string;
  observacao?: string;
  dispositivoId?: string; // apenas no backend Clean
  createdAt?: string;
}

// DTOs para requests
export interface RegistrarPontoRequest {
  usuarioId?: string; // para backend Clean
  usuario?: { id: string }; // para backend MVC
  dataHora?: string; // opcional, usa now() se não informado
  localizacao?: string;
  observacao?: string;
  dispositivoId?: string;
}

// DTOs para responses específicos
export interface RelatorioHorasResponse {
  usuarioId: string;
  usuario: Usuario;
  dataInicio: string;
  dataFim: string;
  totalHoras: number;
  diasTrabalhados: number;
  pontos: PontoEletronico[];
}

// Backends disponíveis
export const Backend = {
  MVC: 'MVC',
  CLEAN: 'CLEAN'
} as const;

export type Backend = typeof Backend[keyof typeof Backend];

// Configuração do backend
export interface BackendConfig {
  type: Backend;
  name: string;
  baseURL: string;
  port: number;
}

// Configurações dos backends
export const BACKEND_CONFIGS: Record<string, BackendConfig> = {
  MVC: {
    type: Backend.MVC,
    name: 'MVC Architecture',
    baseURL: 'http://localhost:8081',
    port: 8081
  },
  CLEAN: {
    type: Backend.CLEAN,
    name: 'Clean Architecture',
    baseURL: 'http://localhost:8080',
    port: 8080
  }
};

// Context types
export interface AppContextType {
  selectedUser: Usuario | null;
  setSelectedUser: (user: Usuario | null) => void;
  usuarios: Usuario[];
  setUsuarios: (usuarios: Usuario[]) => void;
  loading: boolean;
  setLoading: (loading: boolean) => void;
}

// Filtros para consulta de pontos
export interface FiltrosPontos {
  dataInicio?: string;
  dataFim?: string;
  usuarioId?: string;
}

// Estado de loading/erro
export interface ApiState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}