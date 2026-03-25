# Exemplos de Uso da API - Ponto Eletrônico MVC

## 🚀 Iniciando a aplicação

```bash
cd mvcpontoeletronico
mvn spring-boot:run
```

**URL Base**: `http://localhost:8081`

---

## 📝 **1. Gerenciamento de Usuários**

### 1.1 Criar Usuário
```http
POST /api/usuarios
Content-Type: application/json

{
  "nome": "José Silva",
  "email": "jose.silva@empresa.com",
  "cpf": "123.456.789-00"
}
```

### 1.2 Listar Usuários
```http
GET /api/usuarios
```

### 1.3 Buscar por ID (pegue um ID da listagem anterior)
```http
GET /api/usuarios/{uuid-do-usuario}
```

---

## 🕐 **2. Registros de Ponto**

### 2.1 Registrar Ponto (Automático)
```http
POST /api/pontos
Content-Type: application/json

{
  "usuarioId": "{uuid-do-usuario}"
}
```
> O sistema determina automaticamente o tipo: ENTRADA → SAIDA_ALMOCO → RETORNO_ALMOCO → SAIDA

### 2.2 Registrar Ponto (Específico)
```http
POST /api/pontos
Content-Type: application/json

{
  "usuarioId": "{uuid-do-usuario}",
  "tipoPonto": "ENTRADA",
  "localizacao": "Escritório Central",
  "observacao": "Chegada pontual"
}
```

### 2.3 Consultar Pontos de Hoje
```http
GET /api/pontos/usuario/{uuid-do-usuario}?data=2026-03-24
```

### 2.4 Consultar Pontos do Mês
```http
GET /api/pontos/usuario/{uuid-do-usuario}/periodo?dataInicio=2026-03-01&dataFim=2026-03-31
```

### 2.5 Relatório de Horas Trabalhadas
```http
GET /api/pontos/usuario/{uuid-do-usuario}/relatorio?dataInicio=2026-03-01&dataFim=2026-03-31
```

---

## 📊 **3. Jornadas de Trabalho**

### 3.1 Listar Jornadas
```http
GET /api/jornadas
```

---

## 🧪 **4. Cenário de Teste Completo**

### Passo 1: Criar usuário
```bash
curl -X POST http://localhost:8081/api/usuarios \
-H "Content-Type: application/json" \
-d '{
  "nome": "Maria Santos",
  "email": "maria.santos@empresa.com", 
  "cpf": "987.654.321-01"
}'
```

### Passo 2: Registrar sequência de pontos (usar o ID retornado)
```bash
# Entrada (8h)
curl -X POST http://localhost:8081/api/pontos \
-H "Content-Type: application/json" \
-d '{
  "usuarioId": "SEU-UUID-AQUI",
  "tipoPonto": "ENTRADA",
  "dataHora": "2026-03-24T08:00:00"
}'

# Saída para almoço (12h)  
curl -X POST http://localhost:8081/api/pontos \
-H "Content-Type: application/json" \
-d '{
  "usuarioId": "SEU-UUID-AQUI",
  "tipoPonto": "SAIDA_ALMOCO", 
  "dataHora": "2026-03-24T12:00:00"
}'

# Retorno do almoço (13h)
curl -X POST http://localhost:8081/api/pontos \
-H "Content-Type: application/json" \
-d '{
  "usuarioId": "SEU-UUID-AQUI",
  "tipoPonto": "RETORNO_ALMOCO",
  "dataHora": "2026-03-24T13:00:00"
}'

# Saída (18h)
curl -X POST http://localhost:8081/api/pontos \
-H "Content-Type: application/json" \
-d '{
  "usuarioId": "SEU-UUID-AQUI", 
  "tipoPonto": "SAIDA",
  "dataHora": "2026-03-24T18:00:00"
}'
```

### Passo 3: Consultar relatório
```bash
curl "http://localhost:8081/api/pontos/usuario/SEU-UUID-AQUI/relatorio?dataInicio=2026-03-24&dataFim=2026-03-24"
```

**Resultado esperado**: `9h` trabalhadas (8h-12h + 13h-18h)

---

## 🔍 **5. Validações do Sistema**

### ✅ **Validações Implementadas**:
- ✅ Máximo 4 pontos por dia
- ✅ Intervalo mínimo de 15min entre registros  
- ✅ Não permite registros com mais de 1 dia
- ✅ Email e CPF únicos por usuário
- ✅ Sequência lógica de tipos de ponto

### ❌ **Casos que retornam erro**:

```http
POST /api/pontos
{
  "usuarioId": "uuid-inexistente"
}
// → 400 Bad Request: "Usuário não encontrado"
```

```http  
POST /api/pontos (5º registro do dia)
{
  "usuarioId": "uuid-valido"
}
// → 400 Bad Request: "Limite máximo de 4 registros por dia já atingido"
```

---

## 🛠 **6. Estrutura de Resposta**

### Sucesso (200/201):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "usuarioId": "550e8400-e29b-41d4-a716-446655440001", 
  "nomeUsuario": "José Silva",
  "dataHora": "2026-03-24T08:00:00",
  "tipoPonto": "ENTRADA",
  "tipoPontoDescricao": "Entrada",
  "localizacao": null,
  "observacao": null,
  "createdAt": "2026-03-24T15:58:58.123"
}
```

### Erro de Validação (400):
```json
{
  "timestamp": "2026-03-24T15:58:58.123",
  "status": 400,
  "error": "Bad Request", 
  "message": "Usuário não encontrado"
}
```

### Erro de Validação de Campos (400):
```json
{
  "timestamp": "2026-03-24T15:58:58.123",
  "status": 400,
  "error": "Validation Failed",
  "message": "Campos com valores inválidos",
  "errors": {
    "usuarioId": "ID do usuário é obrigatório",
    "nome": "Nome é obrigatório"  
  }
}
```

---

## 🎯 **7. Dicas de Uso**

1. **UUIDs**: Sempre use os UUIDs retornados pelas respostas
2. **Datas**: Formato ISO: `YYYY-MM-DD` ou `YYYY-MM-DDTHH:MM:SS`
3. **CORS**: Configurado para aceitar qualquer origem (desenvolvimento)
4. **Logs**: Ative logs DEBUG para acompanhar operações
5. **Banco**: Certifique-se que PostgreSQL está rodando na porta 5432

---

> **Pronto!** Agora você tem um sistema de ponto eletrônico MVC completo e funcional! 🚀