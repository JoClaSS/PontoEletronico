# Resultados e Discussão

## 1. Visão Geral dos Resultados

A partir da metodologia descrita, obtiveram-se dados quantitativos (contagem de
arquivos por camada e resultados da suíte de testes automatizados) e
qualitativos (inspeção do código-fonte das camadas equivalentes) para os dois
projetos desenvolvidos: `mvcpontoeletronico` (Arquitetura em Camadas) e
`arquiteturalimpa` (Clean Architecture). Os resultados são apresentados a
seguir, organizados por categoria de análise.

## 2. Resultados Quantitativos

### 2.1 Distribuição de Arquivos por Camada

A varredura da estrutura de diretórios de cada projeto contabilizou o total de
arquivos-fonte Java (`.java`) em `src/main/java`, agrupados por camada/pacote
de responsabilidade:

**`mvcpontoeletronico` (Arquitetura em Camadas) — 51 arquivos**

| Camada (pacote) | Arquivos | Responsabilidade |
|---|---|---|
| `entities` | 9 | Entidades JPA (modelo anêmico) |
| `dtos` / `dto` | 14 | Objetos de transferência de dados |
| `controllers` | 7 | Endpoints REST |
| `services` | 6 | Regras de negócio |
| `repositories` | 6 | Acesso a dados (`JpaRepository`) |
| `security` | 4 | JWT, autenticação, `UserDetails` |
| `config` | 3 | Configuração Spring (CORS, Security) |
| `exception` | 1 | Tratamento global de erros |
| raiz | 1 | Classe de bootstrap (`@SpringBootApplication`) |
| **Total** | **51** | |

**`arquiteturalimpa` (Clean Architecture) — 76 arquivos**

| Camada (pacote) | Arquivos | Responsabilidade |
|---|---|---|
| `domain.model` | 6 | Modelos de domínio ricos, sem anotações de framework |
| `domain.enums` | 3 | Enumerações de domínio |
| `domain.exception` | 3 | Exceções de negócio |
| `domain.security` | 2 | Portas (`PasswordHasher`, `TokenProvider`) |
| `domain.repository` | 6 | Portas de persistência (interfaces) |
| `application.*` (services) | 6 | Casos de uso |
| `application.*.dto` | 13 | DTOs por *feature* |
| `interfaces.web` (+ exception) | 8 | Controllers REST + *handler* global |
| `infrastructure.security` | 5 | Implementação das portas de segurança |
| `infrastructure.config` | 3 | Configuração Spring |
| `infrastructure.persistence.adapter` | 6 | Adaptadores porta → JPA |
| `infrastructure.persistence.mapper` | 2 | Conversão entidade JPA ↔ domínio |
| `infrastructure.persistence.jpa` | 6 | Interfaces `JpaRepository` |
| `infrastructure.persistence.entity` | 6 | Entidades JPA (`*JpaEntity`) |
| raiz | 1 | Classe de bootstrap |
| **Total** | **76** | |

A Clean Architecture resultou em **49% mais arquivos** que a Arquitetura em
Camadas (76 contra 51) para o mesmo escopo funcional. O acréscimo concentrou-se
majoritariamente na camada de infraestrutura (28 arquivos, correspondente a
37% do total), decorrente da introdução de adaptadores, *mappers* e da
duplicação do modelo de dados em duas representações (modelo de domínio e
entidade JPA) — estrutura inexistente na versão em camadas, na qual a entidade
JPA é reutilizada diretamente como modelo de negócio.

### 2.2 Resultados da Suíte de Testes Automatizados

A execução de `mvn test` (Maven Surefire Plugin) em ambos os projetos, sob as
mesmas condições de ambiente, produziu os seguintes resultados:

