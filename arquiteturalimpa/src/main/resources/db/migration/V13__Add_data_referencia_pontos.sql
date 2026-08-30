-- V13: Adiciona campo data de referência para pontos eletrônicos
-- Adiciona campo data na tabela pontos_eletronicos para separar data de referência da data de criação

-- Adicionar campo data de referência
ALTER TABLE pontos_eletronicos
ADD COLUMN data DATE;

-- Preencher o campo data com base na created_at existente (para dados já existentes)
UPDATE pontos_eletronicos 
SET data = DATE(created_at);

-- Tornar o campo NOT NULL após preenchimento
ALTER TABLE pontos_eletronicos
ALTER COLUMN data SET NOT NULL;

-- Criar índice para consultas por usuário e data
CREATE INDEX idx_pontos_usuario_data ON pontos_eletronicos(usuario_id, data);

-- Adicionar comentário na coluna
COMMENT ON COLUMN pontos_eletronicos.data IS 'Data de referência do ponto (independente da data de criação)';