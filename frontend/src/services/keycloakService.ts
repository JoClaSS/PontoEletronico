import Keycloak from 'keycloak-js';

// Configuração do Keycloak
const keycloakConfig = {
  url: 'http://localhost:8080/',
  realm: 'MundialCiclo',
  clientId: 'ponto-frontend'
};

// Instância do Keycloak
const keycloak = new Keycloak(keycloakConfig);

// Opções de inicialização
const initOptions = {
  onLoad: 'check-sso' as 'check-sso',
  silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
  pkceMethod: 'S256' as 'S256',
  checkLoginIframe: false
};

// Interface para informações do usuário
export interface UserProfile {
  id?: string;
  username?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  roles?: string[];
  emailVerified?: boolean;
}

// Serviço de autenticação
class KeycloakService {
  private initialized = false;

  async init(): Promise<boolean> {
    try {
      const authenticated = await keycloak.init(initOptions);
      this.initialized = true;
      
      // Configurar refresh automático do token
      if (authenticated) {
        this.setupTokenRefresh();
      }
      
      return authenticated;
    } catch (error) {
      console.error('Erro ao inicializar Keycloak:', error);
      return false;
    }
  }

  private setupTokenRefresh() {
    // Renovar token automaticamente quando expirar (5 minutos antes)
    setInterval(() => {
      if (keycloak.token) {
        keycloak.updateToken(300)
          .then(refreshed => {
            if (refreshed) {
              console.log('Token renovado');
            }
          })
          .catch(error => {
            console.error('Erro ao renovar token:', error);
            this.logout();
          });
      }
    }, 60000); // Verificar a cada minuto
  }

  login(): Promise<void> {
    return keycloak.login();
  }

  logout(): void {
    keycloak.logout({
      redirectUri: window.location.origin
    });
  }

  isAuthenticated(): boolean {
    return !!keycloak.token && !keycloak.isTokenExpired();
  }

  getToken(): string | undefined {
    return keycloak.token;
  }

  getUserProfile(): UserProfile | null {
    if (!keycloak.tokenParsed) return null;

    const token = keycloak.tokenParsed as any;
    
    return {
      id: token.sub,
      username: token.preferred_username,
      email: token.email,
      firstName: token.given_name,
      lastName: token.family_name,
      emailVerified: token.email_verified,
      roles: token.realm_access?.roles || []
    };
  }

  hasRole(role: string): boolean {
    const profile = this.getUserProfile();
    return profile?.roles?.includes(role) || false;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isFuncionario(): boolean {
    return this.hasRole('FUNCIONARIO');
  }

  isMaster(): boolean {
    return this.hasRole('MASTER');
  }

  getAuthHeader(): string {
    const token = this.getToken();
    return token ? `Bearer ${token}` : '';
  }

  // Método para ser usado nos interceptors do Axios
  isInitialized(): boolean {
    return this.initialized;
  }
}

// Exportar instância singleton
const keycloakService = new KeycloakService();
export default keycloakService;

// Exportar também a instância do Keycloak para casos específicos
export { keycloak };