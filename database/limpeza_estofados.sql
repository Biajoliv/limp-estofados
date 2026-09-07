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

CREATE TABLE precos_orcamento (
    id BIGSERIAL PRIMARY KEY,
    servico_id BIGINT NOT NULL REFERENCES servicos(id) ON DELETE RESTRICT,
    modelo VARCHAR(100) NOT NULL,
    preco_base NUMERIC(10, 2) NOT NULL CHECK (preco_base >= 0),
    unidade VARCHAR(30) NOT NULL DEFAULT 'fixo',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_preco_servico_modelo UNIQUE (servico_id, modelo)
);

CREATE TABLE configuracoes_empresa (
    id BIGSERIAL PRIMARY KEY,
    chave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Dados iniciais para testes e demonstração
INSERT INTO servicos (nome, descricao, ativo) VALUES
('Sofá', 'Higienização completa para todos os tipos de sofás.', TRUE),
('Colchão', 'Limpeza e higienização para deixar seu colchão mais limpo.', TRUE),
('Tapete', 'Higienização profunda para renovar e cuidar dos seus tapetes.', TRUE),
('Poltrona', 'Higienização de poltronas estofadas.', TRUE),
('Cadeiras', 'Limpeza e higienização de cadeiras estofadas.', TRUE);

INSERT INTO precos_orcamento (servico_id, modelo, preco_base, unidade) VALUES
(1, '1.80-2.00', 150.00, 'fixo'),
(1, '2.20-2.50', 180.00, 'fixo'),
(1, '3.00-3.30', 220.00, 'fixo'),
(2, 'solteiro', 90.00, 'fixo'),
(2, 'casal', 150.00, 'fixo'),
(2, 'queen', 160.00, 'fixo'),
(2, 'king', 180.00, 'fixo'),
(3, 'metro-linear', 20.00, 'por_metro'),
(4, 'padrao', 60.00, 'fixo'),
(5, 'assento', 15.00, 'fixo'),
(5, 'assento-encosto', 20.00, 'fixo');

INSERT INTO configuracoes_empresa (chave, valor) VALUES
('telefone', '(14) 99834-4797'),
('instagram', '@limpservicebauru'),
('horario_semana', 'segunda a sexta, das 8h às 18h.'),
('horario_sabado', 'das 8h às 13h.'),
('horario_domingo', 'fechado.'),
('ano_site', '2026');

INSERT INTO solicitacao_orcamento (servico_id, nome, telefone, cidade) VALUES
(1, 'Maria Silva', '(11) 99999-1111', 'São Paulo'),
(2, 'João Pereira', '(21) 98888-2222', 'Rio de Janeiro'),
(3, 'Ana Costa', '(31) 97777-3333', 'Belo Horizonte'),
(4, 'Carlos Mendes', '(41) 96666-4444', 'Curitiba');

-- Índices úteis para consultas por cidade e por serviço
CREATE INDEX idx_solicitacao_servico_id ON solicitacao_orcamento(servico_id);
CREATE INDEX idx_solicitacao_cidade ON solicitacao_orcamento(cidade);
CREATE INDEX idx_servicos_ativo ON servicos(ativo);
CREATE INDEX idx_precos_servico_id ON precos_orcamento(servico_id);
CREATE INDEX idx_configuracoes_empresa_ativo ON configuracoes_empresa(ativo);

-- Comentário final para documentação do banco
COMMENT ON TABLE servicos IS 'Catálogo de serviços disponíveis pela empresa.';
COMMENT ON TABLE solicitacao_orcamento IS 'Solicitações de orçamento enviadas pelos clientes.';
COMMENT ON TABLE precos_orcamento IS 'Regras de preço usadas na calculadora de orçamento.';
COMMENT ON TABLE configuracoes_empresa IS 'Dados públicos e horários de atendimento da empresa.';
