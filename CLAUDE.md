# CLAUDE.md

Guia de contexto. Para detalhes completos, ver os documentos linkados em cada seção.

## O que é este projeto

Site institucional de uma empresa de limpeza de estofados (Limp Service), com
catálogo de serviços e calculadora de estimativa de orçamento. O contato para
fechar negócio acontece via WhatsApp. Detalhes completos em [`doc-técnica/Spec.md`](doc-técnica/Spec.md).

> O formulário de solicitação de orçamento (nome/telefone/cidade, RF04–RF06) foi
> **descontinuado** — o backend que o implementa (`SolicitacaoController`/
> `SolicitacaoService`) continua no código, mas não é mais chamado pelo frontend.
> Ver nota de mudança de escopo em `doc-técnica/Spec.md`.

## Stack

- Backend: Java 21 + Spring Boot 4.1.1 + Maven
- Banco: PostgreSQL, gerenciado via Flyway (migrations `V1`, `V2`, `V3`)
- Frontend: HTML/CSS/JS puro (sem framework), pasta `Frontend/`, separado do backend
- Containerização: Docker + Docker Compose

## Como rodar

```bash
docker compose up --build
```

Sobe backend + banco juntos. A API responde em `http://localhost:8080`.
Requer um arquivo `.env` na raiz — usar `.env.example` como modelo, incluindo
`FRONTEND_URL` (origem do frontend, usada pelo CORS) e as variáveis de SMTP.

O `Frontend/` não sobe junto — precisa ser servido à parte (Live Server,
`Frontend/serve.sh`/`serve.bat`, ou `npx serve`). Abrir `index.html` direto
(`file://`) quebra as chamadas `fetch()` por CORS. Ver README para detalhes.

## Convenções

- Commits seguem Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`).
- Cada tarefa/commit relevante referencia o RF correspondente em `doc-técnica/Spec.md` (ex: "RF08").
- Segredos (senhas, chaves) nunca são commitados — sempre via `.env` (fora do Git).
- Organização por domínio, não por tipo técnico: `catalog/`, `quote/`, `calculator/`, `config/` — cada um com sua própria entidade, repository, service e controller.

## Decisões importantes que não são óbvias só olhando o código

- **Migrations (Flyway)**: `V1` cria as tabelas (`servicos`, `solicitacao_orcamento`); `V2` normaliza/popula o catálogo de serviços; `V3` cria `precos_orcamento` e `configuracoes_empresa`, populando os preços da calculadora. `ddl-auto=update` continua ativo (não `validate`) — isso é uma sobreposição intencional/tolerada por ora: o Flyway cria o schema real, o Hibernate só ajusta detalhes menores por cima. Pode gerar avisos inofensivos no log (tipo "constraint ... does not exist, skipping") — não é erro.
- **Nunca usar H2 no `application.properties` principal.** Já aconteceu de alguém sobrescrever a configuração do Postgres com H2 em memória para rodar testes locais, quebrando toda a integração com Docker. Se precisar de H2 para testes automatizados, isso vai num profile separado (`application-test.properties`), nunca no arquivo principal.
- **Envio de e-mail é síncrono** (não assíncrono): decisão consciente para reduzir complexidade, dado o volume baixo de uso esperado. Ver justificativa completa em [`doc-técnica/Arquitetura.md`](doc-técnica/Arquitetura.md).
- **Calculadora de orçamento (RF08)**: lê preço de `precos_orcamento` via `PrecoOrcamentoRepository` — nunca usar valor fixo no Java. A estimativa não vincula o preço final (RN04).
- **Sem autenticação** nesta fase: todos os endpoints da API são públicos. Por isso não existe (propositalmente) nenhum `GET` público que liste todas as solicitações — isso exporia dado pessoal de clientes.
- **CORS**: origem lida de `FRONTEND_URL` no `.env`, com fallback pra `localhost`/`127.0.0.1` em desenvolvimento. Nunca usar `allowedOriginPatterns("*")`.
- **Variáveis de ambiente**: no `.env`, definem-se `POSTGRES_DB`/`POSTGRES_USER`/`POSTGRES_PASSWORD` (usadas pelo serviço `db` do `docker-compose.yml`, e repassadas ao `app` como `DB_NAME`/`DB_USER`/`DB_PASSWORD` — `DB_HOST`/`DB_PORT` são fixos no compose, apontando pro serviço `db`), além de `FRONTEND_URL` e `SMTP_HOST`/`SMTP_PORT`/`SMTP_USER`/`SMTP_PASSWORD`, repassadas diretamente ao `app`. Todas são lidas pelo `application.properties` (e `CorsConfig`, no caso de `FRONTEND_URL`) com fallback só para desenvolvimento local fora do Docker.

## Onde encontrar mais

- Requisitos funcionais, não funcionais e regras de negócio: [`doc-técnica/Spec.md`](doc-técnica/Spec.md)
- Arquitetura, Docker e produção: [`doc-técnica/Arquitetura.md`](doc-técnica/Arquitetura.md)
- Contrato da API: [`doc-técnica/api-contract.md`](doc-técnica/api-contract.md)
- Histórico de mudanças: [`doc-técnica/changelog.md`](doc-técnica/changelog.md)