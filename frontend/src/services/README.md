# Services - Arquitetura Organizada

Esta pasta contém os services do frontend organizados por domínio, espelhando a estrutura dos controllers do backend.

## Estrutura dos Services

### Services Específicos por Domínio

- **`authService.ts`** - Gerencia autenticação e autorização
  - Login/logout
  - Verificação de token
  - Troca de senhas
  - Validação de roles

- **`usuarioService.ts`** - Gerencia usuários
  - CRUD de usuários
  - Busca por funcionários ativos
  - Ativação/desativação

- **`pontoService.ts`** - Gerencia pontos eletrônicos
  - Registro de pontos
  - Consulta de pontos por período
  - Relatórios de horas
  - Lista de presença

- **`solicitacaoService.ts`** - Gerencia solicitações
  - CRUD de solicitações
  - Upload/download de anexos
  - Aprovação/rejeição
  - Contadores e estatísticas

- **`configuracaoService.ts`** - Gerencia configurações da empresa
  - Buscar/salvar configurações
  - Horários de trabalho
  - Informações da empresa

- **`systemService.ts`** - Informações do sistema
  - Versão da aplicação
  - Metadados do sistema

- **`jornadaService.ts`** - Gerencia jornadas de trabalho
  - Tipos de jornada disponíveis

### Services Utilitários

- **`apiClient.ts`** - Utilitário para criar instâncias do Axios
  - Configuração de interceptors
  - Gerenciamento de tokens JWT
  - Tratamento de erros padrão

- **`apiService.ts`** - Camada de abstração centralizada
  - Agrega todos os services específicos
  - Mantém compatibilidade com código existente
  - Interface unificada

- **`index.ts`** - Facilita importações
  - Exporta todos os services
  - Tipos relacionados

## Como Usar

### Importação Específica (Recomendado)
```typescript
import { usuarioService } from '../services/usuarioService';
import { pontoService } from '../services/pontoService';

// Usar diretamente o service específico
const usuarios = await usuarioService.getUsuarios();
const pontos = await pontoService.getPontosDeHoje(userId);
```

### Importação Centralizada
```typescript
import { apiService } from '../services/apiService';

// Usar através da camada de abstração
const usuarios = await apiService.getUsuarios();
const pontos = await apiService.getPontosDeHoje(userId);
```

### Importação do Índice
```typescript
import { usuarioService, pontoService } from '../services';

// Múltiplos services de uma vez
```

## Benefícios da Nova Arquitetura

1. **Separação de Responsabilidades** - Cada service tem uma responsabilidade específica
2. **Manutenibilidade** - Mais fácil de encontrar e modificar funcionalidades
3. **Testabilidade** - Services menores e focados são mais fáceis de testar
4. **Escalabilidade** - Fácil adicionar novos services ou expandir existentes
5. **Organização** - Espelha a estrutura do backend (controllers)
6. **Reutilização** - Services específicos podem ser reutilizados em diferentes componentes

## Migração

Todos os componentes foram atualizados para usar os services específicos através de imports dinâmicos:

```typescript
// ✅ Novo padrão
const { usuarioService } = await import('../../services/usuarioService');
const { pontoService } = await import('../../services/pontoService');
const { solicitacaoService } = await import('../../services/solicitacaoService');
```

## Padrões de Nomenclatura

- **Classes**: PascalCase (`UsuarioService`)
- **Instâncias**: camelCase (`usuarioService`)
- **Métodos**: camelCase descritivo (`getUsuarios`, `criarUsuario`)
- **Arquivos**: camelCase com sufixo Service (`usuarioService.ts`)