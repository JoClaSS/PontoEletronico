# Sistema de Ponto Eletrônico - Arquitetura Limpa

Este projeto implementa um sistema de ponto eletrônico seguindo os princípios da **Clean Architecture** (Arquitetura Limpa) proposta por Robert C. Martin.

## �️ **Tecnologias Utilizadas**

### **Backend**
- **Java 17** - Linguagem principal
- **Spring Boot 4.0.3** - Framework principal
- **Spring Data JPA** - Persistência
- **PostgreSQL** - Banco de dados principal
- **H2** - Banco em memória para testes
- **Flyway** - Migrations e versionamento do banco
- **Lombok** - Redução de boilerplate
- **JUnit 5** - Testes unitários

### **Arquitetura**
- **Clean Architecture** - Padrão arquitetural
- **Hexagonal Architecture** - Padrão de portas e adaptadores
- **Domain-Driven Design (DDD)** - Modelagem do domínio
- **SOLID Principles** - Princípios de design

## �📁 Estrutura do Projeto

### 🎯 Camada de Domínio (`domain`)
**Núcleo da aplicação - Regras de negócio puras**

#### Entidades (`domain/entities`)
- **PontoEletronico**: Representa um registro de entrada/saída
- **Usuario**: Representa um funcionário do sistema
- **JornadaTrabalho**: Define horários e regras de trabalho
- **TipoPonto**: Enum para tipos de registro (entrada, saída, etc.)

#### Repositórios (`domain/repositories`)
- **PontoEletronicoRepository**: Interface para persistência de pontos
- **UsuarioRepository**: Interface para persistência de usuários

### ⚙️ Camada de Aplicação (`application`)
**Casos de uso e regras de negócio da aplicação**

#### Casos de Uso (`application/usecases`)
- **RegistrarPontoUseCase**: Registra entrada/saída de funcionário
- **ConsultarPontosUseCase**: Consulta registros por data/período
- **CalcularHorasTrabalhadasUseCase**: Calcula horas trabalhadas e saldo

### 🔌 Camada de Interface (`adapters`)
**Adaptadores para comunicação externa**

#### Controllers REST (`adapters/web/controllers`)
- **PontoEletronicoController**: API REST para operações de ponto
- **UsuarioController**: API REST para consulta de usuários

#### DTOs (`adapters/web/dto`)
- **RegistrarPontoRequestDto**: Request para registro de ponto
- **PontoEletronicoResponseDto**: Response de ponto eletrônico
- **RelatorioHorasResponseDto**: Response de relatório de horas

#### Mappers (`adapters/web/mappers`)
- **PontoEletronicoMapper**: Conversão entre entidades e DTOs

### 🏗️ Camada de Infraestrutura (`infrastructure`)
**Frameworks, banco de dados e tecnologias externas**

#### Persistência (`infrastructure/persistence`)
- **Entidades JPA**: JornadaTrabalhoJpaEntity, UsuarioJpaEntity, PontoEletronicoJpaEntity
- **Repositórios JPA**: Spring Data JPA para acesso ao banco PostgreSQL
- **Implementações**: UsuarioRepositoryJpaImpl, PontoEletronicoRepositoryJpaImpl
- **Mappers**: PersistenceMapper para conversão entre entidades de domínio e JPA

#### Migrations (`infrastructure/migrations`)
- **Flyway**: Controle de versionamento do banco de dados
- **V1__Create_initial_tables.sql**: Criação das tabelas principais
- **V2__Insert_initial_data.sql**: Dados iniciais do sistema

#### Configuração (`infrastructure/config`)
- **UseCaseConfiguration**: Configuração de injeção de dependências
- **PostgreSQL**: Banco de dados principal
- **H2**: Banco em memória para testes

## 🚀 Endpoints da API

### Registro de Ponto
```http
POST /api/pontos
Content-Type: application/json

{
  "usuarioId": "user-1",
  "dataHora": "2026-03-14T08:00:00",
  "localizacao": "Escritório Central",
  "observacao": "Entrada normal",
  "dispositivoId": "DEVICE-001"
}
```

### Consultar Pontos
```http
# Pontos do dia atual
GET /api/pontos/usuario/{usuarioId}

# Pontos de uma data específica
GET /api/pontos/usuario/{usuarioId}/data/{data}

# Pontos de um período
GET /api/pontos/usuario/{usuarioId}/periodo?dataInicio={inicio}&dataFim={fim}
```

### Relatório de Horas
```http
# Horas de um dia específico
GET /api/pontos/usuario/{usuarioId}/horas/data/{data}

# Horas de um período
GET /api/pontos/usuario/{usuarioId}/horas/periodo?dataInicio={inicio}&dataFim={fim}
```

### Usuários
```http
# Listar todos os usuários
GET /api/usuarios

# Listar usuários ativos
GET /api/usuarios/ativos

# Buscar por ID
GET /api/usuarios/{id}

# Buscar por email
GET /api/usuarios/email/{email}
```

## 🏛️ Princípios da Clean Architecture Aplicados

### 1. **Inversão de Dependências**
- As camadas internas não dependem das externas
- Repositórios são interfaces no domínio, implementadas na infraestrutura

### 2. **Separação de Responsabilidades**
- **Entidades**: Regras de negócio essenciais
- **Casos de Uso**: Regras de negócio da aplicação
- **Controllers**: Interface com o mundo externo
- **Repositórios**: Persistência de dados

