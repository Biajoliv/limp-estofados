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
- `Frontend/serve.sh` e `Frontend/serve.bat`: scripts para servir `Frontend/` via `python -m http.server` na porta 5500, evitando o erro de CORS ao abrir `index.html` por `file://`

### Corrigido
- `SolicitacaoService` associava um `Servico` "vazio" (só com id) à solicitação salva — corrigido para usar o resultado do `findById`
- **Bug crítico**: `application.properties` teve configuração de banco H2 em memória adicionada por engano, sobrescrevendo a conexão real com Postgres — removida; conexão com Postgres via Docker restaurada
- **Segurança**: removido endpoint `GET /api/v1/quotes` que expunha publicamente dados pessoais (nome, telefone, cidade) de todos os clientes, sem autenticação
- **Regra de negócio**: removida lógica que criava automaticamente um "Serviço Teste" quando o `servicoId` informado não existia — RN01 restaurada (rejeita solicitação com serviço inválido)
- Contrato da calculadora alinhado entre frontend e backend: frontend agora envia `servicoId` (numérico, resolvido via `mapaServicos`) e `modelo`, batendo com o que `CalculoRequest` espera (antes o frontend enviava `servicoAlvo`/`tamanhoOuModelo`, incompatível)
- Organização de pastas: duplicidade `frontend/` e `Frontend/` consolidada em uma única pasta (`Frontend/`); arquivos da calculadora movidos do pacote `quote/` (local errado) para `calculator/`
- **Bug crítico**: modelo do Sofá na calculadora convertia o valor selecionado de ponto para vírgula (ex: `"2.20-2.50"` → `"2,20-2,50"`) antes de enviar ao backend, mas `precos_orcamento` armazena os modelos com ponto — toda tentativa de calcular Sofá retornava erro 500. Conversão removida, valor enviado agora bate com a migration.
- Links de documentação quebrados em `CLAUDE.md` e `README.md` (apontavam para `docs/*.md` e `CHANGELOG.md`, que não existem) corrigidos para os caminhos reais em `doc-técnica/`; removidos os links para `GETTING_STARTED.md` e `CONTRIBUTING.md`, que não existem e não têm equivalente

### Removido
- Script solto `database/limpeza_estofados.sql` (continha `DROP DATABASE IF EXISTS`, além de duplicar o que as migrations do Flyway já fazem, com `servico_id` fixo em vez de resolvido por nome) — Flyway é a única fonte de verdade para schema agora

### Alterado
- `Dockerfile`: multi-stage (builda dentro do próprio container, sem exigir `mvn package` manual)
- `docker-compose.yml`: `depends_on` do `app` usa `condition: service_healthy`, com healthcheck no serviço `db`
- `CorsConfig`: origem lida de `FRONTEND_URL` (variável de ambiente), com fallback para `localhost`/`127.0.0.1` em dev — antes usava `allowedOriginPatterns("*")`, inseguro
- `.env.example`: adicionadas `FRONTEND_URL` e `SMTP_HOST`/`SMTP_PORT`/`SMTP_USER`/`SMTP_PASSWORD`, que `application.properties` já lia mas não estavam documentadas — quem clonasse o repo do zero não saberia que existiam
- `docker-compose.yml`: bloco `environment` do serviço `app` agora repassa `FRONTEND_URL` e as 4 variáveis de SMTP — antes só repassava as de banco, então mesmo preenchendo o `.env` corretamente elas nunca chegavam na aplicação
- `README.md`: adicionada seção explicando como servir `Frontend/` localmente (Live Server, `Frontend/serve.sh`/`serve.bat`, ou `npx serve`) e por que abrir `index.html` via `file://` quebra as chamadas `fetch()` por CORS
- `README.md`: seção "Status do projeto" atualizada de "Em planejamento" para "Protótipo funcional"

### Decisões registradas
- Calculadora de orçamento (RF08) confirmada em escopo, modelo "tabelado" via `precos_orcamento`, não fórmula por dimensão real
- Envio de e-mail: síncrono (decisão final, após idas e vindas) — **sem efeito prático hoje**, ver descontinuação do formulário abaixo
- Formato de erro: RFC 7807 (Problem Details) — **ainda não implementado no código** (ver Pendente)
- Proteção do formulário via honeypot — **ainda não implementado no código** (ver Pendente); também sem objeto, ver descontinuação abaixo
- Sem autenticação nesta fase
- Deploy sob demanda via AWS EC2 (instância única, ligada apenas para demonstrações)
- **Formulário de solicitação de orçamento (RF04–RF06) descontinuado**: o time decidiu seguir só com a calculadora (RF08) + WhatsApp (RF03) como canal de contato com o visitante. O backend que implementa esse fluxo (`SolicitacaoController`, `SolicitacaoService`, entidade `Solicitacao`, endpoint `POST /api/v1/quotes`) continua no código por ora, mas não é mais chamado pelo frontend — não deletar sem decisão explícita do time. Documentação atualizada em `Spec.md`, `Arquitetura.md` e `api-contract.md`.

### Adicionado
- Sincronização Dinâmica do Frontend: O index.html agora realiza chamadas a /api/v1/services no carregamento da página e mapeia dinamicamente os IDs reais das migrations V1/V2 às opções do select.
- Fluxo em 2 Etapas: Separado o cálculo em tempo real da gravação final do cliente no banco.
- Validação de Formulário: Adicionada verificação no frontend exigindo preenchimento de Nome e Telefone antes de persistir no PostgreSQL.
- Tratamento de Exceções SMTP: Ajustado application.properties para suportar conexões locais sem autenticação obrigatória quando as variáveis SMTP_USER não estiverem preenchidas.

### Corrigido
- Bug Erro 400 em Calculadora: Resolvida a divergência de IDs hardcoded no frontend em relação às chaves primárias geradas pelo Flyway nas migrations V2 e V3.
- Prevenção de Exposição de Dados: Removido o número da empresa do placeholder de telefone do cliente e eliminados fallbacks que inseriam o número institucional na tabela de solicitações.
- Redirecionamento WhatsApp Assíncrono: Ajustado envio do POST /api/v1/quotes garantindo a gravação do registro antes da abertura da nova guia para o WhatsApp.

### Fora do escopo do protótipo atual
Rodando localmente via WSL/Docker, sem previsão de subir em produção agora — os
itens abaixo não são necessários enquanto isso for verdade, e podem ser
retomados quando o projeto for além do protótipo:
- Formato de erro RFC 7807 (`@RestControllerAdvice`) — erros usam o formato padrão do Spring por enquanto
- Campo honeypot no formulário — sem tráfego público real, sem risco de spam
- Breakpoint de tablet no CSS — desktop e mobile cobrem a demonstração
- Configuração e deploy da instância EC2