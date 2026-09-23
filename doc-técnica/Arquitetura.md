# Desenho Arquitetural — Limpeza de Estofados

## 1. Componentes

[Frontend estático: HTML/CSS/JS] --fetch()/JSON--> [Backend: Spring Boot API REST]
                                                       (Spring Data JPA / Hibernate)
                                                              |
                                                              | JDBC
                                                              v
                                                        [PostgreSQL]

### RF -> Componente responsável

| RF | Descrição | Componente(s) |
|---|---|---|
| RF01 | Listar catálogo de serviços | Backend (GET /api/v1/services) + Banco |
| RF02 | Exibir nome/descrição do serviço | Frontend |
| RF03 | Link de contato via WhatsApp | Frontend (com mensagem parametrizada) |
| RF04 | Enviar solicitação de orçamento | Frontend + Backend (POST /api/v1/quotes) |
| RF05 | Persistir a solicitação | Backend + Banco (solicitacao_orcamento) |
| RF07 | Galeria antes/depois | Frontend (estático) |
| RF08 | Calculadora de estimativa de preço | Frontend + Backend (POST /api/v1/quotes/calcular) + Banco (precos_orcamento) |

## 2. Decisões-chave

| Decisão | Por quê |
|---|---|
| Mapeamento Dinâmico no Frontend | As opções do formulário lêem os IDs reais de /api/v1/services no carregamento da página, evitando erros HTTP 400 por ID fixo em desconformidade com o banco. |
| Separação do Fluxo em 2 Etapas | Etapa 1 calcula e apresenta a estimativa ao visitante; Etapa 2 recolhe dados pessoais, persiste no PostgreSQL e redireciona ao WhatsApp. |
| Foco em Conversão via WhatsApp | Remoção de serviços de e-mail para simplificar a arquitetura e garantir atendimento direto e imediato via WhatsApp. |
| Containerização | Docker Compose gerencia a subida do banco PostgreSQL 16 e do container Spring Boot em rede unificada. |
| Implantação em Produção (AWS EC2) | Servidor em nuvem dedicado na AWS executando o ambiente Docker completo de forma isolada, resiliente e de baixo custo. |

## 3. Fluxo de Execução e Persistência

1. Simulação (Cálculo):
   - O visitante escolhe o serviço e o tamanho/modelo.
   - Chamada POST /api/v1/quotes/calcular.
   - O backend lê a tabela precos_orcamento e devolve a estimativa na interface (sem salvar dados).

2. Gravação e WhatsApp:
   - O visitante preenche Nome, Telefone e Cidade.
   - O frontend envia POST /api/v1/quotes gravando o registro em solicitacao_orcamento.
   - Em seguida, redireciona o utilizador para a conversa do WhatsApp com a mensagem formatada contendo o resumo da simulação.

## 4. Modelo de Dados

- servicos: id (BIGSERIAL), nome (VARCHAR), descricao (TEXT), ativo (BOOLEAN).
- solicitacao_orcamento: id (BIGSERIAL), servico_id (BIGINT, FK), nome (VARCHAR), telefone (VARCHAR), cidade (VARCHAR), criado_em (TIMESTAMP).
- precos_orcamento: id (BIGSERIAL), servico_id (BIGINT, FK), modelo (VARCHAR), preco_base (NUMERIC), unidade (VARCHAR), ativo (BOOLEAN).

## 5. Infraestrutura e Implantação em Produção (AWS EC2)

### 5.1 Especificação do Servidor (AWS EC2)
- Instância: Amazon EC2 (Ubuntu 22.04 LTS ou Amazon Linux 2023).
- Tipo recomendado: t3.micro ou t3.small (suficiente para o volume do protótipo/produção inicial).
- Armazenamento: 20 GB a 30 GB EBS gp3.

### 5.2 Segurança (Security Group / Firewall)
- Porta 22 (SSH): Apenas para o IP do administrador do sistema.
- Porta 80 (HTTP): Aberta para tráfego público web.
- Porta 443 (HTTPS): Aberta para tráfego seguro com certificado SSL (Certbot / Let's Encrypt).
- Porta 8080 (Backend) e 5432 (PostgreSQL): Bloqueadas para acesso externo público (comunicação interna via rede Docker).

### 5.3 Arquitetura de Deploy no EC2
1. O servidor EC2 instala o Docker Engine e Docker Compose.
2. O arquivo docker-compose.yml orquestra o banco PostgreSQL e o Backend Spring Boot na mesma rede privada virtual do Docker.
3. Um servidor Nginx atua como Proxy Reverso no EC2, recebendo as requisições HTTPS do domínio (ex.: https://limpservice.com.br) e redirecionando internamente para:
   - Arquivos estáticos do Frontend (/var/www/html ou porta 5500).
   - Chamadas de API REST para o container Spring Boot (http://localhost:8080/api/v1).