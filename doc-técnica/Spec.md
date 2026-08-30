# Especificação de Requisitos — Limpeza de Estofados

## 1. Visão geral

Site institucional de uma empresa de limpeza de estofados. Apresenta o catálogo
de serviços oferecidos, uma galeria de fotos antes/depois dos trabalhos realizados,
e permite que o visitante solicite um orçamento informando dados de contato.
A solicitação gera notificação por e-mail ao responsável, que faz o contato e a
negociação final manualmente, via WhatsApp.

Existem exatamente dois canais de contato com o visitante, sem sobreposição:

- **Solicitação de orçamento via formulário** (RF04–RF06) — canal formal, gera
  registro persistido e notificação por e-mail. Recomendado para quem quer um
  histórico do pedido.
- **WhatsApp** (RF03) — canal informal, direto, sem registro no sistema. Para
  quem só quer tirar dúvidas rapidamente.

## 2. Escopo

### Estrutura de seções do front-end (3 a 5, conforme diretriz do projeto)

1. **Cabeçalho** — identidade da empresa + link de contato via WhatsApp (RF03)
2. **Catálogo de serviços** — conteúdo principal da página inicial (RF01, RF02)
3. **Galeria antes/depois** — fotos dos trabalhos realizados (RF07)
4. **Formulário de orçamento** — captura de lead (RF04)
5. **Rodapé/contato** — informações complementares

### Dentro do escopo (nesta fase)

- Exibição do catálogo de serviços na página inicial
- Galeria de fotos "antes e depois" dos serviços realizados
- Link de contato direto via WhatsApp
- Formulário de solicitação de orçamento
- Persistência das solicitações recebidas
- Notificação por e-mail ao responsável a cada nova solicitação
- Layout responsivo (desktop, tablet, smartphone), com CSS puro (sem framework tipo Bootstrap)

## 3. Requisitos Funcionais

| ID | Descrição | Critério de aceite |
|----|-----------|---------------------|
| RF01 | O sistema deve permitir que o visitante visualize o catálogo de serviços ativos na página inicial. | A página inicial lista nome e descrição de todos os serviços com status ativo cadastrados no banco. |
| RF02 | O sistema deve exibir, para cada serviço do catálogo, nome e descrição. | Cada item do catálogo exibido contém os dois campos preenchidos, sem campos em branco. |
| RF03 | O sistema deve exibir um link de contato via WhatsApp que direciona ao responsável por agendamentos. | Ao clicar no link, o WhatsApp abre uma conversa com o número configurado, em nova aba/janela. |
| RF04 | O sistema deve permitir que o visitante envie uma solicitação de orçamento informando nome, telefone, cidade e tipo de serviço. | O formulário só é submetido com sucesso quando os 4 campos são preenchidos com valores válidos; tipo de serviço deve corresponder a um serviço existente no catálogo. |
| RF05 | O sistema deve persistir toda solicitação de orçamento recebida com sucesso. | Após o envio, a solicitação é encontrada no banco de dados com todos os campos enviados e data/hora de criação. |
| RF06 | O sistema deve notificar o responsável por e-mail sempre que uma nova solicitação de orçamento for registrada. | Um e-mail é recebido pelo endereço configurado, contendo os dados da solicitação, em até alguns minutos após o envio do formulário. |
| RF07 | O sistema deve exibir uma galeria de fotos "antes e depois" dos serviços já realizados. | A página inicial exibe pelo menos um par de imagens (antes/depois) por tipo de serviço, ou uma galeria geral, dependendo do que o responsável fornecer como material. |

## 4. Requisitos Não Funcionais

| ID | Categoria | Descrição |
|----|-----------|-----------|
| RNF01 | Desempenho | A listagem do catálogo deve responder em até 300ms em condições normais de uso (volume de dados pequeno, sem necessidade de cache nesta fase). |
| RNF02 | Segurança | O formulário de orçamento, por ser público e sem autenticação, deve ter proteção básica contra abuso automatizado. Dado o volume baixo esperado (~20 acessos/dia), um campo honeypot é suficiente nesta fase — sem necessidade de infraestrutura de rate limiting dedicada. |
| RNF03 | Confiabilidade | O envio da notificação por e-mail é síncrono (decisão consciente, ver `architecture.md` seção 2). Uma falha no envio é registrada em log, mas não pode impedir que a solicitação já persistida seja considerada válida — o lead não pode se perder mesmo que a notificação falhe. |
| RNF04 | Conformidade legal | O formulário deve obter consentimento explícito (checkbox) para o tratamento dos dados pessoais informados, em conformidade com a LGPD. |
| RNF05 | Portabilidade | A aplicação (backend e banco) deve poder ser executada via Docker Compose, sem exigir configuração manual do ambiente por quem for rodá-la. |
| RNF06 | Usabilidade | O formulário de orçamento deve ser utilizável em dispositivos móveis, dado que é o canal mais provável de acesso (ex: vindo de redes sociais). |
| RNF07 | Responsividade | O layout deve se adaptar corretamente a três larguras de referência: desktop, tablet e smartphone, usando CSS puro (sem framework como Bootstrap). Testável via ferramenta de inspeção de dispositivo do navegador (F12). |

## 5. Regras de Negócio

| ID | Regra | Relacionada a (RF) |
|----|-------|----------------------|
| RN01 | Uma solicitação de orçamento só é válida se referenciar um serviço existente no catálogo. | RF04, RF05 |
| RN02 | Um serviço não pode ser excluído se já possuir solicitações de orçamento associadas; ele deve ser apenas desativado (`ativo = false`), para preservar o histórico. | RF01 |
| RN03 | Serviços inativos não aparecem no catálogo público (RF01) nem como opção selecionável no formulário de orçamento (RF04). | RF01, RF04 |
| RN04 | Nesta fase, o preço do serviço não é calculado pelo sistema — é definido manualmente pelo responsável após contato direto com o cliente. (Sujeito a mudar se a calculadora de orçamento, ainda em definição, for implementada.) | RF04 |

## 6. Glossário

- **Serviço**: item do catálogo oferecido pela empresa (ex: limpeza de sofá, limpeza de colchão), com nome, descrição e status ativo/inativo.
- **Solicitação de orçamento**: registro criado quando um visitante preenche o formulário, contendo seus dados de contato e o serviço de interesse.
- **Responsável**: pessoa que recebe a notificação de nova solicitação e faz o contato manual com o cliente via WhatsApp.
- **Ativo/Inativo**: status de um serviço que controla se ele aparece publicamente no catálogo e no formulário, sem apagar seu histórico.

## 7. Perguntas em aberto

- Definir o texto exato de consentimento LGPD a ser exibido no formulário.
- Definir se haverá painel administrativo em uma fase futura (impacta se vale já modelar um campo de status na solicitação, mesmo sem usá-lo agora).
- Definir o(s) e-mail(is) que receberão a notificação.
- Definir a origem das fotos da galeria (RF07): arquivos estáticos servidos pelo frontend, ou upload gerenciado via backend/armazenamento externo (ex: S3)? Isso afeta se é implementação simples (imagens fixas no repositório) ou exige nova infraestrutura.
- Definir se vamos implementar solicitação de orçamento com cálculo de preço baseado em dimensões do móvel/estofado e quantidade de lugares — possível melhoria futura (calculadora de orçamento), estrutura ainda em definição.