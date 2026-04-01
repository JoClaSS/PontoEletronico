# Sistema de Ponto Eletrônico - Arquitetura MVC

## Visão Geral
Este projeto implementa um sistema de ponto eletrônico seguindo a **Arquitetura MVC (Model-View-Controller)** tradicional com Spring Boot.

## Estrutura do Projeto

### 📁 **Model Layer (entities/)**
**Responsabilidade**: Representa os dados e regras de negócio básicas

```
entities/
├── Usuario.java           // Entidade de funcionários  
├── PontoEletronico.java   // Registro de ponto
├── JornadaTrabalho.java   // Tipos de jornada
└── TipoPonto.java         // Enum: entrada_1, saida_1, entrada_2, saida_2, entrada_3, saida_3
```

**Características**:
- Anotações JPA (`@Entity`, `@Table`, etc.)
- Validações Bean Validation (`@NotNull`, `@Size`)
- Métodos de negócio básicos
- Mapeamento direto com banco de dados

### 📁 **View Layer (DTOs)**
**Responsabilidade**: Transfer Objects para entrada/saída de dados

```
dtos/
├── RegistrarPontoRequest.java    // Request para registrar ponto
├── PontoEletronicoResponse.java  // Response de consultas
└── RelatorioHorasResponse.java   // Response de relatórios
```

**Características**:
- Desacoplamento entre API e entidades
- Validações de entrada
- Formatação de dados de saída
- Estruturas específicas para cada endpoint

### 📁 **Controller Layer (controllers/)**
**Responsabilidade**: Endpoints REST - recebe requests e chama services

```
controllers/
├── UsuarioController.java           // CRUD de usuários
├── PontoEletronicoController.java   // Operações de ponto
└── JornadaTrabalhoController.java   // Consulta jornadas
```

**Características**:
- Anotações REST (`@RestController`, `@RequestMapping`)
- Validação de entrada (`@Valid`)
- Mapeamento HTTP (GET, POST, PUT, DELETE)
- Tratamento básico de logs

### 📁 **Service Layer (services/)**
**Responsabilidade**: Lógica de negócio e orquestração

```
services/
├── UsuarioService.java           // Regras de usuários
└── PontoEletronicoService.java   // Regras de ponto eletrônico
```

**Características**:
- Transações (`@Transactional`)
- Validações de negócio
- Cálculos de horas trabalhadas
- Orquestração entre repositories

### 📁 **Repository Layer (repositories/)**
**Responsabilidade**: Acesso aos dados com Spring Data JPA

```
repositories/
├── UsuarioRepository.java           // Queries de usuários
├── PontoEletronicoRepository.java   // Queries de pontos  
└── JornadaTrabalhoRepository.java   // Queries de jornadas
```

**Características**:
- Extends `JpaRepository<Entity, ID>`
- Queries customizadas com `@Query`
- Métodos convencionais do Spring Data
- Named queries para consultas complexas

### 📁 **Configuration Layer (config/)**
**Responsabilidade**: Configurações gerais da aplicação

```
config/
└── WebConfig.java    // Configuração CORS e Web
```

### 📁 **Exception Layer (exception/)**
**Responsabilidade**: Tratamento global de erros

```
exception/
└── GlobalExceptionHandler.java    // @RestControllerAdvice
```

## Comparação: MVC vs Clean Architecture

| Aspecto | **MVC Tradicional** | **Clean Architecture** |
|---------|-------------------|----------------------|
| **Estrutura** | Model → Service → Controller | Domain ← Application ← Interface ← Infrastructure |
| **Dependências** | Controller → Service → Repository → Entity | Domain ← Application ← Interface ← Infrastructure |
| **Entidades** | JPA Entities (Anemic Domain) | Rich Domain Models |
| **Regras de Negócio** | Principalmente em Services | Entities + Use Cases |
| **Testabilidade** | Integração com banco | Unit tests isolados |
| **Coupling** | Alto acoplamento com frameworks | Baixo acoplamento |
| **Complexidade** | Menor complexidade inicial | Maior complexidade estrutural |

## Endpoints da API

### 🟢 **Usuários**
```
GET    /api/usuarios              // Lista todos
GET    /api/usuarios/{id}         // Busca por ID
GET    /api/usuarios/email/{email} // Busca por email  
GET    /api/usuarios/buscar?nome=  // Busca por nome
POST   /api/usuarios              // Criar usuário
PUT    /api/usuarios/{id}         // Atualizar usuário
DELETE /api/usuarios/{id}         // Remover usuário
```

### 🟡 **Pontos Eletrônicos**
```
POST   /api/pontos                           // Registrar ponto
GET    /api/pontos/usuario/{id}?data=        // Pontos por data
GET    /api/pontos/usuario/{id}/periodo?     // Pontos por período
GET    /api/pontos/usuario/{id}/relatorio?   // Relatório de horas
DELETE /api/pontos/{pontoId}                 // Remover ponto
```

### 🔵 **Jornadas de Trabalho**
```
GET    /api/jornadas              // Lista todas
GET    /api/jornadas/{id}         // Busca por ID  
GET    /api/jornadas/buscar?nome= // Busca por nome
```

## Banco de Dados

### Tabelas Criadas
- `usuarios` - Funcionários do sistema
- `pontos_eletronicos` - Registros de ponto
- `jornadas_trabalho` - Tipos de jornada

### Migrations Flyway
- `V1__Create_initial_tables.sql` - Criação das tabelas
- `V2__Insert_initial_data.sql` - Dados iniciais

## Como Executar

1. **Configurar PostgreSQL**:
   ```sql
   CREATE DATABASE pontoeletronico;
   ```

2. **Executar aplicação**:
   ```bash
   mvn spring-boot:run
   ```

3. **Testar endpoints**:
   ```
   http://localhost:8081/api/usuarios
   ```

## Vantagens da Arquitetura MVC

✅ **Simplicidade**: Estrutura familiar e direta  
✅ **Produtividade**: Desenvolvimento rápido  
✅ **Frameworks**: Aproveitamento total do Spring Boot  
✅ **Pragmatismo**: Solução eficiente para maioria dos casos  

## Quando Utilizar MVC

- 🎯 **Aplicações CRUD** simples a médias
- 🚀 **Time-to-market** crítico  
- 👥 **Equipe familiarizada** com Spring Boot
- 📊 **Sistemas convencionais** de gestão
- 🔧 **Prototipagem rápida**

---

> **Resumo**: A arquitetura MVC oferece uma solução **pragmática e eficiente** para sistemas de médio porte, com foco na produtividade e simplicidade, sendo ideal quando as regras de negócio não são extremamente complexas e a estrutura tradicional atende às necessidades do projeto.