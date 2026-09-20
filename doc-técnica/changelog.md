# Changelog

Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

## [Não lançado]

### Adicionado
- Estrutura inicial do projeto Spring Boot (Maven, Java 21, Spring Boot 4.1.1)
- Entidades JPA `Servico` (domínio `catalog`) e `Solicitacao` (domínio `quote`), com relacionamento `@ManyToOne`
- `ServicoRepository`, `SolicitacaoRepository`, `ServicoService`, `SolicitacaoService`
- Endpoints `GET /api/v1/services` e `POST /api/v1/quotes`, com DTO `SolicitacaoRequest`
- Validação da regra RN01 (serviço deve existir e estar ativo) no `SolicitacaoService`
- Envio de e-mail de notificação (síncrono, protegido por `try/catch`)
- `springdoc-openapi` (Swagger UI) em `/swagger-ui.html`
- **Calculadora de orçamento (RF08), confirmada em escopo**: pacote `calculator/` com `PrecoOrcamento` (entidade), `PrecoOrcamentoRepository`, `CalculoRequest`, `CalculadoraService` (lê preço real da tabela `precos_orcamento`, nunca hardcoded), `CalculadoraController` (`POST /api/v1/quotes/calcular`)
- Migrations Flyway reais: `V1__create_initial_tables.sql` (tabelas base), `V2__seed_catalog_services.sql` (normaliza/popula catálogo), `V3__seed_pricing_and_company_settings.sql` (cria e popula `precos_orcamento` e `configuracoes_empresa`)
- **Frontend real implementado**: `index.html` + `style.css`, HTML/CSS/JS puro — navbar, seção inicial (hero), catálogo de serviços em cards, seção "sobre", calculadora de orçamento com campos dinâmicos por tipo de serviço, formulário de contato, rodapé, scroll suave
- Responsividade: breakpoint mobile (`max-width: 700px`) implementado

### Corrigido
- `SolicitacaoService` associava um `Servico` "vazio" (só com id) à solicitação salva — corrigido para usar o resultado do `findById`
- **Bug crítico**: `application.properties` teve configuração de banco H2 em memória adicionada por engano, sobrescrevendo a conexão real com Postgres — removida; conexão com Postgres via Docker restaurada
- **Segurança**: removido endpoint `GET /api/v1/quotes` que expunha publicamente dados pessoais (nome, telefone, cidade) de todos os clientes, sem autenticação
- **Regra de negócio**: removida lógica que criava automaticamente um "Serviço Teste" quando o `servicoId` informado não existia — RN01 restaurada (rejeita solicitação com serviço inválido)
- Contrato da calculadora alinhado entre frontend e backend: frontend agora envia `servicoId` (numérico, resolvido via `mapaServicos`) e `modelo`, batendo com o que `CalculoRequest` espera (antes o frontend enviava `servicoAlvo`/`tamanhoOuModelo`, incompatível)
- Organização de pastas: duplicidade `frontend/` e `Frontend/` consolidada em uma única pasta (`frontend/`); arquivos da calculadora movidos do pacote `quote/` (local errado) para `calculator/`

### Removido
- Script solto `database/limpeza_estofados.sql` (continha `DROP DATABASE IF EXISTS`, além de duplicar o que as migrations do Flyway já fazem, com `servico_id` fixo em vez de resolvido por nome) — Flyway é a única fonte de verdade para schema agora

### Alterado
- `Dockerfile`: multi-stage (builda dentro do próprio container, sem exigir `mvn package` manual)
- `docker-compose.yml`: `depends_on` do `app` usa `condition: service_healthy`, com healthcheck no serviço `db`
- `CorsConfig`: origem lida de `FRONTEND_URL` (variável de ambiente), com fallback para `localhost`/`127.0.0.1` em dev — antes usava `allowedOriginPatterns("*")`, inseguro

### Decisões registradas
- Calculadora de orçamento (RF08) confirmada em escopo, modelo "tabelado" via `precos_orcamento`, não fórmula por dimensão real
- Envio de e-mail: síncrono (decisão final, após idas e vindas)
- Formato de erro: RFC 7807 (Problem Details) — **ainda não implementado no código** (ver Pendente)
- Proteção do formulário via honeypot — **ainda não implementado no código** (ver Pendente)
- Sem autenticação nesta fase
- Deploy sob demanda via AWS EC2 (instância única, ligada apenas para demonstrações)

### Pendente
- Bean Validation nos campos obrigatórios do `SolicitacaoRequest`
- `consentimentoLgpd` chega na API mas não é persistido nem validado
- Endpoint para cadastro de serviço (`POST /api/v1/services`) — hoje inserido manualmente via SQL
- Entidade/endpoint para `configuracoes_empresa` (telefone, Instagram, horários) — tabela e dados existem, backend ainda não expõe isso via API para o rodapé do frontend consumir dinamicamente
- Testar envio de e-mail com credenciais SMTP reais (Gmail ou AWS SES)
- Limpar `UPDATE`s mortos em `V2__seed_catalog_services.sql` (nunca encontram linha correspondente, inofensivos mas são código morto)

### Fora do escopo do protótipo atual
Rodando localmente via WSL/Docker, sem previsão de subir em produção agora — os
itens abaixo não são necessários enquanto isso for verdade, e podem ser
retomados quando o projeto for além do protótipo:
- Formato de erro RFC 7807 (`@RestControllerAdvice`) — erros usam o formato padrão do Spring por enquanto
- Campo honeypot no formulário — sem tráfego público real, sem risco de spam
- Breakpoint de tablet no CSS — desktop e mobile cobrem a demonstração
- Configuração e deploy da instância EC2