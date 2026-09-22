# Especificação de Requisitos — Limpeza de Estofados

## 1. Visão geral

Site institucional de uma empresa de limpeza de estofados. Apresenta o catálogo
de serviços oferecidos, uma galeria de fotos antes/depois dos trabalhos realizados,
e uma calculadora de estimativa de preço. O contato para fechar negócio acontece
por WhatsApp, diretamente com o responsável.

> **Mudança de escopo:** a ideia original previa um canal formal adicional —
> formulário de solicitação de orçamento (nome/telefone/cidade), com persistência
> e notificação por e-mail ao responsável (RF04–RF06). O time decidiu descontinuar
> esse fluxo e seguir só com a calculadora (RF08) + WhatsApp (RF03) como único
> canal de contato. RF04–RF06 permanecem documentados abaixo por rastreabilidade,
> marcados como descontinuados; o backend que os implementa
> (`SolicitacaoController`/`SolicitacaoService`, `POST /api/v1/quotes`) continua
> no código mas não é mais chamado pelo frontend. Ver `changelog.md`.

Existe um único canal de contato com o visitante:

- **WhatsApp** (RF03) — canal informal, direto, sem registro no sistema. A
  calculadora (RF08) ajuda o visitante a se preparar para essa conversa com uma
  estimativa de preço, mas não gera nenhum registro nem envia dado nenhum ao
  responsável.

## 2. Escopo

### Estrutura de seções do front-end (3 a 5, conforme diretriz do projeto)

1. **Cabeçalho** — identidade da empresa + link de contato via WhatsApp (RF03)
2. **Catálogo de serviços** — conteúdo principal da página inicial (RF01, RF02)
3. **Galeria antes/depois** — fotos dos trabalhos realizados (RF07)
4. **Calculadora de orçamento** — estimativa de preço por serviço + modelo/tamanho (RF08). Não captura lead — RF04 (formulário de solicitação) foi descontinuado.
5. **Rodapé/contato** — informações complementares

### Dentro do escopo (nesta fase)

- Exibição do catálogo de serviços na página inicial
- Galeria de fotos "antes e depois" dos serviços realizados
- Link de contato direto via WhatsApp
- Calculadora de estimativa de preço por serviço + modelo/tamanho (RF08)
- Layout responsivo (desktop, tablet, smartphone), com CSS puro (sem framework tipo Bootstrap)

### Descontinuado (não implementar)

- Formulário de solicitação de orçamento (nome/telefone/cidade/consentimento LGPD)
- Persistência das solicitações recebidas via formulário
- Notificação por e-mail ao responsável a cada nova solicitação

O backend desse fluxo (RF04–RF06) continua implementado (`SolicitacaoController`,
`SolicitacaoService`, `POST /api/v1/quotes`), mas não deve ser removido nem
consumido pelo frontend — ver nota de mudança de escopo na seção 1.

## 3. Requisitos Funcionais

RF04, RF05 e RF06 estão marcados **[Descontinuado]** — ver nota de mudança de
escopo na seção 1. Mantidos na tabela por rastreabilidade (RN01/RN03 e o
backend ainda os referenciam), mas não fazem mais parte do protótipo atual.

| ID | Descrição | Critério de aceite |
|----|-----------|---------------------|
| RF01 | O sistema deve permitir que o visitante visualize o catálogo de serviços ativos na página inicial. | A página inicial lista nome e descrição de todos os serviços com status ativo cadastrados no banco. |
| RF02 | O sistema deve exibir, para cada serviço do catálogo, nome e descrição. | Cada item do catálogo exibido contém os dois campos preenchidos, sem campos em branco. |
| RF03 | O sistema deve exibir um link de contato via WhatsApp que direciona ao responsável por agendamentos. | Ao clicar no link, o WhatsApp abre uma conversa com o número configurado, em nova aba/janela. |
| RF04 **[Descontinuado]** | O sistema deve permitir que o visitante envie uma solicitação de orçamento informando nome, telefone, cidade e tipo de serviço. | O formulário só é submetido com sucesso quando os 4 campos são preenchidos com valores válidos; tipo de serviço deve corresponder a um serviço existente no catálogo. |
| RF05 **[Descontinuado]** | O sistema deve persistir toda solicitação de orçamento recebida com sucesso. | Após o envio, a solicitação é encontrada no banco de dados com todos os campos enviados e data/hora de criação. |
| RF06 **[Descontinuado]** | O sistema deve notificar o responsável por e-mail sempre que uma nova solicitação de orçamento for registrada. | Um e-mail é recebido pelo endereço configurado, contendo os dados da solicitação, em até alguns minutos após o envio do formulário. |
| RF07 | O sistema deve exibir uma galeria de fotos "antes e depois" dos serviços já realizados. | A página inicial exibe pelo menos um par de imagens (antes/depois) por tipo de serviço, ou uma galeria geral, dependendo do que o responsável fornecer como material. |
| RF08 | O sistema deve permitir que o visitante calcule uma estimativa de preço, informando o serviço e o modelo/tamanho do item. | Ao selecionar serviço + modelo (e metros lineares, quando aplicável), o sistema retorna um valor estimado, obtido de uma tabela de preços cadastrada. |

