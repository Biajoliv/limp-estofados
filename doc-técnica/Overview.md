# Limp Service — Visão Geral

## O que é

Site institucional para a Limp Service, empresa de limpeza de estofados. O
visitante vê o catálogo de serviços, simula uma estimativa de orçamento numa
calculadora, pode falar direto pelo WhatsApp, ou preencher um formulário para
pedir um orçamento de verdade. O responsável recebe um e-mail avisando de cada
novo pedido e faz o contato/negociação manualmente.

## O que o sistema faz (resumo)

- Mostra os serviços oferecidos (nome + descrição) na página inicial
- Calculadora de estimativa de orçamento por serviço + modelo/tamanho
- Link direto pro WhatsApp do responsável
- Formulário de orçamento: nome, telefone, cidade e tipo de serviço
- Ao enviar, salva o pedido e manda um e-mail de notificação

## Como é feito, por baixo dos panos

| Parte | Tecnologia |
|---|---|
| Backend (API) | Java 21 + Spring Boot |
| Banco de dados | PostgreSQL, com schema versionado via Flyway |
| Frontend | HTML/CSS/JS puro (sem framework) |
| Ambiente | Docker (backend + banco sobem juntos com um comando) |
| Hospedagem | AWS EC2, ligada só na hora de apresentar pro cliente |

Frontend e backend são independentes um do outro — conversam só por uma API.

## Estado atual do projeto

- ✅ Backend rodando via Docker, junto com o banco Postgres real
- ✅ Tabelas criadas e populadas via Flyway (`V1`, `V2`, `V3`)
- ✅ Endpoints de catálogo, solicitação de orçamento e calculadora funcionando
- ✅ Frontend real implementado (HTML/CSS/JS puro) e integrado com a API
- ⬜ Formato de erro (RFC 7807) e honeypot ainda não implementados no código
- ⬜ Breakpoint de tablet no CSS (só desktop/mobile hoje)
- ⬜ Instância EC2 ainda em configuração

## Onde encontrar mais detalhe

- O que o sistema deve fazer, requisito por requisito: `docs/spec.md`
- Como é montado por dentro (decisões técnicas, Docker, produção): `docs/architecture.md`
- O que a API espera/devolve: `docs/api-contract.md`
- Histórico completo de mudanças: `CHANGELOG.md`
- Como contribuir (fluxo de commits, branches): `CONTRIBUTING.md`