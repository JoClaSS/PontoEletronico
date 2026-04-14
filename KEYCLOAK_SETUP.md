# Configuração do Keycloak para Ponto Eletrônico

Este documento descreve como configurar o Keycloak para autenticação no sistema de Ponto Eletrônico.

## 1. Instalação do Keycloak

### Via Docker (Recomendado)
```bash
# Baixar e executar Keycloak
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
```

### Download Manual
1. Baixe o Keycloak de: https://www.keycloak.org/downloads
2. Extraia o arquivo
3. Execute: `bin/kc.bat start-dev` (Windows) ou `bin/kc.sh start-dev` (Linux/Mac)

## 2. Configuração Inicial

1. Acesse: http://localhost:8080
2. Faça login com `admin/admin` (ou as credenciais definidas)
3. Crie um novo realm chamado: `pontoeletronico`

## 3. Configuração do Realm

### 3.1 Criar o Realm
1. Clique em "Create Realm"
2. Nome: `pontoeletronico`
3. Enabled: `ON`
4. Clique em "Create"

### 3.2 Configurar o Client
1. Vá para "Clients" > "Create client"
2. Configurações:
   - **Client ID**: `ponto-frontend`
   - **Client type**: `OpenID Connect`
   - **Client authentication**: `OFF` (público)
3. Capability config:
   - **Standard flow**: `ON`
   - **Direct access grants**: `ON`
   - **Implicit flow**: `OFF`
   - **Service accounts roles**: `OFF`
4. Login settings:
   - **Valid redirect URIs**: `http://localhost:5173/*`
   - **Valid post logout redirect URIs**: `http://localhost:5173/*`
   - **Web origins**: `http://localhost:5173`

### 3.3 Configurar CORS
No client `ponto-frontend`:
- **Web Origins**: `http://localhost:5173`
- **Valid Redirect URIs**: `http://localhost:5173/*`

## 4. Criar Roles

1. Vá para "Realm roles" > "Create role"
2. Crie as seguintes roles:
   - **admin**: Para administradores do sistema
   - **user**: Para usuários normais

## 5. Criar Usuários de Teste

### 5.1 Usuário Admin
1. Vá para "Users" > "Create new user"
2. Configurações:
   - **Username**: `admin`
   - **Email**: `admin@pontoeletronico.com`
   - **First name**: `Administrador`
   - **Last name**: `Sistema`
   - **Email verified**: `ON`
   - **Enabled**: `ON`
3. Na aba "Credentials":
   - Defina senha: `admin123`
   - **Temporary**: `OFF`
4. Na aba "Role mapping":
   - Assign roles: `admin`, `user`

### 5.2 Usuário Normal
1. Crie outro usuário:
   - **Username**: `user`
   - **Email**: `user@pontoeletronico.com`
   - **First name**: `Usuário`
   - **Last name**: `Teste`
   - **Password**: `user123`
2. Role mapping: `user`

## 6. Configurações de Segurança

### 6.1 Configurar Token Timeouts
Em "Realm settings" > "Tokens":
- **Access Token Lifespan**: 15 minutes
- **Refresh Token Max Reuse**: 0
- **SSO Session Idle**: 30 minutes
- **SSO Session Max**: 10 hours

### 6.2 Configurar Login Settings
Em "Realm settings" > "Login":
- **User registration**: `OFF` (ou `ON` se quiser permitir auto-registro)
- **Email as username**: `ON`
- **Forgot password**: `ON`
- **Remember me**: `ON`

## 7. Verificação da Configuração

### 7.1 Endpoints Importantes
- **Issuer URL**: `http://localhost:8080/realms/pontoeletronico`
- **Authorization Endpoint**: `http://localhost:8080/realms/pontoeletronico/protocol/openid-connect/auth`
- **Token Endpoint**: `http://localhost:8080/realms/pontoeletronico/protocol/openid-connect/token`
- **JWK Set URI**: `http://localhost:8080/realms/pontoeletronico/protocol/openid-connect/certs`

### 7.2 Testar Configuração
1. Inicie o backend: `mvn spring-boot:run`
2. Inicie o frontend: `npm run dev`
3. Acesse: `http://localhost:5173`
4. Teste o login com: `admin@pontoeletronico.com` / `admin123`

## 8. Configurações de Produção

### 8.1 Backend (application.properties)
```properties
# Produção - alterar URLs para o servidor Keycloak real
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://keycloak.empresa.com/realms/pontoeletronico
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://keycloak.empresa.com/realms/pontoeletronico/protocol/openid-connect/certs
app.cors.allowed-origins=https://ponto.empresa.com
```

### 8.2 Frontend (keycloakService.ts)
```typescript
const keycloakConfig = {
  url: 'https://keycloak.empresa.com/',
  realm: 'pontoeletronico',
  clientId: 'ponto-frontend'
};
```

## 9. Troubleshooting

### Problemas Comuns:
1. **CORS Error**: Verifique as configurações de Web Origins no client
2. **Invalid Redirect URI**: Verifique os Valid Redirect URIs
3. **Token Expired**: Verifique as configurações de timeout dos tokens
4. **Realm Not Found**: Verifique se o realm está habilitado e o nome está correto

### Logs Úteis:
- **Keycloak**: Console do Docker ou logs do servidor
- **Backend**: Logs do Spring Boot (nivel DEBUG para security)
- **Frontend**: Console do navegador (Network tab para requisições)

## 10. Comandos Úteis

```bash
# Instalar dependências do frontend
cd frontend
npm install

# Executar frontend em modo desenvolvimento
npm run dev

# Executar backend
cd mvcpontoeletronico  
mvn spring-boot:run

# Verificar se Keycloak está rodando
curl http://localhost:8080/realms/pontoeletronico/.well-known/openid_configuration
```