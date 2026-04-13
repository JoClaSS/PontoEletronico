-- V15: Alternativa - Recria a coluna data se ainda houver problemas
-- Execute esta migration apenas se a V14 não resolver o problema

-- Passo 1: Salvar os dados em uma coluna temporária
ALTER TABLE pontos_eletronicos 
ADD COLUMN data_temp DATE;

UPDATE pontos_eletronicos 
SET data_temp = data;

-- Passo 2: Remover a coluna problemática
ALTER TABLE pontos_eletronicos 
DROP COLUMN data CASCADE;

-- Passo 3: Recriar a coluna do zero
ALTER TABLE pontos_eletronicos 
ADD COLUMN data DATE NOT NULL DEFAULT '2000-01-01';

-- Passo 4: Restaurar os dados
UPDATE pontos_eletronicos 
SET data = data_temp;

-- Passo 5: Limpar coluna temporária
ALTER TABLE pontos_eletronicos 
DROP COLUMN data_temp;

-- Passo 6: Remover o default agora que os dados estão restaurados
ALTER TABLE pontos_eletronicos 
ALTER COLUMN data DROP DEFAULT;

-- Passo 7: Recriar o índice
CREATE INDEX IF NOT EXISTS idx_pontos_usuario_data ON pontos_eletronicos(usuario_id, data);