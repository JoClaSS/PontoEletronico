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
  private initPromise: Promise<boolean> | null = null;

  async init(): Promise<boolean> {
    // Se já está inicializando ou já inicializou, retorna o resultado
    if (this.initPromise) {
      return this.initPromise;
    }
    
    if (this.initialized) {
      return keycloak.authenticated || false;
    }

    try {
      this.initPromise = keycloak.init(initOptions);
      const authenticated = await this.initPromise;
      this.initialized = true;
      // Configurar refresh automático do token
      if (authenticated) {
        this.setupTokenRefresh();
      }
      
      return authenticated;
    } catch (error) {
      console.error('Erro ao inicializar Keycloak:', error);
      this.initPromise = null; // Reset promise em caso de erro
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

  // Login direto com usuário/senha (Resource Owner Password Credentials)
  async loginDirect(username: string, password: string): Promise<boolean> {
    try {
      const response = await fetch(`/keycloak/realms/${keycloakConfig.realm}/protocol/openid-connect/token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'password',
          client_id: keycloakConfig.clientId,
          username: username,
          password: password,
          scope: 'openid profile email'
        })
      });

      if (!response.ok) {
        return false;
      }

      const tokenData = await response.json();
      
      console.log('Token data recebido:', {
        access_token_length: tokenData.access_token?.length,
        expires_in: tokenData.expires_in,
        token_type: tokenData.token_type
      });
      
      // Definir tokens no keycloak
      keycloak.token = tokenData.access_token;
      keycloak.refreshToken = tokenData.refresh_token;
      keycloak.idToken = tokenData.id_token;
      
      // Parse do token para obter informações do usuário
      keycloak.tokenParsed = this.parseJwt(tokenData.access_token);
      keycloak.refreshTokenParsed = tokenData.refresh_token ? this.parseJwt(tokenData.refresh_token) : null;
      keycloak.idTokenParsed = tokenData.id_token ? this.parseJwt(tokenData.id_token) : null;

      // Definir o status de autenticação
      (keycloak as any).authenticated = true;
      (keycloak as any).loginRequired = false;

      // Configurar refresh automático
      this.setupTokenRefresh();
      
      return true;
    } catch (error) {
      console.error('Erro no login direto:', error);
      return false;
    }
  }

  // Função auxiliar para fazer parse do JWT
  private parseJwt(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      const parsed = JSON.parse(jsonPayload);
      
      return parsed;
    } catch (error) {
      console.error('Erro ao fazer parse do JWT:', error);
      return null;
    }
  }

  logout(): void {
    console.log('Fazendo logout...');
    
    // Limpar tokens e status
    keycloak.token = undefined;
    keycloak.refreshToken = undefined;
    keycloak.idToken = undefined;
    keycloak.tokenParsed = undefined;
    keycloak.refreshTokenParsed = undefined;
    keycloak.idTokenParsed = undefined;
    (keycloak as any).authenticated = false;
    (keycloak as any).loginRequired = true;
    
    console.log('Logout completo, tokens limpos');
    
    // Redirecionar para a página de login do Keycloak se necessário
    // keycloak.logout({ redirectUri: window.location.origin });
  }

  // Abrir página de reset de senha do Keycloak
  openResetPassword(): void {
    const resetUrl = `/keycloak/realms/${keycloakConfig.realm}/protocol/openid-connect/auth?client_id=${keycloakConfig.clientId}&redirect_uri=${encodeURIComponent(window.location.origin)}&response_type=code&scope=openid&kc_action=UPDATE_PASSWORD`;
    
    // Abre em nova janela popup
    window.open(resetUrl, 'resetPassword', 'width=800,height=600,scrollbars=yes,resizable=yes');
  }

  isAuthenticated(): boolean {
    const hasTokens = !!keycloak.token;
    const keycloakAuth = (keycloak as any).authenticated;
    let tokenExpired = true;
    
    if (keycloak.token && keycloak.tokenParsed) {
      const currentTime = Math.floor(Date.now() / 1000);
      const tokenExp = keycloak.tokenParsed.exp;
      tokenExpired = tokenExp < currentTime;
    }
    
    return keycloakAuth && hasTokens && !tokenExpired;
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

  // Método de debug para verificar status completo
  getDebugInfo(): any {
    return {
      initialized: this.initialized,
      authenticated: this.isAuthenticated(),
      keycloakAuthenticated: (keycloak as any).authenticated,
      hasToken: !!keycloak.token,
      tokenExpired: keycloak.token ? keycloak.isTokenExpired() : 'no token',
      userProfile: this.getUserProfile(),
      tokenPreview: keycloak.token ? keycloak.token.substring(0, 50) + '...' : null
    };
  }
}

// Exportar instância singleton
const keycloakService = new KeycloakService();
export default keycloakService;

// Exportar também a instância do Keycloak para casos específicos
export { keycloak };