# Contrato da API — Limpeza de Estofados

> Fonte de verdade sobre o que a API espera e devolve. Quem cuida do frontend
> deve conseguir integrar só lendo este documento.
>
> Alternativa a manter isso manualmente: adicionar `springdoc-openapi` ao projeto
> Spring para gerar esta documentação automaticamente a partir do código.

## Convenções gerais

- Prefixo de versão: `/api/v1`
- CORS habilitado via `FRONTEND_URL` (variável de ambiente), com fallback para `localhost`/`127.0.0.1` em desenvolvimento
- Sem autenticação (a API é pública, não há login ou token)
- Formato de erro: padrão do Spring (JSON com `timestamp`, `status`, `error`, `path`). Um formato padronizado próprio (RFC 7807) não é necessário no protótipo — ver `CHANGELOG.md`.

## Endpoints

### GET /api/v1/services

**Descrição:** retorna a lista de serviços ativos, usada no catálogo da página inicial.

**Request:** sem payload.

**Response — sucesso (200):**
```json
[
  {
    "id": 1,
    "nome": "Sofá",
    "descricao": "Higienização completa para todos os tipos de sofás."
  },
  {
    "id": 2,
    "nome": "Colchão",
    "descricao": "Limpeza e higienização para deixar seu colchão mais limpo."
  }
]
```

**Regras/validações relevantes:** apenas serviços com `ativo = true` são retornados (RN03).

---

### POST /api/v1/quotes **[Descontinuado]**

> O time decidiu descontinuar o fluxo de solicitação de orçamento (RF04–RF06) e
> seguir só com a calculadora (`POST /quotes/calcular`, abaixo) + WhatsApp. Este
> endpoint continua implementado no backend, mas o frontend não faz mais nenhuma
> chamada para ele — documentado aqui só por completude/rastreabilidade.

**Descrição:** registra uma nova solicitação de orçamento enviada pelo visitante.

**Request:**
```json
{
  "nome": "Maria Silva",
  "telefone": "11999998888",
  "cidade": "São Paulo",
  "servicoId": 1,
  "consentimentoLgpd": true
}
```

**Response — sucesso (201):**
```json
{
  "id": 42,
  "cidade": "São Paulo",
  "criadoEm": "2026-08-24T00:59:21.370917222",
  "nome": "Maria Silva",
  "telefone": "11999998888",
  "servico": {
    "id": 1,
    "nome": "Sofá",
    "descricao": "Higienização completa para todos os tipos de sofás.",
    "ativo": true
  }
}
```

**Response — erro (500, formato padrão do Spring):** ocorre quando o serviço informado não existe ou não está mais disponível (RN01/RN03).

**Regras/validações relevantes:**
- `nome`, `telefone`, `cidade` e `servicoId` são obrigatórios (RF04) — hoje sem Bean Validation, aceitos mesmo vazios (ver `CHANGELOG.md`, Pendente).
- `servicoId` deve corresponder a um serviço existente e ativo (RN01, RN03).
- `consentimentoLgpd` chega no payload, mas ainda **não é validado nem persistido** pelo backend (RNF04, pendente).

---

### POST /api/v1/quotes/calcular

**Descrição:** calcula uma estimativa de preço (RF08), sem persistir nada — é só simulação.

**Request:**
```json
{
  "servicoId": 1,
  "modelo": "2,20-2,50",
  "metrosLineares": null,
  "incluirImpermeabilizacao": false
}
```

- `servicoId`: obrigatório, id de um serviço existente no catálogo.
- `modelo`: obrigatório, precisa corresponder a um registro cadastrado em `precos_orcamento` para esse `servicoId` (ex: `"2,20-2,50"` para sofá, `"solteiro"`/`"casal"`/`"queen"`/`"king"` para colchão, `"padrao"` para poltrona, `"assento"`/`"assento-encosto"` para cadeira, `"metro-linear"` para tapete).
- `metrosLineares`: obrigatório **apenas** quando o preço cadastrado para o modelo usa `unidade = "por_metro"` (hoje, só tapete).
- `incluirImpermeabilizacao`: opcional, `false` por padrão — se `true`, dobra o valor calculado (RN06).

**Response — sucesso (200):**
```json
180.00
```
(Retorna só o número, tipo `BigDecimal` serializado como JSON number — não um objeto.)

**Response — erro (500, formato padrão do Spring):** ocorre quando não existe preço cadastrado para a combinação `servicoId` + `modelo`, ou quando `metrosLineares` está ausente/inválido para um serviço cobrado "por_metro".

**Regras/validações relevantes:**
- RN04: o valor retornado é uma estimativa — não vincula o preço final, que é sempre confirmado manualmente pelo responsável.
- RN05: se a unidade cadastrada for `"por_metro"`, o preço base é multiplicado por `metrosLineares`.
- RN06: se `incluirImpermeabilizacao = true`, o valor é dobrado.