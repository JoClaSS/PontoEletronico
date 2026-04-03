# Keycloak com PostgreSQL

## Como usar:

1. **Iniciar os serviços:**
   ```bash
   cd docker
   docker-compose up -d
   ```

2. **Parar os serviços:**
   ```bash
   docker-compose down
   ```

3. **Ver os logs:**
   ```bash
   docker-compose logs -f keycloak
   docker-compose logs -f postgres-keycloak
   ```

## Acesso ao Keycloak:
- **URL:** http://localhost:8080
- **Admin:** admin
- **Senha:** admin

## Conexão DBeaver:

### Configurações de Conexão:
- **Tipo:** PostgreSQL
- **Host:** localhost
- **Porta:** 5433
- **Database:** keycloak
- **Usuário:** keycloak
- **Senha:** keycloak123

### Passos no DBeaver:
1. New Database Connection > PostgreSQL
2. Preencher os dados acima
3. Test Connection
4. Finish

## Tabelas principais do Keycloak:
- `user_entity` - Usuários
- `user_role_mapping` - Relação usuário-role
- `keycloak_role` - Roles do sistema
- `realm` - Realms configurados
- `client` - Clients configurados

## Comandos úteis:
```sql
-- Ver todos os usuários
SELECT id, username, email, first_name, last_name, enabled 
FROM user_entity;

-- Ver roles dos usuários
SELECT u.username, r.name as role_name
FROM user_entity u
JOIN user_role_mapping urm ON u.id = urm.user_id
JOIN keycloak_role r ON urm.role_id = r.id;

-- Ver usuários por realm
SELECT u.username, u.email, r.name as realm_name
FROM user_entity u
JOIN realm r ON u.realm_id = r.id;
```