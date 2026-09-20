# Desenho Arquitetural — Limpeza de Estofados

## 1. Componentes

```
[Frontend estático: HTML/CSS/JS] --fetch()/JSON--> [Backend: Spring Boot API REST]
                                                       (Spring Data JPA / Hibernate)
                                                              |
                                                              | JDBC
                                                              v
                                                        [PostgreSQL]
                                                              |
                                                              +--> [E-mail via SMTP, síncrono]
```

### RF → componente responsável

| RF | Descrição | Componente(s) |
|---|---|---|
| RF01 | Listar catálogo de serviços | Backend (`GET /services`) + Banco |
| RF02 | Exibir nome/descrição do serviço | Frontend |
| RF03 | Link de contato via WhatsApp | Frontend (estático) |
| RF04 | Enviar solicitação de orçamento | Frontend + Backend + Banco |
| RF05 | Persistir a solicitação | Backend + Banco |
| RF06 | Notificar responsável por e-mail | Backend + SMTP |
| RF07 | Galeria antes/depois | Frontend (imagens estáticas) |
| RF08 | Calculadora de estimativa de preço | Frontend + Backend (`POST /quotes/calcular`) + Banco (tabela de preços) |

## 2. Decisões-chave

| Decisão | Por quê |
|---|---|
| Frontend e backend desacoplados (API REST, sem SSR) | Um contrato de API bem definido (`api-contract.md`) permite que frontend e backend evoluam de forma independente. |
| Catálogo (`Servico`) em banco, não hardcoded | Há chance real de mudar a lista de serviços; evita precisar de deploy para isso. |
| Sem autenticação nesta fase | Público-alvo é o visitante geral, sem necessidade de conta. |
| E-mail de notificação **síncrono** | Volume baixo (~20 acessos/dia) não justifica a complexidade de `@Async`/evento. Protegido por `try/catch`: falha no envio não derruba a solicitação já persistida. |
| Honeypot em vez de rate limiting | Volume baixo torna rate limiting dedicado desnecessário. |
| Formato de erro **RFC 7807 (Problem Details)** | Padrão HTTP formal para erros, via `@RestControllerAdvice` + `ProblemDetail`. |
| `Dockerfile` multi-stage | Builda dentro do próprio container; evita artefato desatualizado por esquecimento de `mvn package` manual. |
| `docker-compose`: healthcheck no banco + `depends_on: condition: service_healthy` | Evita o container da aplicação crashar na primeira tentativa de conexão com o banco. |
| Calculadora de orçamento (RF08) **confirmada em escopo**, com tabela de preços no banco | Modelo "tabelado" (não fórmula por dimensão real) — mais simples de manter, atualizável sem deploy. Ver seção 4 (modelo de dados) e RN05/RN06. |

## 3. Fluxo crítico: solicitação de orçamento

1. `POST /api/v1/quotes` com nome, telefone, cidade, serviço e campo honeypot.
2. Honeypot preenchido → descarta silenciosamente.
3. Valida formato (Bean Validation) e RN01 (serviço deve existir e estar ativo).
4. Persiste a solicitação.
5. Envia e-mail de notificação, dentro de `try/catch` — falha é logada, não impede a resposta.
6. Responde ao visitante.

## 3.1 Fluxo: calculadora de orçamento (RF08)

1. `POST /api/v1/quotes/calcular` com `servicoId`, `modelo` (e `metrosLineares`/`incluirImpermeabilizacao`, quando aplicável).
2. Backend busca o preço em `precos_orcamento` pela combinação `servico_id` + `modelo`.
3. Se a unidade for `por_metro`, multiplica pelo valor de `metrosLineares`.
4. Se `incluirImpermeabilizacao = true`, dobra o valor.
5. Retorna o valor estimado — **não** persiste nada (é só simulação, RN04).

## 4. Modelo de dados

- **Servico**: `id`, `nome`, `descricao`, `ativo`
- **SolicitacaoOrcamento**: `id`, `nome`, `telefone`, `cidade`, `servico_id` (FK), `criado_em`
- **PrecoOrcamento** (novo, suporta RF08): `id`, `servico_id` (FK), `modelo`, `preco_base`, `unidade` (`fixo` | `por_metro`), `ativo`. Restrição de unicidade em (`servico_id`, `modelo`).

## 5. Integração Frontend ↔ Backend

Contrato completo em [`api-contract.md`](api-contract.md). Prefixo `/api/v1`; erro padronizado
RFC 7807; CORS configurado no backend — origem deve vir de variável de ambiente
(`FRONTEND_URL`), não hardcoded, para funcionar tanto em desenvolvimento local
quanto quando o frontend for hospedado externamente.

## 6. Docker

Backend + PostgreSQL via `docker-compose`, credenciais via `.env`. `app` usa
`depends_on: condition: service_healthy` no banco. Dockerfile multi-stage.

## 7. Produção

AWS EC2 sob demanda — ligada só para apresentações ao cliente. Instância única
(t3.micro), VPC padrão, sem NAT Gateway, sem RDS (Postgres em container na mesma
instância). Security Group liberando só as portas necessárias.

## 8. Riscos

- Envio de e-mail síncrono adiciona latência; aceitável no volume atual.
- EC2 única ligada/desligada manualmente não serve para disponibilidade contínua.
- Tabela `precos_orcamento` mantida manualmente (sem painel administrativo) — atualizar preços exige acesso direto ao banco.
- Estimativa da calculadora (RF08) pode divergir do preço final negociado — reforçar isso na UI para não gerar expectativa equivocada no cliente (RN04).