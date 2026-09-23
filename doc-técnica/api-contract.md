# Contrato da API — Limpeza de Estofados

> Fonte de verdade sobre o que a API espera e devolve. Quem cuida do frontend
> deve conseguir integrar só lendo este documento.

## Convenções gerais

- Prefixo de versão: /api/v1
- CORS habilitado via FRONTEND_URL (variável de ambiente), com fallback para localhost/127.0.0.1 em desenvolvimento (porta padrão do frontend: 5500).
- Sem autenticação (a API é pública, não há login ou token).
- Formato de erro: padrão do Spring (JSON com timestamp, status, error, path).

## Endpoints

### GET /api/v1/services

Descrição: retorna a lista de serviços ativos cadastrados no banco de dados.

Request: sem payload.

Response — sucesso (200):
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

Regras/validações relevantes: apenas serviços com ativo = true são retornados (RN03).

---

### POST /api/v1/quotes

Descrição: registra uma nova solicitação de orçamento/agendamento no banco de dados (solicitacao_orcamento).

Request (SolicitacaoRequest):
{
  "servicoId": 1,
  "nome": "Paula Santos",
  "telefone": "3399586535",
  "cidade": "Lençóis Paulista",
  "consentimentoLgpd": true
}

Response — sucesso (201 / 200):
{
  "id": 4,
  "cidade": "Lençóis Paulista",
  "criadoEm": "2026-09-23T22:10:00.123",
  "nome": "Paula Santos",
  "telefone": "3399586535",
  "servico": {
    "id": 1,
    "nome": "Sofá",
    "descricao": "Higienização completa para todos os tipos de sofás.",
    "ativo": true
  }
}

Response — erro (400 / 500): ocorre quando o servicoId informado não existe ou não está ativo na base de dados.

Regras/validações relevantes:
- nome, telefone, cidade e servicoId são obrigatórios no formulário.
- servicoId deve corresponder ao ID primário de um serviço ativo gerado pelas migrations do Flyway (V1/V2).
- A persistência é realizada diretamente na tabela solicitacao_orcamento do PostgreSQL.

---

### POST /api/v1/quotes/calcular

Descrição: calcula a estimativa do valor com base na tabela de preços (precos_orcamento), sem persistir nada no banco.

Request (CalculoRequest):
{
  "servicoId": 1,
  "modelo": "1.80-2.00",
  "incluirImpermeabilizacao": false
}

- servicoId: ID numérico do serviço no PostgreSQL.
- modelo: string que corresponde exatamente ao modelo cadastrado em precos_orcamento (ex.: "1.80-2.00", "2.20-2.50", "solteiro", "casal", "assento", "padrao").
- incluirImpermeabilizacao: booleano (true/false). Se true, o valor base é dobrado (RN06).

Response — sucesso (200):
150.00
(Retorna um número BigDecimal/Double serializado em JSON).

Response — erro (400 Bad Request): ocorre se a combinação servicoId + modelo não estiver cadastrada na tabela precos_orcamento.