| Classe de teste | `mvcpontoeletronico` | `arquiteturalimpa` |
|---|---|---|
| `AuthServiceTest` | 2 testes, 0 falhas, 0.686 s | 2 testes, 0 falhas, 1.523 s |
| `PontoEletronicoServiceTest` | 2 testes, 0 falhas, 0.149 s | 2 testes, 0 falhas, 0.106 s |
| `SolicitacaoServiceTest` | 2 testes, 0 falhas, 0.128 s | 2 testes, 0 falhas, 0.078 s |
| `UsuarioServiceTest` | 2 testes, 0 falhas, 0.088 s | 2 testes, 0 falhas, 0.026 s |
| Teste de contexto (`*ApplicationTests`) | 1 teste, 0 falhas, 6.083 s | 1 teste, 0 falhas, 0.001 s |
| **Total** | **9 testes, 0 falhas, 0 erros** | **9 testes, 0 falhas, 0 erros** |

Ambos os projetos alcançaram **100% de aprovação** nos testes unitários
escritos para as camadas de serviço/caso de uso, com o mesmo número de classes
de teste e de casos de teste por classe, o que confirma a equivalência
funcional entre as duas implementações. Observou-se, contudo, uma diferença
expressiva no tempo de carregamento do contexto Spring: 6,083 s no
`mvcpontoeletronico` contra 0,001 s no `arquiteturalimpa`. Essa diferença é
atribuída à ordem de execução e ao estado de *cache* do Maven/Spring no momento
da coleta e **não** foi isolada estatisticamente (execução única, sem
repetições), de modo que não se pôde afirmar, a partir apenas desse dado, que a
Clean Architecture inicializa de forma consistentemente mais rápida.

### 2.3 Ampliação da Cobertura de Testes (Camada de Apresentação)

A suíte inicial cobria exclusivamente a camada de serviço/caso de uso,
deixando controllers, filtros de segurança e adaptadores de persistência sem
nenhum teste automatizado. Para reduzir essa lacuna, foram adicionados testes
de fatia web (`@WebMvcTest`, com o filtro JWT excluído da fatia via
`excludeFilters` e `@AutoConfigureMockMvc(addFilters = false)`) para os 7
controllers REST de cada projeto, além de testes unitários para o
`ConfiguracaoEmpresaService`, anteriormente não testado em nenhuma das duas
versões. A execução de `mvn test` após a ampliação produziu os seguintes
resultados, em ambos os projetos:

| Categoria | `mvcpontoeletronico` | `arquiteturalimpa` |
|---|---|---|
| Testes de serviço/caso de uso (suíte original) | 9 | 9 |
| Testes de `ConfiguracaoEmpresaService` (novo) | 3 | 3 |
| Testes de controller (`@WebMvcTest`, 7 classes, novo) | 18 | 18 |
| **Total** | **30 testes, 0 falhas, 0 erros** | **30 testes, 0 falhas, 0 erros** |

O acréscimo elevou a cobertura de **9 para 30 testes por projeto** (aumento de
233%), com 100% de aprovação em ambos, mantendo a paridade estrutural entre as
duas suítes (mesmo número de classes de teste e de casos por classe). A
ampliação também revelou duas diferenças de comportamento não identificadas na
suíte original, descritas na Seção 3.3.

## 3. Resultados Qualitativos: Acoplamento e Testabilidade

A inspeção do código da funcionalidade de autenticação (`AuthService`),
presente em ambos os projetos, evidenciou diferenças estruturais relevantes:

- Em `mvcpontoeletronico`, o `AuthService` depende diretamente de classes do
  Spring Security (`AuthenticationManager`, `PasswordEncoder`) e da injeção via
  `@Autowired` em campos, além de conter chamadas de depuração
  (`System.out.println`) misturadas à lógica de autenticação.
- Em `arquiteturalimpa`, o `AuthService` depende exclusivamente de portas de
  domínio (`PasswordHasher`, `TokenProvider`, `UsuarioRepository`), sem
  qualquer referência a classes do Spring Security ou instrução de depuração,
  com injeção via construtor (`@RequiredArgsConstructor`).

