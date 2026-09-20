# Especificação de Requisitos — Limpeza de Estofados

## 1. Visão geral

Site institucional de uma empresa de limpeza de estofados. Apresenta o catálogo
de serviços oferecidos, uma galeria de fotos antes/depois dos trabalhos realizados,
uma calculadora de estimativa de preço, e permite que o visitante solicite um
orçamento informando dados de contato. A solicitação gera notificação por e-mail
ao responsável, que faz o contato e a negociação final manualmente.

Existem exatamente dois canais de contato com o visitante, sem sobreposição:

- **Solicitação de orçamento via formulário** (RF04–RF06) — canal formal, gera
  registro persistido e notificação por e-mail.
- **WhatsApp** (RF03) — canal informal, direto, sem registro no sistema.

## 2. Escopo

### Estrutura de seções do front-end (3 a 5, conforme diretriz do projeto)

1. **Cabeçalho** — identidade da empresa + link de contato via WhatsApp (RF03)
2. **Catálogo de serviços** — conteúdo principal da página inicial (RF01, RF02)
3. **Galeria antes/depois** — fotos dos trabalhos realizados (RF07)
4. **Formulário de orçamento** — captura de lead, com calculadora de estimativa embutida (RF04, RF08)
5. **Rodapé/contato** — informações complementares

### Dentro do escopo (nesta fase)

- Exibição do catálogo de serviços na página inicial
- Galeria de fotos "antes e depois" dos serviços realizados
- Link de contato direto via WhatsApp
- Calculadora de estimativa de preço por serviço + modelo/tamanho (RF08)
- Formulário de solicitação de orçamento
- Persistência das solicitações recebidas
- Notificação por e-mail ao responsável a cada nova solicitação (síncrono)
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
| RF08 | O sistema deve permitir que o visitante calcule uma estimativa de preço, informando o serviço e o modelo/tamanho do item. | Ao selecionar serviço + modelo (e metros lineares, quando aplicável), o sistema retorna um valor estimado, obtido de uma tabela de preços cadastrada. |

## 4. Requisitos Não Funcionais

| ID | Categoria | Descrição |
|----|-----------|-----------|
| RNF01 | Desempenho | A listagem do catálogo deve responder em até 300ms em condições normais de uso. |
| RNF02 | Segurança | O formulário de orçamento, por ser público e sem autenticação, deve ter proteção básica contra abuso automatizado (honeypot). **Dispensável no protótipo atual**, rodando local sem tráfego público real. |
| RNF03 | Confiabilidade | O envio da notificação por e-mail é síncrono, protegido por `try/catch`: uma falha no envio é registrada em log, mas não impede que a solicitação já persistida seja considerada válida. |
| RNF04 | Conformidade legal | O formulário deve obter consentimento explícito (checkbox) para o tratamento dos dados pessoais informados, em conformidade com a LGPD. |
| RNF05 | Portabilidade | A aplicação (backend e banco) deve poder ser executada via Docker Compose, sem exigir configuração manual do ambiente. |
| RNF06 | Usabilidade | O formulário de orçamento deve ser utilizável em dispositivos móveis. |
| RNF07 | Responsividade | O layout deve se adaptar a desktop e smartphone, usando CSS puro (sem framework). **No protótipo atual, o breakpoint de tablet é dispensado** — suficiente para a demonstração. Testável via ferramenta de inspeção de dispositivo do navegador (F12). |

## 5. Regras de Negócio

| ID | Regra | Relacionada a (RF) |
|----|-------|----------------------|
| RN01 | Uma solicitação de orçamento só é válida se referenciar um serviço existente e ativo no catálogo. | RF04, RF05 |
| RN02 | Um serviço não pode ser excluído se já possuir solicitações de orçamento associadas; ele deve ser apenas desativado (`ativo = false`). | RF01 |
| RN03 | Serviços inativos não aparecem no catálogo público (RF01) nem como opção selecionável no formulário de orçamento (RF04). | RF01, RF04 |
| RN04 | O preço final do serviço é sempre confirmado manualmente pelo responsável após contato com o cliente; a calculadora (RF08) fornece apenas uma estimativa inicial, não vinculante. | RF04, RF08 |
| RN05 | O preço estimado pela calculadora é obtido de uma tabela de preços cadastrada (serviço + modelo); se a unidade de cobrança for "por metro", o preço é multiplicado pelos metros lineares informados. | RF08 |
| RN06 | Se o visitante solicitar impermeabilização na estimativa, o valor calculado é dobrado. | RF08 |

## 6. Glossário

- **Serviço**: item do catálogo oferecido pela empresa, com nome, descrição e status ativo/inativo.
- **Solicitação de orçamento**: registro criado quando um visitante preenche o formulário.
- **Preço de orçamento (tabela de preços)**: valor de referência cadastrado por combinação de serviço + modelo/tamanho, usado pela calculadora (RF08).
- **Responsável**: pessoa que recebe a notificação de nova solicitação e faz o contato manual com o cliente.

## 7. Perguntas em aberto

- Definir o texto exato de consentimento LGPD a ser exibido no formulário.
- Definir se haverá painel administrativo em uma fase futura.
- Definir o(s) e-mail(is) que receberão a notificação.
- Definir a origem das fotos da galeria (RF07): arquivos estáticos ou upload/armazenamento externo.
- Definir quem/como a tabela de preços (RN05) será mantida atualizada — cadastro manual no banco, ou uma tela futura de gestão.