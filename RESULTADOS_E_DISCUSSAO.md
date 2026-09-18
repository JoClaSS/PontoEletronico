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
`mvcpontoeletronico` contra 0,001 s no `arquiteturalimpa`. Investigação do
código-fonte dos dois testes (Seção 2.4) revelou a causa real dessa diferença:
não se trata de uma propriedade da arquitetura, e sim de uma **assimetria na
configuração dos testes** — o teste de contexto do `mvcpontoeletronico` usa
`@SpringBootTest` (sobe o contexto Spring completo, conectando a um banco
PostgreSQL real via `.env`), enquanto o do `arquiteturalimpa` foi
deliberadamente implementado **sem** `@SpringBootTest` (comentário no código
explica que subir o contexto completo exigiria um Postgres real acessível).
Ou seja, o teste de contexto do `arquiteturalimpa` não valida efetivamente a
inicialização do Spring; os 0,001 s medidos refletem a execução de um método
de teste vazio, não a inicialização de uma aplicação Spring Boot. Essa
diferença de configuração é tratada como limitação metodológica na Seção 4.2.

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

### 2.4 Métricas Formais de Cobertura de Código (JaCoCo)

Para complementar a contagem de testes com uma métrica objetiva de qualidade,
o plugin `jacoco-maven-plugin` (versão 0.8.12) foi adicionado ao `pom.xml` de
ambos os projetos, com execução do *goal* `prepare-agent` (instrumentação em
tempo de execução) seguida de `report` na fase `test`. Os relatórios foram
gerados em `target/site/jacoco/` (HTML e CSV) a partir da mesma execução de
`mvn test` usada na Seção 2.3, garantindo que a métrica de cobertura reflita
exatamente a suíte de 30 testes por projeto.

**Cobertura agregada (todas as classes de `src/main/java`):**

| Métrica | `mvcpontoeletronico` | `arquiteturalimpa` |
|---|---|---|
| Cobertura de linhas | 409/1138 (**35,9%**) | 282/992 (**28,4%**) |
| Cobertura de desvios (*branches*) | 57/339 (**16,8%**) | 55/321 (**17,1%**) |
| Cobertura de instruções | 1777/5112 (**34,8%**) | 1180/4398 (**26,8%**) |

**Cobertura de linhas por camada** (agrupamento por pacote, mesmo critério da
Seção 2.1):

| Camada | `mvcpontoeletronico` | `arquiteturalimpa` |
|---|---|---|
| Negócio/Caso de uso (`services` / `application.*`) | 237/586 (40,4%) | 176/428 (41,1%) |
| Domínio/Modelo (`entities` / `domain.*`) | 19/66 (28,8%) | 30/71 (42,3%) |
| Apresentação (`controllers` / `interfaces.web`) | 65/285 (22,8%) | 21/104 (20,2%) |
| DTO | 13/16 (81,2%) | 13/16 (81,2%) |
| Persistência (`repositories` / `infrastructure.persistence.*`) | 0/2 (0%) | 0/190 (**0%**) |
| Infraestrutura de segurança (`security` / `infrastructure.security`) | 3/68 (4,4%) | 0/67 (0%) |
| Infraestrutura de configuração (`config` / `infrastructure.config`) | 55/82 (67,1%) | 9/75 (12%) |
| Cross-cutting (`exception` / `*.exception`) | 16/30 (53,3%) | 32/38 (84,2%)* |

\* Inclui o `GlobalExceptionHandler`, exercitado indiretamente pelos testes de
controller que verificam os códigos de status 400/401/404 (Seção 3.3).

O agrupamento por camada foi obtido com um script auxiliar
(`scripts/coverage_by_layer.ps1`), criado para tornar a extração dos dados do
`jacoco.csv` reproduzível, em vez de inspecionar manualmente o relatório HTML.

**Achados principais:**

1. **A cobertura de negócio é equivalente entre as arquiteturas** (40,4% vs.
   41,1%), consistente com o fato de as duas suítes de teste de
   serviço/caso de uso terem o mesmo número de classes e de casos de teste
   (Seção 2.2). Isso reforça que a comparação de arquitetura não introduziu
   viés de esforço de teste desigual nessa camada.
