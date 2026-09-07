UPDATE servicos
SET nome = 'Sofá', descricao = 'Higienização completa para todos os tipos de sofás.'
WHERE nome = 'Higienização de Sofá';

UPDATE servicos
SET nome = 'Cadeiras', descricao = 'Limpeza e higienização de cadeiras estofadas.'
WHERE nome = 'Limpeza de Cadeiras';

UPDATE servicos
SET nome = 'Tapete', descricao = 'Higienização profunda para renovar e cuidar dos seus tapetes.'
WHERE nome = 'Cuidado de Tapete';

UPDATE servicos
SET nome = 'Almofadas', descricao = 'Processo de limpeza e revitalização de almofadas.', ativo = FALSE
WHERE nome = 'Lavagem de Almofadas';

INSERT INTO servicos (nome, descricao, ativo)
SELECT 'Sofá', 'Higienização completa para todos os tipos de sofás.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM servicos WHERE nome = 'Sofá');

INSERT INTO servicos (nome, descricao, ativo)
SELECT 'Colchão', 'Limpeza e higienização para deixar seu colchão mais limpo.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM servicos WHERE nome = 'Colchão');

INSERT INTO servicos (nome, descricao, ativo)
SELECT 'Tapete', 'Higienização profunda para renovar e cuidar dos seus tapetes.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM servicos WHERE nome = 'Tapete');

INSERT INTO servicos (nome, descricao, ativo)
SELECT 'Poltrona', 'Higienização de poltronas estofadas.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM servicos WHERE nome = 'Poltrona');

INSERT INTO servicos (nome, descricao, ativo)
SELECT 'Cadeiras', 'Limpeza e higienização de cadeiras estofadas.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM servicos WHERE nome = 'Cadeiras');