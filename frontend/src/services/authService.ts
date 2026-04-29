// Tipos para autenticação customizada
import type { Usuario } from '../types';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  usuario: Usuario;
  primeiroLogin?: boolean;
}

export interface AuthError {
  message: string;
  code?: string;
}

// Serviço de autenticação customizada
class AuthService {
  private token: string | null = null;
  private user: Usuario | null = null;
  private baseURL = 'http://localhost:8081/api';

  constructor() {
    // Recuperar token do localStorage na inicialização
    this.token = localStorage.getItem('auth_token');
    const userStr = localStorage.getItem('auth_user');
    if (userStr) {
      try {
        this.user = JSON.parse(userStr);
      } catch (e) {
        console.warn('Erro ao carregar usuário do localStorage:', e);
        localStorage.removeItem('auth_user');
      }
    }
  }

  async login(email: string, senha: string): Promise<LoginResponse> {
    try {
      const response = await fetch(`${this.baseURL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, senha }),
      });

      if (!response.ok) {
        if (response.status === 400) {
          throw new Error('Email ou senha incorretos');
        }
        throw new Error('Erro ao fazer login');
      }

      const data: LoginResponse = await response.json();
      
      // Salvar token e usuário
      this.token = data.token;
      this.user = data.usuario;
      
      localStorage.setItem('auth_token', data.token);
      localStorage.setItem('auth_user', JSON.stringify(data.usuario));
      localStorage.setItem('auth_login_time', Date.now().toString());

      return data;
    } catch (error) {
      console.error('Erro no login:', error);
      throw error;
    }
  }

  logout(): void {
    this.token = null;
    this.user = null;
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    localStorage.removeItem('auth_login_time');
  }

  isAuthenticated(): boolean {
    return !!this.token && !!this.user;
  }

  getToken(): string | null {
    return this.token;
  }

  getUser(): Usuario | null {
    return this.user;
  }

  hasRole(role: string): boolean {
    return this.user?.role === role;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isFuncionario(): boolean {
    return this.hasRole('FUNCIONARIO');
  }

  isVisitante(): boolean {
    return this.hasRole('VISITANTE');
  }

  // Verificar se o token ainda é válido
  async checkTokenValidity(): Promise<boolean> {
    if (!this.token) return false;

    // Verificar expiração local (9 horas = 32400000ms)
    const loginTime = localStorage.getItem('auth_login_time');
    if (loginTime) {
      const timeElapsed = Date.now() - parseInt(loginTime);
      const tokenExpireTime = 32400000; // 9 horas em ms
      
      if (timeElapsed >= tokenExpireTime) {
        console.log('🔐 Token expirado localmente, fazendo logout');
        this.logout();
        return false;
      }
    }

    try {
      const response = await fetch(`${this.baseURL}/auth/user-info`, {
        headers: {
          'Authorization': `Bearer ${this.token}`,
        },
      });

      if (response.ok) {
        return true;
      } else {
        // Token inválido, fazer logout
        this.logout();
        return false;
      }
    } catch (error) {
      console.error('Erro ao verificar token:', error);
      this.logout();
      return false;
    }
  }

  // Verificar tempo restante do token em minutos
  getTokenTimeRemaining(): number {
    const loginTime = localStorage.getItem('auth_login_time');
    if (!loginTime || !this.token) return 0;

    const timeElapsed = Date.now() - parseInt(loginTime);
    const tokenExpireTime = 32400000; // 9 horas em ms
    const timeRemaining = tokenExpireTime - timeElapsed;
    
    return Math.max(0, Math.floor(timeRemaining / 60000)); // retorna em minutos
  }

  // Verificar se o token está próximo da expiração (menos de 5 minutos)
  isTokenExpiringSoon(): boolean {
    return this.getTokenTimeRemaining() <= 5;
  }

  // Fazer requisições autenticadas
  async authenticatedFetch(url: string, options: RequestInit = {}): Promise<Response> {
    const headers = {
      ...options.headers,
    } as any;

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const response = await fetch(url, {
      ...options,
      headers,
    });

    // Se o token expirou, fazer logout
    if (response.status === 401) {
      this.logout();
      window.location.reload();
    }

    return response;
  }

  // Trocar senha do usuário
  async trocarSenha(senhaAtual: string, novaSenha: string, confirmarSenha: string): Promise<void> {
    try {
      const response = await fetch(`${this.baseURL}/auth/trocar-senha`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.token}`,
        },
        body: JSON.stringify({
          senhaAtual,
          novaSenha,
          confirmarSenha
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Erro ao alterar senha');
      }

      // Após trocar a senha com sucesso, atualizar o status do usuário
      if (this.user) {
        this.user.primeiroLogin = false;
        localStorage.setItem('auth_user', JSON.stringify(this.user));
      }
    } catch (error) {
      console.error('Erro ao trocar senha:', error);
      throw error;
    }
  }
}

const authService = new AuthService();
export default authService;