2. **A camada de persistência do Clean Architecture concentra 190 linhas
   totalmente descobertas** (adaptadores, *mappers* e entidades JPA), contra
   apenas 2 linhas na versão em camadas. Esse número quantifica, com
   evidência de ferramenta, o achado já levantado na Seção 2.1 (49% mais
   arquivos): a inversão de dependência introduziu uma quantidade
   significativa de código de infraestrutura que, neste trabalho, permaneceu
   sem nenhum teste automatizado — um débito de teste concreto que a
   arquitetura em camadas simplesmente não possui, pois delega essa
   responsabilidade ao Spring Data JPA sem código intermediário próprio.
3. **A discrepância na cobertura de "Infraestrutura de configuração" (67,1%
   vs. 12%) não reflete uma diferença arquitetural**, mas sim a assimetria de
   configuração de teste descrita na Seção 2.2: o `@SpringBootTest` do
   `mvcpontoeletronico` sobe o contexto Spring completo (incluindo
   `SecurityConfig`, `WebConfig` e `AdminUserInitializer`) contra um banco
   PostgreSQL real, cobrindo essas classes como efeito colateral; o teste
   equivalente do `arquiteturalimpa` é um método vazio, sem `@SpringBootTest`.
   Essa assimetria é tratada como limitação na Seção 4.2 e deveria ser
   corrigida antes de qualquer conclusão comparativa sobre essa camada
   específica.

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
- O ganho de desacoplamento foi mensurado parcialmente por meio de métricas
  formais de cobertura de código (JaCoCo, Seção 2.4), mas outras métricas de
  qualidade estática (por exemplo, *coupling between objects*,
  *afferent/efferent coupling* ou complexidade ciclomática agregada por
  ferramenta como PMD/Checkstyle/SonarQube) ainda não foram coletadas,
  permanecendo como trabalho futuro (ver sugestões de melhoria).
- A comparação da cobertura da camada "Infraestrutura de configuração"
  (Seção 2.4) foi invalidada por uma assimetria de configuração de teste: o
  teste de contexto do `mvcpontoeletronico` usa `@SpringBootTest` (contexto
  Spring completo, conectando a um PostgreSQL real), enquanto o do
  `arquiteturalimpa` é um método vazio sem `@SpringBootTest` — diferença de
  configuração de teste entre os projetos, e não uma propriedade das
  arquiteturas comparadas. Essa assimetria também explicou, a posteriori, a
  diferença de tempo de inicialização do contexto Spring relatada na
  Seção 2.2 (6,083 s vs. 0,001 s).
- Mesmo após a ampliação da suíte (Seção 2.3), que elevou a cobertura de 9
  para 30 testes por projeto ao incluir a camada de controller, os testes de
  fatia web (`@WebMvcTest`) foram executados com o filtro JWT e a
  configuração de segurança excluídos da fatia (`addFilters = false`); logo, o
  comportamento de autenticação/autorização em si (validação de token,
  `@PreAuthorize`) permanece sem cobertura automatizada. Os adaptadores de
  persistência (`*RepositoryAdapter`/`*JpaRepository`) também continuam sem
  testes de integração com banco de dados, pois isso exigiria um banco H2 ou
  Testcontainers compatível com as migrations Flyway (atualmente escritas
  para PostgreSQL), o que não foi configurado neste trabalho. A cobertura de
  linhas medida por JaCoCo confirmou esse ponto de forma quantitativa: 0%
  (0/190 linhas) na camada de persistência do `arquiteturalimpa` — a
  conclusão sobre testabilidade, portanto, ainda se limita às camadas
  efetivamente testadas (serviço/caso de uso e controller), não podendo ser
  generalizada para a totalidade da base de código.
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
estilos arquiteturais. A métrica formal de cobertura de código (JaCoCo,
Seção 2.4) corroborou esse *trade-off* com um número concreto: a camada de
negócio possui cobertura equivalente entre as duas versões (~40%), mas a
Clean Architecture introduziu 190 linhas de código de infraestrutura
(adaptadores e *mappers*) inteiramente não testadas — um custo de manutenção
e de esforço de teste que a arquitetura em camadas não precisa pagar, por
delegar a persistência diretamente ao Spring Data JPA.

