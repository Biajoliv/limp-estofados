# Contrato da API — Limpeza de Estofados

> Fonte de verdade sobre o que a API espera e devolve. Quem cuida do frontend
> deve conseguir integrar só lendo este documento.
>
> Alternativa a manter isso manualmente: adicionar `springdoc-openapi` ao projeto
> Spring para gerar esta documentação automaticamente a partir do código.

## Convenções gerais

- Prefixo de versão: `/api/v1`
- CORS habilitado para `localhost`/`127.0.0.1` em qualquer porta (ambiente de desenvolvimento)
- Formato de erro padrão, usado em toda a API:

```json
{
  "codigo": "VALIDATION_ERROR",
  "mensagem": "Descrição legível do erro",
  "campo": "nomeDoCampoComProblema"
}
```

- Autenticação: nenhuma. Todos os endpoints abaixo são públicos nesta fase.

## Endpoints

### GET /api/v1/services

**Descrição:** retorna a lista de serviços ativos, usada no catálogo da página inicial.

**Request:** sem payload.

**Response — sucesso (200):**
```json
[
  {
    "id": 1,
    "nome": "Limpeza de sofá",
    "descricao": "Limpeza profunda a seco ou com extração, remove manchas e odores."
  },
  {
    "id": 2,
    "nome": "Limpeza de colchão",
    "descricao": "Higienização com remoção de ácaros e manchas."
  }
]
```

**Regras/validações relevantes:** apenas serviços com `ativo = true` são retornados (RN03).

---

### POST /api/v1/quotes

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
  "cidade": "Bauru",
  "criadoEm": "2026-08-24T00:59:21.370917222",
  "nome": "Maria",
  "telefone": "33991811440",
  "servico": {
    "id": 1,
    "nome": "Limpeza de sofá",
    "descricao": "Limpeza profunda a seco ou com extração",
    "ativo": true
  }
}
```

**Response — erro de validação (400):**
```json
{
  "codigo": "VALIDATION_ERROR",
  "mensagem": "Telefone é obrigatório",
  "campo": "telefone"
}
```

**Response — serviço inexistente/inativo (400):**
```json
{
  "codigo": "SERVICO_INVALIDO",
  "mensagem": "O serviço informado não existe ou não está mais disponível",
  "campo": "servicoId"
}
```

**Regras/validações relevantes:**
- `nome`, `telefone`, `cidade` e `servicoId` são obrigatórios (RF04).
- `servicoId` deve corresponder a um serviço existente (RN01); serviços inativos não são aceitos (RN03).
- `consentimentoLgpd` deve ser `true` para a solicitação ser aceita (RNF04).