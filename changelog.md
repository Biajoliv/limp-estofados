# Changelog

Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

## [Não lançado]

### Adicionado
- Estrutura inicial do projeto Spring Boot (Maven, Java 21, Spring Boot 4.1.1)
- Configuração de `application.properties` via variáveis de ambiente (banco, JPA, SMTP)
- `docker-compose.yml` orquestrando backend + PostgreSQL, com `.env`/`.env.example`
- Documentação inicial: `README.md`, `CONTRIBUTING.md`, `docs/spec.md`, `docs/architecture.md`, `docs/api-contract.md`
- Entidades JPA `Servico` (domínio `catalog`) e `Solicitacao` (domínio `quote`), com relacionamento `@ManyToOne`
- `ServicoRepository`, `SolicitacaoRepository`, `ServicoService`, `SolicitacaoService`
- Endpoints `GET /api/v1/services` e `POST /api/v1/quotes`, com DTO `SolicitacaoRequest`
- Validação da regra RN01 (serviço deve existir e estar ativo) no `SolicitacaoService`
- Envio de e-mail de notificação (síncrono, protegido por `try/catch`)
- Dependências do Flyway (`flyway-core`, `flyway-database-postgresql`) e migration inicial `V1__create_initial_tables.sql`
- `springdoc-openapi` (Swagger UI) para explorar e testar a API em `/swagger-ui.html`
- `CorsConfig`, liberando chamadas de `localhost`/`127.0.0.1` para desenvolvimento
- `teste-manual/teste-api.html`: página HTML solta para testar os endpoints visualmente, fora da estrutura do backend
- Testado com sucesso via Swagger e via `teste-api.html`: listagem de catálogo e criação de solicitação de ponta a ponta

### Corrigido
- `SolicitacaoService` associava um `Servico` "vazio" (só com id) à solicitação salva, em vez do serviço completo já validado — corrigido para usar o resultado do `findById`

### Alterado
- `Dockerfile`: revertido de single-stage para multi-stage (o single-stage causou builds desatualizados silenciosos mais de uma vez na prática)
- `docker-compose.yml`: `depends_on` do `app` passou a usar `condition: service_healthy`, com healthcheck no serviço `db`, em vez de depender só do `restart: on-failure`

### Decisões registradas
- Envio de e-mail de notificação definido como síncrono (não assíncrono), dado o volume baixo de uso esperado
- Proteção do formulário público via honeypot, sem rate limiting dedicado (honeypot em si ainda não implementado no código)
- Sem autenticação nesta fase
- Deploy sob demanda via AWS EC2 (instância única, ligada apenas para demonstrações) — criação da instância em andamento

### Pendente
- Validar o Flyway funcionando de ponta a ponta dentro do Docker, e reverter `ddl-auto` de `update` para `validate`
- Testar envio de e-mail com credenciais SMTP reais (Gmail) — falha de autenticação confirmada com credenciais vazias, como esperado
- Campo honeypot ainda não implementado no `SolicitacaoRequest`/Controller
- Bean Validation nos campos obrigatórios do `SolicitacaoRequest`
- `consentimentoLgpd` chega na API mas não é persistido nem validado
- Endpoint para cadastro de serviço (`POST /api/v1/services`) — hoje inserido manualmente via SQL
- Finalizar criação e configuração da instância EC2
- Frontend real (ainda não iniciado — `teste-manual/` é só ferramenta de teste)