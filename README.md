# Limpeza de Estofados — Site institucional e captação de orçamentos

Site institucional para uma empresa de limpeza de estofados. Apresenta o catálogo
de serviços oferecidos e permite que o visitante solicite um orçamento, que é
recebido pelo responsável via notificação por e-mail para contato posterior.

## Status do projeto

Em planejamento.

## Índice

- [Contexto e objetivo](#contexto-e-objetivo)
- [Documentação](#documentação)
- [Stack técnica](#stack-técnica)
- [Arquitetura em alto nível](#arquitetura-em-alto-nível)
- [Como rodar localmente](#como-rodar-localmente)
- [Como contribuir](#como-contribuir)
- [Time](#time)

## Contexto e objetivo

O objetivo é gerar leads qualificados para uma empresa de limpeza de estofados:
o visitante conhece os serviços oferecidos e solicita um orçamento informando
dados básicos de contato. Não há fechamento de negócio nem pagamento dentro do
sistema — a negociação final acontece manualmente, via WhatsApp, entre o
responsável e o cliente.

**Fora de escopo nesta fase:** autenticação de usuários, pagamento online, cálculo
automático de distância/frete a partir do CEP, painel administrativo de gestão das
solicitações, avaliações de clientes.

## Documentação

- **Novo no time / sem experiência com Docker?** [`GETTING_STARTED.md`](GETTING_STARTED.md) — guia passo a passo para instalar tudo e rodar o projeto.
- Requisitos funcionais, não funcionais e regras de negócio: [`docs/spec.md`](docs/spec.md)
- Desenho arquitetural, Docker e produção: [`docs/architecture.md`](docs/architecture.md)
- Contrato da API (integração com o frontend): [`docs/api-contract.md`](docs/api-contract.md)

## Stack técnica

- **Backend:** Java + Spring Boot (Spring Web, Spring Data JPA, Bean Validation)
- **Banco de dados:** PostgreSQL
- **Migrations:** Flyway
- **Envio de e-mail:** Spring Mail (SMTP), disparo síncrono (dentro da mesma requisição)
- **Frontend:** HTML, CSS e JavaScript puro (sem framework), consumindo a API via `fetch`
- **Containerização:** Docker + Docker Compose

## Arquitetura em alto nível

```
[Frontend estático: HTML/CSS/JS] --HTTP/JSON--> [API Backend: Spring Boot] --JDBC--> [PostgreSQL]
                                                          |
                                                          +--> [Envio síncrono de e-mail via SMTP]
```

Frontend e backend são desacoplados: o frontend consome a API REST documentada em
`docs/api-contract.md` e pode ser hospedado separadamente do backend.

## Como rodar localmente

### Pré-requisitos

- Docker e Docker Compose instalados
- JDK 17+ (apenas se for rodar o backend fora do container, direto na IDE)

### Subindo o ambiente completo via Docker

1. Clonar o repositório
2. Copiar `.env.example` para `.env` e preencher as variáveis (credenciais de banco,
   configuração SMTP)
3. Rodar `docker-compose up --build`
4. A API estará disponível em `http://localhost:8080/api/v1/...`

### Servindo o Frontend localmente

O backend já sobe com `docker-compose up --build`, mas o `Frontend/` (HTML/CSS/JS
estático) precisa ser servido por um servidor HTTP à parte — **não abra
`Frontend/index.html` por duplo clique (`file://`)**. O navegador manda a
requisição com origem `null` nesse caso, e o CORS configurado no backend
(`CorsConfig`, variável `FRONTEND_URL`) não libera `file://`, então as
chamadas `fetch()` para `/services` e `/quotes/calcular` falham silenciosamente.

Use uma destas opções, a partir da pasta `Frontend/`:

- **Live Server (VS Code):** botão direito em `index.html` → "Open with Live Server"
  (por padrão sobe em `http://localhost:5500`, que já é a origem liberada por padrão
  no `CorsConfig`).
- **Script pronto neste repositório:**
  ```bash
  # Linux/macOS/Git Bash
  ./Frontend/serve.sh

  # Windows (cmd/PowerShell)
  Frontend\serve.bat
  ```
  Ambos sobem um servidor estático na porta 5500 usando `python -m http.server` (Python 3
  precisa estar instalado). Depois é só abrir `http://localhost:5500` no navegador.
- **Alternativa sem Python:** `npx serve Frontend -l 5500`

Se usar uma porta diferente de 5500, defina `FRONTEND_URL` no `.env` com a origem
correspondente (ex: `FRONTEND_URL=http://localhost:8081`) antes de subir o backend.

### Rodando apenas o banco via Docker (para quem for mexer só no backend)

1. Rodar `docker-compose up db` para subir apenas o PostgreSQL
2. Rodar a aplicação Spring Boot direto pela IDE, apontando para o banco local do container

## Produção / Demonstrações

A aplicação roda sob demanda em uma instância AWS EC2 (ligada apenas para
apresentações ao cliente). Detalhes completos, incluindo rede, security group
e rotina de uso, estão em [`docs/architecture.md`](docs/architecture.md#7-implantação-em-produção).

## Como contribuir

Ver [`CONTRIBUTING.md`](CONTRIBUTING.md) para o fluxo de branches, commits e definição
de pronto.