# Metodologia

## 1. Caracterização da Pesquisa

Esta pesquisa classificou-se, quanto à natureza, como aplicada, pois teve como
objetivo gerar conhecimento para aplicação prática na construção de sistemas de
software. Quanto à abordagem, adotou-se um método misto: qualitativo, na
descrição e comparação das estruturas de código e das decisões arquiteturais, e
quantitativo, na coleta de métricas objetivas (quantidade de arquivos, classes,
pacotes e testes) extraídas dos dois sistemas desenvolvidos.

Quanto aos objetivos, a pesquisa caracterizou-se como exploratória e
comparativa, uma vez que não buscou testar hipóteses estatísticas, mas
investigar, descrever e contrastar duas abordagens arquiteturais aplicadas a um
mesmo domínio de problema. Quanto ao procedimento técnico, utilizou-se o
delineamento de **estudo de caso comparativo**, em que dois artefatos de
software funcionalmente equivalentes foram construídos, executados e analisados
lado a lado.

## 2. Delineamento do Estudo

O estudo comparou duas implementações de um mesmo sistema de controle de ponto
eletrônico, ambas desenvolvidas com as mesmas tecnologias de base (Java 17,
Spring Boot 3.2.0, Maven, PostgreSQL, Flyway, Spring Security e JWT), a fim de
isolar a variável de interesse — o estilo arquitetural — e reduzir a influência
de fatores externos (linguagem, framework, banco de dados, mecanismo de
autenticação) sobre a comparação.

Os dois artefatos analisados foram:

- **`mvcpontoeletronico`**: implementação seguindo a **Arquitetura em Camadas
  (MVC tradicional)**, com pacotes organizados por papel técnico
  (`entities`, `dtos`, `controllers`, `services`, `repositories`, `security`,
  `config`, `exception`).
- **`arquiteturalimpa`**: reimplementação funcionalmente equivalente do mesmo
  domínio seguindo os princípios da **Clean Architecture**, com separação em
  camadas concêntricas (`domain`, `application`, `infrastructure`,
  `interfaces`), inversão de dependência via portas e adaptadores, e modelos de
  domínio ricos, desacoplados de anotações de framework.

Ambos os sistemas expõem a mesma API REST (mesmos endpoints, contratos de
entrada e saída) e compartilham o mesmo schema de banco de dados (mesmas
migrations Flyway), garantindo equivalência funcional entre as duas versões e
permitindo que a comparação recaia exclusivamente sobre a organização interna
do código.

## 3. Etapas de Desenvolvimento

O desenvolvimento ocorreu em quatro etapas sequenciais:

1. **Implementação da versão em Arquitetura em Camadas** (`mvcpontoeletronico`):
   construção do sistema seguindo o padrão MVC tradicional do Spring Boot, com
   entidades JPA anêmicas, controllers finos, services concentrando regras de
   negócio e repositories estendendo `JpaRepository`.
2. **Transcrição para Clean Architecture** (`arquiteturalimpa`): reescrita do
   mesmo conjunto de funcionalidades separando regras de negócio (camadas
   `domain` e `application`) dos detalhes técnicos (camada `infrastructure`),
   introduzindo interfaces de repositório (*ports*) no domínio e adaptadores de
   persistência (mapeando entidades JPA para modelos de domínio) na
   infraestrutura.
3. **Desenvolvimento do frontend** (React + TypeScript + Vite), consumido por
   ambas as APIs de forma intercambiável, utilizado como cliente para validação
   funcional manual das duas implementações.
4. **Coleta de métricas e testes automatizados** em ambos os projetos, sob as
   mesmas condições de execução (mesma máquina, mesma versão de JDK e Maven),
   para subsidiar a análise comparativa.

## 4. Coleta de Dados

A coleta de dados foi realizada por meio de:

- **Análise estática da estrutura de diretórios e pacotes** de cada projeto,
  contabilizando o número de arquivos-fonte Java, classes e pacotes por
  camada, obtidos por varredura do sistema de arquivos de cada repositório.
- **Execução da suíte de testes automatizados** de cada projeto por meio do
  comando `mvn test` (Maven Surefire Plugin), com os resultados (quantidade de
  testes executados, falhas, erros e tempo de execução) registrados nos
  relatórios gerados automaticamente em `target/surefire-reports/`.
- **Inspeção do código-fonte** das camadas equivalentes nos dois projetos
  (por exemplo, `AuthService`, `PontoEletronicoService`,
  `SolicitacaoService`, `UsuarioService`), para identificar diferenças de
  acoplamento, responsabilidade e dependência de frameworks.
- **Registro documental** das decisões de projeto tomadas durante a
  transcrição arquitetural (por exemplo, substituição de dependências diretas
  do Spring Security por abstrações de domínio), utilizado como fonte
  qualitativa complementar às métricas quantitativas.

## 5. Análise dos Dados

Os dados coletados foram analisados por meio de estatística descritiva simples
(contagens absolutas e comparação direta), organizados em quadros
comparativos contrastando, para cada projeto:

- Número de classes/arquivos por camada arquitetural;
- Quantidade de testes unitários e resultado de execução (aprovados,
  falhos, tempo de execução);
- Grau de acoplamento entre camadas, verificado pela presença ou ausência de
  dependência direta de classes de regra de negócio em relação a anotações e
  APIs do framework Spring.

A análise qualitativa consistiu na comparação estrutural das duas arquiteturas
a partir dos princípios de design de software (separação de responsabilidades,
inversão de dependência e testabilidade), confrontando a organização por papel
técnico da Arquitetura em Camadas com a organização por regra de negócio da
Clean Architecture, tomando como referência a literatura consolidada sobre os
dois estilos arquiteturais.

## 6. Ambiente e Ferramentas

O desenvolvimento e a coleta de dados foram realizados no seguinte ambiente:

| Item | Especificação |
|---|---|
| Linguagem | Java 17 |
| Framework backend | Spring Boot 3.2.0 |
| Gerenciador de dependências/build | Apache Maven |
| Banco de dados | PostgreSQL |
| Controle de versão de schema | Flyway |
| Autenticação | Spring Security + JSON Web Token (JJWT 0.9.1) |
| Framework de testes | JUnit 5 + Spring Boot Test |
| Frontend | React, TypeScript, Vite |
| Sistema operacional | Windows |
| Editor/IDE | Visual Studio Code |

## 7. Limitações do Método

Reconhece-se que o estudo de caso comparativo, por não empregar amostragem
estatística nem replicação em múltiplos domínios de problema, não permitiu
generalizações estatísticas para todo e qualquer sistema de software. Os
resultados obtidos restringiram-se ao contexto do domínio de controle de ponto
eletrônico e às decisões de implementação adotadas neste trabalho,
constituindo evidência qualitativa e quantitativa aplicável a sistemas de
escopo e complexidade semelhantes.
