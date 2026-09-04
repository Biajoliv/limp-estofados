-- Script completo do banco de dados para o projeto Limpeza de Estofados
-- Este arquivo pode ser versionado no GitHub e editado futuramente.
-- Uso recomendado:
--   psql -U postgres -d postgres -f database/limpeza_estofados.sql
-- ou importar via pgAdmin.

DROP DATABASE IF EXISTS limpeza_estofados;
CREATE DATABASE limpeza_estofados;

\c limpeza_estofados;

CREATE TABLE servicos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE solicitacao_orcamento (
    id BIGSERIAL PRIMARY KEY,
    servico_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_solicitacao_servico
        FOREIGN KEY (servico_id) REFERENCES servicos(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- Dados iniciais para testes e demonstração
INSERT INTO servicos (nome, descricao, ativo) VALUES
('Higienização de Sofá', 'Limpeza profunda de estofados com produtos específicos para tecidos e couro.', TRUE),
('Limpeza de Cadeiras', 'Remoção de sujeira, odores e manchas em cadeiras e poltronas.', TRUE),
('Cuidado de Tapete', 'Higienização de tapetes e carpetes residenciais e comerciais.', TRUE),
('Lavagem de Almofadas', 'Processo de limpeza e revitalização de almofadas e nichos.', TRUE);

INSERT INTO solicitacao_orcamento (servico_id, nome, telefone, cidade) VALUES
(1, 'Maria Silva', '(11) 99999-1111', 'São Paulo'),
(2, 'João Pereira', '(21) 98888-2222', 'Rio de Janeiro'),
(3, 'Ana Costa', '(31) 97777-3333', 'Belo Horizonte'),
(4, 'Carlos Mendes', '(41) 96666-4444', 'Curitiba');

-- Índices úteis para consultas por cidade e por serviço
CREATE INDEX idx_solicitacao_servico_id ON solicitacao_orcamento(servico_id);
CREATE INDEX idx_solicitacao_cidade ON solicitacao_orcamento(cidade);
CREATE INDEX idx_servicos_ativo ON servicos(ativo);

-- Comentário final para documentação do banco
COMMENT ON TABLE servicos IS 'Catálogo de serviços disponíveis pela empresa.';
COMMENT ON TABLE solicitacao_orcamento IS 'Solicitações de orçamento enviadas pelos clientes.';