## 4. Requisitos Não Funcionais

| ID | Categoria | Descrição |
|----|-----------|-----------|
| RNF01 | Desempenho | A listagem do catálogo deve responder em até 300ms em condições normais de uso. |
| RNF02 **[Descontinuado]** | Segurança | O formulário de orçamento, por ser público e sem autenticação, deve ter proteção básica contra abuso automatizado (honeypot). Sem objeto desde que RF04–RF06 foram descontinuados. |
| RNF03 | Confiabilidade | O envio da notificação por e-mail é síncrono, protegido por `try/catch`: uma falha no envio é registrada em log, mas não impede que a solicitação já persistida seja considerada válida. Só se aplica se o endpoint `POST /api/v1/quotes` (RF04–RF06, descontinuado) for chamado diretamente. |
| RNF04 **[Descontinuado]** | Conformidade legal | O formulário deve obter consentimento explícito (checkbox) para o tratamento dos dados pessoais informados, em conformidade com a LGPD. Sem objeto desde que RF04–RF06 foram descontinuados. |
| RNF05 | Portabilidade | A aplicação (backend e banco) deve poder ser executada via Docker Compose, sem exigir configuração manual do ambiente. |
| RNF06 **[Descontinuado]** | Usabilidade | O formulário de orçamento deve ser utilizável em dispositivos móveis. Sem objeto desde que RF04–RF06 foram descontinuados. |
| RNF07 | Responsividade | O layout deve se adaptar a desktop e smartphone, usando CSS puro (sem framework). **No protótipo atual, o breakpoint de tablet é dispensado** — suficiente para a demonstração. Testável via ferramenta de inspeção de dispositivo do navegador (F12). |

## 5. Regras de Negócio

| ID | Regra | Relacionada a (RF) |
|----|-------|----------------------|
| RN01 | Uma solicitação de orçamento só é válida se referenciar um serviço existente e ativo no catálogo. Só se aplica ao endpoint `POST /api/v1/quotes` (RF04–RF05, descontinuado no frontend, ainda existente no backend). | RF04, RF05 |
| RN02 | Um serviço não pode ser excluído se já possuir solicitações de orçamento associadas; ele deve ser apenas desativado (`ativo = false`). | RF01 |
| RN03 | Serviços inativos não aparecem no catálogo público (RF01) nem como opção selecionável na calculadora de orçamento (RF08). A parte referente ao formulário de solicitação (RF04) está sem objeto — RF04 foi descontinuado. | RF01, RF08 |
| RN04 | O preço final do serviço é sempre confirmado manualmente pelo responsável após contato com o cliente (via WhatsApp, RF03); a calculadora (RF08) fornece apenas uma estimativa inicial, não vinculante. | RF03, RF08 |
| RN05 | O preço estimado pela calculadora é obtido de uma tabela de preços cadastrada (serviço + modelo); se a unidade de cobrança for "por metro", o preço é multiplicado pelos metros lineares informados. | RF08 |
| RN06 | Se o visitante solicitar impermeabilização na estimativa, o valor calculado é dobrado. | RF08 |

## 6. Glossário

- **Serviço**: item do catálogo oferecido pela empresa, com nome, descrição e status ativo/inativo.
- **Solicitação de orçamento**: registro criado quando um visitante preenche o formulário de solicitação (RF04–RF06, **descontinuado** — não existe mais no frontend, backend mantido sem uso).
- **Preço de orçamento (tabela de preços)**: valor de referência cadastrado por combinação de serviço + modelo/tamanho, usado pela calculadora (RF08).
- **Responsável**: pessoa que recebe a notificação de nova solicitação e faz o contato manual com o cliente.

## 7. Perguntas em aberto

- Definir se haverá painel administrativo em uma fase futura.
- Definir a origem das fotos da galeria (RF07): arquivos estáticos ou upload/armazenamento externo.
- Definir quem/como a tabela de preços (RN05) será mantida atualizada — cadastro manual no banco, ou uma tela futura de gestão.

As perguntas sobre texto de consentimento LGPD e e-mail(is) de notificação
deixaram de se aplicar com a descontinuação do formulário de solicitação
(RF04–RF06) — ver nota de mudança de escopo na seção 1.