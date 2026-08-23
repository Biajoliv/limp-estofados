# CLAUDE.md

Guia de contexto. Para detalhes completos, ver os documentos linkados em cada seção.

## O que é este projeto

Site institucional de uma empresa de limpeza de estofados (MEI), com catálogo
de serviços e formulário de solicitação de orçamento. Detalhes completos em
[`docs/spec.md`](docs/spec.md).

## Stack

- Backend: Java 21 + Spring Boot 4.1.1 + Maven
- Banco: PostgreSQL, gerenciado via Flyway (migrations)
- Frontend: HTML/CSS/JS puro (sem framework), separado do backend
- Containerização: Docker + Docker Compose

## Como rodar

```bash
docker compose up --build
```

Sobe backend + banco juntos. A API responde em `http://localhost:8080`.
Requer um arquivo `.env` na raiz — usar `.env.example` como modelo.

## Convenções

- Commits seguem Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`) — ver [`CONTRIBUTING.md`](CONTRIBUTING.md).
- Cada tarefa/commit relevante referencia o RF correspondente em `docs/spec.md` (ex: "RF04").
- Segredos (senhas, chaves) nunca são commitados — sempre via `.env` (fora do Git).

## Decisões importantes que não são óbvias só olhando o código

- **`spring.jpa.hibernate.ddl-auto=validate`**: o Hibernate nunca cria/altera tabelas sozinho. Quem faz isso é o Flyway, via migrations versionadas. Sem migration aplicada, a aplicação falha ao subir.
- **Envio de e-mail é síncrono** (não assíncrono): decisão consciente para reduzir complexidade, dado o volume baixo de uso (~20 acessos/dia). Ver justificativa completa em [`docs/architecture.md`](docs/architecture.md), seção 2.
- **Sem autenticação** nesta fase: todos os endpoints da API são públicos.
- **Variáveis de ambiente do banco**: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` — usadas tanto pelo `application.properties` quanto pelo `docker-compose.yml`. Ao alterar uma, alterar nos dois lugares (ou manter via `.env`, que alimenta ambos).

## Onde encontrar mais

- Requisitos funcionais, não funcionais e regras de negócio: [`docs/spec.md`](docs/spec.md)
- Arquitetura, Docker e produção: [`docs/architecture.md`](docs/architecture.md)
- Contrato da API: [`docs/api-contract.md`](docs/api-contract.md)
- Histórico de mudanças: [`CHANGELOG.md`](CHANGELOG.md)