Essa diferença se refletiu diretamente nos testes unitários correspondentes:
o teste do `mvcpontoeletronico` precisou duplicar (via *mock*) dependências do
próprio *framework* de segurança (`AuthenticationManager`, `PasswordEncoder`),
enquanto o teste do `arquiteturalimpa` mockou apenas abstrações do domínio da
aplicação. Ambos os testes utilizaram a mesma ferramenta (JUnit 5 + Mockito) e
tiveram a mesma quantidade de casos de teste, porém o teste da versão em
camadas ficou **acoplado a detalhes de infraestrutura de segurança**, o que, em
um cenário de troca de mecanismo de autenticação (por exemplo, substituição do
Spring Security por outra biblioteca), exigiria alteração do teste mesmo sem
mudança na regra de negócio — situação que não ocorreria na versão em Clean
Architecture, pois o teste depende apenas do contrato (porta), não da
implementação.

### 3.3 Achados da Camada de Controller Reveladas pelos Novos Testes

Os testes de fatia web adicionados na Seção 2.3 expuseram duas diferenças de
comportamento entre as arquiteturas que não eram visíveis nos testes de
serviço isoladamente:

- **Tratamento de erro no login**: em `mvcpontoeletronico`, o `AuthController`
  envolve a chamada a `authService.login(...)` em um bloco `catch (Exception e)`
  genérico que retorna sempre `400 Bad Request` com corpo vazio (`body(null)`),
  independentemente da causa real da falha (usuário inexistente, senha
  incorreta, usuário inativo, etc.). O teste `AuthControllerTest` confirmou
  esse comportamento: uma falha de autenticação retorna 400 sem nenhuma
  mensagem no corpo da resposta. Em `arquiteturalimpa`, o `AuthController` não
  captura a exceção; ela se propaga até o `GlobalExceptionHandler`, que traduz
  `AuthenticationFailedException` em `401 Unauthorized` com um corpo JSON
  estruturado contendo a mensagem de erro. O teste equivalente confirmou o
  código de status correto e a presença da mensagem `"Credenciais inválidas"`
  no corpo da resposta — evidência concreta de que a camada de infraestrutura
  de erro é mais semanticamente correta e mais informativa para o consumidor
  da API na versão Clean Architecture.
- **Acoplamento direto controller → repositório**: o teste do
  `JornadaTrabalhoController` revelou que, em `mvcpontoeletronico`, o
  controller injeta e chama diretamente o `JornadaTrabalhoRepository`
  (`JpaRepository`), sem nenhuma camada de serviço intermediária — não existe
  um `JornadaTrabalhoService` nessa versão. Em `arquiteturalimpa`, o mesmo
  controller depende de um `JornadaTrabalhoService` dedicado, que por sua vez
  depende do *port* `JornadaTrabalhoRepository` do domínio. Esse achado
  reforça, com evidência de código executável (e não apenas inspeção visual),
  a inconsistência de camadas já esperada na arquitetura em camadas quando
  comparada à separação estrita de responsabilidades da Clean Architecture.

## 4. Discussão

### 4.1 Pontos Positivos Identificados

**Arquitetura em Camadas:**
- Menor quantidade de arquivos e de indireções para entender o fluxo completo
  de uma requisição (controller → service → repository → entidade), o que
  reduziu a curva de aprendizado inicial durante o desenvolvimento.
- Suficiente para o escopo do sistema desenvolvido, sem custo adicional de
  mapeamento entre modelos.

**Clean Architecture:**
- Isolamento efetivo das regras de negócio em relação a frameworks: as classes
  de `domain` e `application` não apresentaram nenhuma dependência de Spring,
  JPA ou Spring Security, confirmado pela inspeção de importações.
- Testes unitários das camadas de negócio dependeram exclusivamente de
  abstrações (*ports*) definidas no próprio domínio, reduzindo o acoplamento
  do teste a detalhes de infraestrutura, como demonstrado no caso do
  `AuthService`.
- Maior facilidade, evidenciada na organização por pacotes, para localizar e
  isolar o impacto de uma eventual troca de tecnologia de persistência ou de
  autenticação, uma vez que essas preocupações ficaram confinadas à camada
  `infrastructure`.
