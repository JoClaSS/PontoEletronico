# Sistema de Ponto Eletrônico com Keycloak

Sistema completo de controle de ponto eletrônico com autenticação Keycloak, desenvolvido com Spring Boot (backend) e React + TypeScript (frontend).

## 🚀 Funcionalidades

- ✅ **Autenticação via Keycloak** - Sistema seguro de login/logout
- ✅ **Controle de Roles** - Diferenciação entre admin e usuários
- ✅ **Registro de Pontos** - Entrada, saída, pausa
- ✅ **Gestão de Usuários** - CRUD completo de funcionários
- ✅ **Relatórios de Frequência** - Visualização de pontos registrados
- ✅ **Sistema de Solicitações** - Correções e ajustes de ponto
- ✅ **API REST Segura** - Todos endpoints protegidos por JWT
- ✅ **Interface Responsiva** - Material-UI com design moderno

## 🛠️ Tecnologias

**Backend:**
- Spring Boot 3.2
- Spring Security + OAuth2 Resource Server
- PostgreSQL com Flyway
- Maven

**Frontend:**
- React 19 + TypeScript
- Material-UI (MUI)
- Keycloak-js
- Axios
- Vite

**Autenticação:**
- Keycloak 23+
- JWT Tokens
- RBAC (Role-Based Access Control)

## 📋 Pré-requisitos

- Java 17+
- Node.js 18+
- PostgreSQL 12+
- Docker (para Keycloak)

## 🚀 Instalação e Execução

### 1. Setup do Keycloak

```bash
# Executar Keycloak via Docker
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
```

**Configure o Keycloak seguindo o guia detalhado:** [KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md)

### 2. Setup do Banco de Dados

```sql
-- Criar database
CREATE DATABASE pontoeletronico;
```

### 3. Backend (Spring Boot)

```bash
cd mvcpontoeletronico

# Configurar application.properties com suas credenciais do DB
# Executar aplicação
mvn spring-boot:run
```

O backend estará disponível em: `http://localhost:8081`

### 4. Frontend (React)

```bash
cd frontend

# Instalar dependências
npm install

# Executar em modo desenvolvimento
npm run dev
```

O frontend estará disponível em: `http://localhost:5173`

## 🔐 Usuários Padrão

Após configurar o Keycloak, você terá:

- **Admin**: `admin@pontoeletronico.com` / `admin123`
  - Acesso total ao sistema
  
- **Usuario**: `user@pontoeletronico.com` / `user123`
  - Acesso limitado (próprios pontos)

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Keycloak      │    │   Backend       │
│   React + TS    │◄──►│   Auth Server   │◄──►│   Spring Boot   │
│   Port: 5173    │    │   Port: 8080    │    │   Port: 8081    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                               ┌─────────────────┐
                                               │   PostgreSQL    │
                                               │   Database      │
                                               └─────────────────┘
```

### Fluxo de Autenticação

1. **Usuario acessa frontend** → Redirecionado para Keycloak
2. **Login no Keycloak** → JWT token gerado
3. **Frontend recebe token** → Armazena e inclui em requisições
4. **Backend valida JWT** → Autoriza acesso aos endpoints
5. **Renovação automática** → Token refreshado transparentemente

## 📁 Estrutura do Projeto

```
PontoEletronico/
├── frontend/                     # React TypeScript App
│   ├── src/
│   │   ├── components/          # Componentes React
│   │   ├── contexts/           # Context Providers
│   │   │   ├── AppContext.tsx  # Estado global da app
│   │   │   └── KeycloakContext.tsx # Contexto de autenticação
│   │   ├── services/           # Serviços e API calls
│   │   └── types/              # Definições TypeScript
│   └── public/
│       └── silent-check-sso.html # SSO silencioso
├── mvcpontoeletronico/          # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/empresa/mvcpontoeletronico/
│   │       ├── config/         # Configurações (Security, CORS)
│   │       ├── controllers/    # REST Controllers
│   │       ├── dtos/          # Data Transfer Objects
│   │       ├── entities/      # JPA Entities
│   │       └── services/      # Business Logic
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/      # Scripts Flyway
├── KEYCLOAK_SETUP.md           # Guia de configuração Keycloak
└── README.md                   # Este arquivo
```

## 🔧 Configuração de Desenvolvimento

### Backend Configuration
```properties
# application.properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/pontoeletronico
app.cors.allowed-origins=http://localhost:5173
```

### Frontend Configuration
```typescript
// keycloakService.ts
const keycloakConfig = {
  url: 'http://localhost:8080/',
  realm: 'pontoeletronico',
  clientId: 'ponto-frontend'
};
```

## 🧪 Testando a Integração

1. **Inicie os serviços** na ordem:
   - Keycloak (Docker)
   - PostgreSQL
   - Backend (Spring Boot)
   - Frontend (React)

2. **Acesse** `http://localhost:5173`

3. **Teste o login** com usuários configurados

4. **Verifique os logs** para troubleshooting

## 📝 Endpoints da API

### Autenticação
- `GET /api/auth/user-info` - Informações do usuário atual
- `GET /api/auth/is-admin` - Verificar se é admin
- `POST /api/auth/logout` - Logout (processado pelo frontend)

### Pontos
- `GET /api/pontos` - Listar pontos do usuário
- `POST /api/pontos` - Registrar novo ponto

### Usuários (Admin)
- `GET /api/usuarios` - Listar usuários
- `POST /api/usuarios` - Criar usuário
- `PUT /api/usuarios/{id}` - Atualizar usuário

### Solicitações
- `GET /api/solicitacoes` - Listar solicitações
- `POST /api/solicitacoes` - Criar solicitação

## 🚨 Troubleshooting

### Problemas Comuns

1. **CORS Error**: 
   - Verifique configuração no Keycloak (Web Origins)
   - Confirme CORS no backend

2. **Token Expired**:
   - Verificar configuração de timeout no Keycloak
   - Implementação de refresh funcionando

3. **404 - Realm not found**:
   - Confirmar nome do realm no Keycloak
   - Verificar URL do issuer

### Logs Importantes

```bash
# Backend - logs de segurança
logging.level.org.springframework.security=DEBUG

# Frontend - console do navegador
# Network tab para ver requisições com tokens

# Keycloak - logs do container
docker logs <container-id>
```

## 🔒 Segurança

- **JWT Tokens** com assinatura RSA256
- **CORS** configurado adequadamente  
- **Roles** controladas pelo Keycloak
- **Endpoints protegidos** por role/authentication
- **Refresh automático** de tokens
- **Logout seguro** com revogação de sessão

## 📈 Próximos Passos

- [ ] Implementar notificações em tempo real
- [ ] Dashboard analítico para admins
- [ ] Export de relatórios (PDF/Excel)
- [ ] App mobile (React Native)
- [ ] Integração com Windows AD
- [ ] Auditoria completa de ações

## 📄 Licença

Este projeto é de uso interno da empresa. Todos os direitos reservados.

---

**Desenvolvido com ❤️ usando Spring Boot + React + Keycloak**