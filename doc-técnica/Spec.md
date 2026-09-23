# Especificação de Requisitos — Limpeza de Estofados

## 1. Visão geral

Site institucional da Limp Service. Apresenta o catálogo de serviços, permite simular estimativas de orçamento via calculadora e oferece a gravação de solicitações de agendamento persistidas no banco de dados, com redirecionamento direto para atendimento via WhatsApp.

## 2. Escopo do Protótipo Funcional

- Catálogo de Serviços: Exibição dos itens ativos retornados da base de dados.
- Calculadora de Orçamento (RF08): Simulação do valor base com opção de impermeabilização.
- Formulário de Solicitação e Persistência (RF04, RF05): Recolha de Nome, Telefone e Cidade do visitante, com salvamento no PostgreSQL (tabela solicitacao_orcamento).
- Atendimento WhatsApp (RF03): Redirecionamento com mensagem preenchida contendo os dados da simulação.

## 3. Requisitos Funcionais

| ID | Descrição | Critério de Aceite |
|----|-----------|--------------------|
| RF01 | Visualização do catálogo de serviços | Exibe a lista de serviços ativos vindos de GET /api/v1/services. |
| RF03 | Canal WhatsApp | Botão de envio abre a conversa no WhatsApp em nova aba com o texto parametrizado. |
| RF04 | Entrada de dados do cliente | Formulário com Nome, Telefone e Cidade do solicitante. |
| RF05 | Persistência de solicitações | Gravação bem-sucedida em solicitacao_orcamento no PostgreSQL após validação. |
| RF08 | Calculadora de estimativa | Retorno do valor cadastrado em precos_orcamento ao selecionar serviço e modelo. |

## 4. Regras de Negócio

| ID | Regra |
|----|-------|
| RN01 | Toda solicitação de orçamento deve referenciar um servicoId válido e ativo no banco de dados. |
| RN03 | Serviços inativos (ativo = false) não aparecem no catálogo e não podem ser selecionados. |
| RN04 | O valor exibido pela calculadora é uma estimativa não vinculante, sujeita a confirmação presencial. |
| RN06 | Se a opção "Impermeabilizar" for marcada, o valor calculado é multiplicado por 2. |