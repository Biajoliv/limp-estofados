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

| RF | Descrição - Componente(s) |
|---|---|---|
| RF01 | Listar catálogo de serviços | Backend (`GET /services`) + Banco |
| RF02 | Exibir nome/descrição do serviço | Frontend (renderiza o que o Backend retorna) |
| RF03 | Link de contato via WhatsApp | Frontend (link estático, sem envolver o Backend) |

| RF04 | Enviar solicitação de orçamento | Frontend (formulário) + Backend (valida/persiste) + Banco |
| RF05 | Persistir a solicitação | Backend + Banco |
| RF06 | Notificar responsável por e-mail | Backend + SMTP |


## 2. Decisões-chave

| Decisão | Por quê |
|---|---|
| Frontend e backend desacoplados (API REST, sem SSR) | um contrato de API bem definido (`api-contract.md`) permite que quem cuidar do frontend trabalhe sem depender de Java. |
| Catálogo (`Servico`) em banco, não hardcoded | Há chance real de mudar a lista de serviços; evita precisar de deploy para isso. |
| Sem autenticação nesta fase | Público-alvo é o visitante geral, sem necessidade de conta. |
| E-mail de notificação **síncrono** (não assíncrono) | Volume baixo (~20 acessos/dia) não justifica a complexidade de evento/listener. Protegido por `try/catch`: falha no envio não derruba a solicitação já persistida. |
| Honeypot em vez de rate limiting | Volume baixo torna rate limiting dedicado desnecessário; honeypot já cobre abuso automatizado simples. |
| `Dockerfile` **single-stage** (não multi-stage) | Simplicidade para o time: exige rodar `mvn package` manualmente antes do `docker build`. **Trade-off aceito conscientemente** — ver nota no RNF05 (seção 7). Multi-stage fica como melhoria futura. |

## 3. Fluxo crítico: solicitação de orçamento

1. `POST /api/v1/quotes` com nome, telefone, cidade, serviço e campo honeypot.
2. Honeypot preenchido → descarta silenciosamente (resposta de sucesso falsa).
3. Valida formato (Bean Validation) e RN01 (serviço deve existir e estar ativo).
4. Persiste a solicitação.
5. Envia e-mail de notificação, dentro de `try/catch` — falha é logada, não impede a resposta.
6. Responde ao visitante.

## 4. Modelo de dados

- **Servico**: `id`, `nome`, `descricao`, `ativo`
- **SolicitacaoOrcamento**: `id`, `nome`, `telefone`, `cidade`, `servico_id` (FK), `criado_em`

## 5. Integração Frontend ↔ Backend

Contrato completo em [`api-contract.md`](api-contract.md). Prefixo `/api/v1`; erro padronizado
`{ "codigo", "mensagem", "campo" }`; CORS configurado explicitamente no backend para a origem
do frontend. Alternativa a manter manualmente: `springdoc-openapi` (gera Swagger a partir do código).

## 6. Docker

Backend + PostgreSQL via `docker-compose`, credenciais via `.env` (fora do Git, ver `.env.example`).
`app` usa `restart: on-failure` para lidar com o banco ainda não estar pronto no primeiro start
(alternativa mais simples a um healthcheck). Ver `docker-compose.yml` e `backend/Dockerfile` no
repositório — são a fonte de verdade, não reproduzidos aqui para evitar desatualização.

## 7. Produção

**Decisão atual:** AWS EC2 sob demanda — ligada só para apresentações ao cliente, parada
(stop, não terminate) no resto do tempo. Prioriza custo mínimo e aprendizado; revisar se o uso
deixar de ser esporádico.

- Instância única (ex: t3.micro) rodando o `docker-compose` completo — sem RDS separado.
- VPC padrão, subnet pública, IP direto na instância — **sem NAT Gateway** (custo fixo desnecessário aqui).
- Security Group liberando só as portas necessárias (80/443, e 22 restrito ao IP do administrador).
- Frontend pode ser servido pela mesma instância (Nginx) para simplificar o "ligar tudo para a demo".

**Evolução futura:** se o uso deixar de ser esporádico, revisar para plataforma com disponibilidade
contínua (ex: Neon + Render, ou RDS/ALB na própria AWS).

## 8. RNF → como é atendido

| RNF | Como é atendido |
|---|---|
| RNF01 (desempenho) | Volume de dados pequeno; sem otimização necessária. |
| RNF02 (segurança) | Honeypot no `POST /api/v1/quotes`. |
| RNF03 (confiabilidade) | E-mail síncrono protegido por `try/catch` (seção 2–3). |
| RNF04 (LGPD) | Checkbox de consentimento no formulário + campo no payload. |
| RNF05 (portabilidade) | `docker-compose.yml` versionado — **porém** exige `mvn package` manual antes do build, já que o Dockerfile é single-stage (seção 2). Revisar se isso incomodar no dia a dia do time. |
| RNF06 (usabilidade mobile) | Responsabilidade do frontend. |

## 9. Riscos

- E-mail síncrono adiciona latência; aceitável no volume atual, revisar se crescer.
- EC2 única ligada/desligada manualmente não serve para disponibilidade contínua.
- Sem backup automatizado do banco — considerar `pg_dump` periódico se dados reais se acumularem.
- Dockerfile single-stage exige passo manual (`mvn package`) — se isso gerar atrito real no time, migrar para multi-stage.