- Tratamento de erro mais consistente na camada de apresentação: o
  `GlobalExceptionHandler` central traduziu exceções de domínio em códigos
  HTTP semanticamente corretos (401 para falha de autenticação, 404 para
  entidade não encontrada) com corpo de resposta informativo, ao passo que a
  versão em camadas dependeu de blocos `try/catch` espalhados pelos
  controllers, um dos quais (`AuthController`) descartava a causa real do
  erro e retornava sempre 400 com corpo vazio (Seção 3.3).
- Maior consistência entre camadas: todos os controllers dependeram de uma
  classe de caso de uso (`Service`) dedicada, incluindo jornadas de trabalho —
  cenário em que a versão em camadas acessou o repositório diretamente a
  partir do controller, sem camada de serviço (Seção 3.3).

### 4.2 Limitações Identificadas

- A Clean Architecture exigiu **49% mais arquivos** para entregar o mesmo
  conjunto de funcionalidades, incluindo a manutenção de duas representações
  de cada entidade (modelo de domínio e entidade JPA) e suas respectivas
  classes de mapeamento, o que aumentou o esforço de escrita e manutenção de
  código repetitivo (*boilerplate*).
- O ganho de desacoplamento não foi mensurado por meio de métricas
  formais de qualidade de software (por exemplo, *coupling between objects*,
  *afferent/efferent coupling* ou complexidade ciclomática calculadas por
  ferramenta de análise estática), tendo sido avaliado apenas de forma
  qualitativa, por inspeção manual do código-fonte.
- Mesmo após a ampliação da suíte (Seção 2.3), que elevou a cobertura de 9
  para 30 testes por projeto ao incluir a camada de controller, os testes de
  fatia web (`@WebMvcTest`) foram executados com o filtro JWT e a
  configuração de segurança excluídos da fatia (`addFilters = false`); logo, o
  comportamento de autenticação/autorização em si (validação de token,
  `@PreAuthorize`) permanece sem cobertura automatizada. Os adaptadores de
  persistência (`*RepositoryAdapter`/`*JpaRepository`) também continuam sem
  testes de integração com banco de dados, pois isso exigiria um banco H2 ou
  Testcontainers compatível com as migrations Flyway (atualmente escritas
  para PostgreSQL), o que não foi configurado neste trabalho. A conclusão
  sobre testabilidade, portanto, ainda se limita às camadas efetivamente
  testadas (serviço/caso de uso e controller), não podendo ser generalizada
  para a totalidade da base de código.
- A diferença de tempo de inicialização do contexto Spring observada entre os
  dois projetos (0,001 s contra 6,083 s) foi obtida em uma única execução de
  cada suíte, sem repetições nem controle de variáveis externas (estado do
  sistema operacional, cache de disco, *garbage collection*), não devendo ser
  interpretada como medida confiável de desempenho de inicialização.
- O estudo comparou apenas dois projetos derivados do mesmo domínio de
  problema (controle de ponto eletrônico) e desenvolvidos pelo mesmo autor,
  fator que pode ter introduzido viés de familiaridade progressiva com o
  domínio ao construir a segunda versão (`arquiteturalimpa`), possivelmente
  influenciando a qualidade e a organização do código independentemente do
  estilo arquitetural adotado.

### 4.3 Síntese

Os resultados indicaram que a Clean Architecture cumpriu seu objetivo de
desacoplar regras de negócio de detalhes de infraestrutura, evidenciado tanto
pela ausência de dependências de framework nas camadas `domain` e
`application` quanto pela menor exposição dos testes unitários a classes de
infraestrutura. Esse benefício, entretanto, teve como contrapartida um
aumento mensurável na quantidade de arquivos e de camadas de indireção
necessárias para implementar a mesma funcionalidade, o que caracteriza um
compromisso (*trade-off*) entre desacoplamento e simplicidade estrutural,
consistente com o que é descrito na literatura consultada sobre os dois
estilos arquiteturais.
