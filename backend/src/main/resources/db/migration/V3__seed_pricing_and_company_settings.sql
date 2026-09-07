CREATE TABLE IF NOT EXISTS precos_orcamento (
    id BIGSERIAL PRIMARY KEY,
    servico_id BIGINT NOT NULL REFERENCES servicos(id) ON DELETE RESTRICT,
    modelo VARCHAR(100) NOT NULL,
    preco_base NUMERIC(10, 2) NOT NULL CHECK (preco_base >= 0),
    unidade VARCHAR(30) NOT NULL DEFAULT 'fixo',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_preco_servico_modelo UNIQUE (servico_id, modelo)
);

CREATE TABLE IF NOT EXISTS configuracoes_empresa (
    id BIGSERIAL PRIMARY KEY,
    chave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO precos_orcamento (servico_id, modelo, preco_base, unidade)
SELECT s.id, dados.modelo, dados.preco_base, dados.unidade
FROM servicos s
CROSS JOIN (VALUES
    ('1.80-2.00', 150.00, 'fixo'),
    ('2.20-2.50', 180.00, 'fixo'),
    ('3.00-3.30', 220.00, 'fixo')
) AS dados(modelo, preco_base, unidade)
WHERE s.nome = 'Sofá'
  AND NOT EXISTS (
      SELECT 1 FROM precos_orcamento p
      WHERE p.servico_id = s.id AND p.modelo = dados.modelo
  );

INSERT INTO precos_orcamento (servico_id, modelo, preco_base, unidade)
SELECT s.id, dados.modelo, dados.preco_base, dados.unidade
FROM servicos s
CROSS JOIN (VALUES
    ('assento', 15.00, 'fixo'),
    ('assento-encosto', 20.00, 'fixo')
) AS dados(modelo, preco_base, unidade)
WHERE s.nome = 'Cadeiras'
  AND NOT EXISTS (
      SELECT 1 FROM precos_orcamento p
      WHERE p.servico_id = s.id AND p.modelo = dados.modelo
  );

INSERT INTO precos_orcamento (servico_id, modelo, preco_base, unidade)
SELECT s.id, 'padrao', 60.00, 'fixo'
FROM servicos s
WHERE s.nome = 'Poltrona'
  AND NOT EXISTS (
      SELECT 1 FROM precos_orcamento p
      WHERE p.servico_id = s.id AND p.modelo = 'padrao'
  );

INSERT INTO precos_orcamento (servico_id, modelo, preco_base, unidade)
SELECT s.id, dados.modelo, dados.preco_base, dados.unidade
FROM servicos s
CROSS JOIN (VALUES
    ('solteiro', 90.00, 'fixo'),
    ('casal', 150.00, 'fixo'),
    ('queen', 160.00, 'fixo'),
    ('king', 180.00, 'fixo')
) AS dados(modelo, preco_base, unidade)
WHERE s.nome = 'Colchão'
  AND NOT EXISTS (
      SELECT 1 FROM precos_orcamento p
      WHERE p.servico_id = s.id AND p.modelo = dados.modelo
  );

INSERT INTO precos_orcamento (servico_id, modelo, preco_base, unidade)
SELECT s.id, 'metro-linear', 20.00, 'por_metro'
FROM servicos s
WHERE s.nome = 'Tapete'
  AND NOT EXISTS (
      SELECT 1 FROM precos_orcamento p
      WHERE p.servico_id = s.id AND p.modelo = 'metro-linear'
  );

INSERT INTO configuracoes_empresa (chave, valor)
VALUES
    ('telefone', '(14) 99834-4797'),
    ('instagram', '@limpservicebauru'),
    ('horario_semana', 'segunda a sexta, das 8h às 18h.'),
    ('horario_sabado', 'das 8h às 13h.'),
    ('horario_domingo', 'fechado.'),
    ('ano_site', '2026')
ON CONFLICT (chave) DO UPDATE
SET valor = EXCLUDED.valor, ativo = TRUE;
