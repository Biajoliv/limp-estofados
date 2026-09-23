# CLAUDE.md

Guia de contexto e padrões do projeto. Para detalhes completos, ver os documentos em doc-técnica/.

## O que é este projeto

Site institucional de uma empresa de limpeza de estofados (Limp Service), com catálogo de serviços, calculadora de estimativa de orçamento e formulário de solicitação/agendamento persistido no banco de dados com redirecionamento direto para o WhatsApp. Detalhes completos em doc-técnica/Spec.md.

## Stack Técnica

- Backend: Java 21 + Spring Boot 4.1.1 + Maven
- Banco de Dados: PostgreSQL 16, gerenciado via Flyway (migrations V1, V2, V3)
- Frontend: HTML/CSS/JS puro (sem framework), pasta Frontend/, separado do backend
- Containerização: Docker + Docker Compose

## Como rodar

docker compose up --build

Sobe backend + banco juntos. A API responde em http://localhost:8080.
Requer um arquivo .env na raiz — usar .env.example como modelo, incluindo FRONTEND_URL (origem do frontend, usada pelo CORS).

O Frontend/ não sobe junto com o compose principal — precisa ser servido à parte (Live Server, Frontend/serve.sh, ou python3 -m http.server 5500 na pasta Frontend/). Abrir index.html direto por arquivo (file://) quebra as chamadas fetch() por CORS. Ver README.md para detalhes.

## Convenções

- Commits seguem Conventional Commits (feat:, fix:, docs:, chore:).
- Cada tarefa/commit relevante referencia o RF correspondente em doc-técnica/Spec.md (ex: "RF08").
- Segredos (senhas, chaves) nunca são commitados — sempre via .env (fora do Git).
- Organização por domínio, não por tipo técnico: catalog/, quote/, calculator/, config/ — cada um com sua própria entidade, repository, service e controller.

## Decisões importantes que não são óbvias só olhando o código

- Migrations (Flyway): V1 cria as tabelas (servicos, solicitacao_orcamento); V2 normaliza e popula o catálogo de serviços; V3 cria precos_orcamento e configuracoes_empresa, populando os preços da calculadora. ddl-auto=update continua ativo (não validate) — sobreposição intencional/tolerada: o Flyway cria o schema real, o Hibernate ajusta detalhes menores por cima.
- Nunca usar H2 no application.properties principal. A configuração de banco é exclusivamente PostgreSQL via Docker. Se precisar de H2 para testes automatizados, vai num profile separado (application-test.properties).
- Mapeamento Dinâmico no Frontend: O select do formulário consulta /api/v1/services no carregamento e associa os IDs reais gerados pelo Flyway, impedindo erros HTTP 400 por divergência de chave primária.
- Calculadora de orçamento (RF08): lê preço de precos_orcamento via PrecoOrcamentoRepository — nunca usar valor fixo no Java. A estimativa não vincula o preço final (RN04).
- Persistência Dupla Operacional: A etapa de cálculo executa a consulta de estimativa em memória via POST /api/v1/quotes/calcular sem salvar nada. A confirmação grava a solicitação no banco via POST /api/v1/quotes (tabela solicitacao_orcamento) e redireciona o cliente para o WhatsApp com os dados parametrizados.
- Sem autenticação nesta fase: todos os endpoints da API são públicos. Por isso não existe (propositalmente) nenhum GET público que liste todas as solicitações recebidas — evitaria expor dados pessoais de clientes.
- CORS: origem lida de FRONTEND_URL no .env, com fallback para localhost/127.0.0.1 em desenvolvimento (porta padrão 5500). Nunca usar allowedOriginPatterns("*").
- Deploy em Produção: Projetado para execução isolada e resiliente em nuvem via AWS EC2 (instância Ubuntu/Amazon Linux rodando Docker Compose + Nginx como Proxy Reverso com SSL Let's Encrypt nas portas 80/443).

## Onde encontrar mais

- Requisitos funcionais, não funcionais e regras de negócio: doc-técnica/Spec.md
- Arquitetura, Docker e infraestrutura EC2: doc-técnica/Arquitetura.md
- Contrato da API: doc-técnica/api-contract.md
- Histórico de mudanças: doc-técnica/changelog.md