### 3. **Independência de Frameworks**
- O núcleo não depende do Spring Boot
- Fácil troca de tecnologias de persistência
- Testabilidade independente de frameworks

## �️ **Banco de Dados**

### **PostgreSQL**
O sistema utiliza PostgreSQL como banco de dados principal com as seguintes configurações:

```properties
# Configurações do banco PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/pontoeletronico
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### **Migrations com Flyway**
As migrations são executadas automaticamente pelo Flyway no startup da aplicação:

- **V1__Create_initial_tables.sql**: Cria tabelas jornadas_trabalho, usuarios e pontos_eletronicos
- **V2__Insert_initial_data.sql**: Insere dados iniciais do sistema

#### **Como executar manualmente**:
```bash
# Executa migrations
mvn flyway:migrate

# Limpa o banco (cuidado!)
mvn flyway:clean

# Informações sobre migrations
mvn flyway:info
```

### **Estrutura das Tabelas**

#### **jornadas_trabalho**
- id (UUID, PK)
- nome (VARCHAR)
- inicio_expediente, fim_expediente (TIME)
- inicio_intervalo, fim_intervalo (TIME)
- carga_horaria_diaria (INTERVAL)
- permite_hora_extra (BOOLEAN)
- limite_hora_extra (INTERVAL)

#### **usuarios**
- id (UUID, PK)
- email (VARCHAR, UNIQUE)
- nome (VARCHAR)
- cargo, departamento (VARCHAR)
- ativo (BOOLEAN)
- jornada_id (UUID, FK)

#### **pontos_eletronicos**
- id (UUID, PK)
- usuario_id (UUID, FK)
- data_hora (TIMESTAMP)
- tipo (VARCHAR) - ENUM: ENTRADA, SAIDA, ENTRADA_ALMOCO, SAIDA_ALMOCO
- localizacao, observacao (TEXT)
- dispositivo_id (VARCHAR)

## �🔮 Próximos Passos

### Autenticação Keycloak
1. Adicionar dependências do Keycloak
2. Configurar security no Spring
3. Implementar extração de usuário do token JWT

### Funcionalidades Adicionais
1. Validação de localização (GPS)
2. Fotos para registro de ponto
3. Aprovação de ajustes de ponto
4. Relatórios gerenciais
5. Notificações de inconsistências

### Melhorias de Performance
1. Implementar cache Redis
2. Otimização de consultas SQL
3. Paginação em endpoints de consulta

## 🧪 Como Testar

### **Passe 1: Configurar o Banco**
```bash
# 1. Criar o banco PostgreSQL
createdb pontoeletronico

# 2. Executar a aplicação (migrations automáticas)
mvn spring-boot:run
```

### **Usuários Pré-cadastrados** (inseridos via migration)
- **Email**: `jose@empresa.com` - José Silva (Desenvolvedor)
- **Email**: `maria@empresa.com` - Maria Santos (Analista de RH)  
- **Email**: `carlos@empresa.com` - Carlos Oliveira (Gerente de Projetos)

> **Nota**: Os IDs são UUIDs gerados automaticamente. Use o endpoint `/api/usuarios` para buscar os IDs reais.

### **Fluxo de Teste**
1. Busque um usuário: `GET /api/usuarios/email/jose@empresa.com`
2. Copie o ID retornado
3. Registre entrada, saída para almoço, entrada pós-almoço, saída
4. Consulte o relatório de horas

## 📊 Exemplo de Fluxo Completo

```bash
# 1. Buscar usuário por email
curl http://localhost:8080/api/usuarios/email/jose@empresa.com

# 2. Usar o UUID retornado nos próximos comandos
USER_ID="f1e2d3c4-b5a6-7890-cdef-123456789012"

# 3. Entrada
curl -X POST http://localhost:8080/api/pontos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":"'$USER_ID'","localizacao":"Escritório"}'

# 4. Saída para almoço  
curl -X POST http://localhost:8080/api/pontos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":"'$USER_ID'","localizacao":"Escritório"}'

# 5. Entrada pós-almoço
curl -X POST http://localhost:8080/api/pontos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":"'$USER_ID'","localizacao":"Escritório"}'

# 6. Saída
curl -X POST http://localhost:8080/api/pontos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":"'$USER_ID'","localizacao":"Escritório"}'

# 7. Relatório do dia
curl http://localhost:8080/api/pontos/usuario/$USER_ID/horas/data/2026-03-14
```

## 🚀 **Como Executar**

### **Desenvolvimento**
```bash
# 1. Configurar PostgreSQL local
# 2. Criar banco: pontoeletronico
# 3. Executar aplicação
mvn spring-boot:run
```

### **Testes**
```bash
# Testes unitários (com mocks - rápidos e isolados)
mvn test -Dtest=*UseCaseTest

# Testes de integração (requer PostgreSQL configurado)
mvn integration-test
```

### **Tipos de Teste**

#### **Testes Unitários** 
- **Localização**: `src/test/java/.../*UseCaseTest.java`
- **Tecnologia**: JUnit 5 + Mockito
- **Características**: 
  - Usa mocks para repositórios
  - Não precisa de banco de dados
  - Execução rápida
  - Foca na lógica de negócio

#### **Testes de Integração**
- **Localização**: `src/test/java/.../*ApplicationTests.java` 
- **Tecnologia**: Spring Boot Test
- **Características**:
  - Requer PostgreSQL configurado
  - Testa integração completa
  - Usa JPA real