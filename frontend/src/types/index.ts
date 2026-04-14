// Tipos para entidades do sistema de ponto eletrônico

// Tipos de ponto (comum aos dois backends)
export const TipoPonto = {
  ENTRADA_1: 'entrada_1',
  SAIDA_1: 'saida_1',
  ENTRADA_2: 'entrada_2',
  SAIDA_2: 'saida_2',
  ENTRADA_3: 'entrada_3',
  SAIDA_3: 'saida_3'
} as const;

export type TipoPonto = typeof TipoPonto[keyof typeof TipoPonto];

// Roles do sistema
export const RoleType = {
  FUNCIONARIO: 'FUNCIONARIO',
  ADMIN: 'ADMIN'
} as const;

export type RoleType = typeof RoleType[keyof typeof RoleType];

// Usuario - estrutura comum
export interface Usuario {
  id: string;
  nome: string;
  email: string;
  cpf?: string; // apenas no backend MVC
  role?: RoleType; // role do usuário
  cargo?: string; // apenas no backend Clean
  departamento?: string; // apenas no backend Clean
  ativo?: boolean; // indica se usuário está ativo
  primeiroLogin?: boolean; // indica se é o primeiro login do usuário
  createdAt?: string;
  updatedAt?: string;
}

// Request para criar usuário
export interface CriarUsuarioRequest {
  nome: string;
  email: string;
  senha: string;
  cpf: string;
  role: RoleType;
}

// Request para trocar senha
export interface TrocaSenhaRequest {
  senhaAtual: string;
  novaSenha: string;
  confirmarSenha: string;
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
  // Estados de autenticação
  isLoggedIn: boolean;
  loggedUser: Usuario | null;
  handleLogin: (user: Usuario) => void;
  handleLogout: () => void;
  // Sistema de notificação de pontos
  pontosUpdateTrigger: number;
  notifyPontosUpdate: () => void;
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

// Interface para exibição agrupada de pontos por data
export interface PontoAgrupado {
  data: string; // YYYY-MM-DD
  entrada1?: string; // HH:mm
  saida1?: string; // HH:mm
  entrada2?: string; // HH:mm
  saida2?: string; // HH:mm
  entrada3?: string; // HH:mm
  saida3?: string; // HH:mm
  horasTrabalhadas?: string; // Horas totais trabalhadas no formato HH:mm
  observacao?: string;
}

// Solicitações - Enums e Interfaces
export const StatusSolicitacao = {
  ABERTO: 'ABERTO',
  RESOLVIDO: 'RESOLVIDO',
  CANCELADO: 'CANCELADO'
} as const;

export type StatusSolicitacao = typeof StatusSolicitacao[keyof typeof StatusSolicitacao];

export interface MotivoSolicitacao {
  id: string;
  descricao: string;
  ativo?: boolean;
  requerAnexo: boolean;
  createdAt?: string;
}

export interface Solicitacao {
  id: string;
  dataReferencia: string; // YYYY-MM-DD
  usuarioId: string;
  nomeUsuario: string;
  motivo: {
    id: string;
    descricao: string;
  };
  descricao?: string;
  status: StatusSolicitacao;
  statusDescricao: string;
  anexoNome?: string;
  anexoTipo?: string;
  anexoTamanho?: number;
  temAnexo: boolean;
  diasConsecutivos?: boolean;
  quantidadeDias?: number;
  createdAt: string;
  updatedAt: string;
}

export interface CriarSolicitacaoRequest {
  dataReferencia: string; // YYYY-MM-DD
  usuarioId: string;
  motivoId: string;
  descricao: string; // Obrigatório
  anexoNome?: string;
  anexoTipo?: string;
  anexoTamanho?: number;
  anexoConteudo?: Uint8Array;
  diasConsecutivos?: boolean;
  quantidadeDias?: number;
}

// Configurações da Empresa
export interface ConfiguracaoEmpresa {
  id?: string;
  nomeEmpresa: string;
  horarioCheckin: string; // HH:mm
  horarioCheckout: string; // HH:mm
  fotoEmpresa?: string; // base64 ou URL
  logoEmpresaNome?: string; // nome do arquivo original
  logoEmpresaTipo?: string; // tipo MIME
  logoEmpresaTamanho?: number; // tamanho em bytes
  createdAt?: string;
  updatedAt?: string;
}

export interface AtualizarConfiguracaoRequest {
  nomeEmpresa: string;
  horarioCheckin: string;
  horarioCheckout: string;
  fotoEmpresa?: string;
  logoEmpresaNome?: string;
  logoEmpresaTipo?: string;
  logoEmpresaTamanho?: number;
}