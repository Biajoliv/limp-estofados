# CLAUDE.md

Guia de contexto. Para detalhes completos, ver os documentos linkados em cada seção.

## O que é este projeto

Site institucional de uma empresa de limpeza de estofados (Limp Service), com
catálogo de serviços, calculadora de estimativa de orçamento e formulário de
solicitação de orçamento. Detalhes completos em [`docs/spec.md`](docs/spec.md).

## Stack

- Backend: Java 21 + Spring Boot 4.1.1 + Maven
- Banco: PostgreSQL, gerenciado via Flyway (migrations `V1`, `V2`, `V3`)
- Frontend: HTML/CSS/JS puro (sem framework), pasta `frontend/`, separado do backend
- Containerização: Docker + Docker Compose

## Como rodar

```bash
docker compose up --build
```

Sobe backend + banco juntos. A API responde em `http://localhost:8080`.
Requer um arquivo `.env` na raiz — usar `.env.example` como modelo, incluindo
`FRONTEND_URL` (origem do frontend, usada pelo CORS).

## Convenções

- Commits seguem Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`) — ver [`CONTRIBUTING.md`](CONTRIBUTING.md).
- Cada tarefa/commit relevante referencia o RF correspondente em `docs/spec.md` (ex: "RF08").
- Segredos (senhas, chaves) nunca são commitados — sempre via `.env` (fora do Git).
- Organização por domínio, não por tipo técnico: `catalog/`, `quote/`, `calculator/`, `config/` — cada um com sua própria entidade, repository, service e controller.

## Decisões importantes que não são óbvias só olhando o código

- **Migrations (Flyway)**: `V1` cria as tabelas (`servicos`, `solicitacao_orcamento`); `V2` normaliza/popula o catálogo de serviços; `V3` cria `precos_orcamento` e `configuracoes_empresa`, populando os preços da calculadora. `ddl-auto=update` continua ativo (não `validate`) — isso é uma sobreposição intencional/tolerada por ora: o Flyway cria o schema real, o Hibernate só ajusta detalhes menores por cima. Pode gerar avisos inofensivos no log (tipo "constraint ... does not exist, skipping") — não é erro.
- **Nunca usar H2 no `application.properties` principal.** Já aconteceu de alguém sobrescrever a configuração do Postgres com H2 em memória para rodar testes locais, quebrando toda a integração com Docker. Se precisar de H2 para testes automatizados, isso vai num profile separado (`application-test.properties`), nunca no arquivo principal.
- **Envio de e-mail é síncrono** (não assíncrono): decisão consciente para reduzir complexidade, dado o volume baixo de uso esperado. Ver justificativa completa em [`docs/architecture.md`](docs/architecture.md).
- **Calculadora de orçamento (RF08)**: lê preço de `precos_orcamento` via `PrecoOrcamentoRepository` — nunca usar valor fixo no Java. A estimativa não vincula o preço final (RN04).
- **Sem autenticação** nesta fase: todos os endpoints da API são públicos. Por isso não existe (propositalmente) nenhum `GET` público que liste todas as solicitações — isso exporia dado pessoal de clientes.
- **CORS**: origem lida de `FRONTEND_URL` no `.env`, com fallback pra `localhost`/`127.0.0.1` em desenvolvimento. Nunca usar `allowedOriginPatterns("*")`.
- **Variáveis de ambiente do banco**: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` — usadas tanto pelo `application.properties` quanto pelo `docker-compose.yml`.

## Onde encontrar mais

- Requisitos funcionais, não funcionais e regras de negócio: [`docs/spec.md`](docs/spec.md)
- Arquitetura, Docker e produção: [`docs/architecture.md`](docs/architecture.md)
- Contrato da API: [`docs/api-contract.md`](docs/api-contract.md)
- Histórico de mudanças: [`CHANGELOG.md`](CHANGELOG.md)