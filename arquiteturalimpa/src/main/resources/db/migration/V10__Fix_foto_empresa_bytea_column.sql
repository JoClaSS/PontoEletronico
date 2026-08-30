-- V10: Corrigir problema da coluna foto_empresa com tipo BYTEA vs OID/BIGINT
-- Remove e recria a coluna para garantir que seja interpretada como BYTEA corretamente

-- Primeiro, remove a coluna problemática
ALTER TABLE configuracoes_empresa DROP COLUMN IF EXISTS foto_empresa;

-- Recria a coluna com definição explícita e sem anotação LOB problemática  
ALTER TABLE configuracoes_empresa ADD COLUMN foto_empresa BYTEA NULL;

-- Atualiza os comentários
COMMENT ON COLUMN configuracoes_empresa.foto_empresa IS 'Logo/foto da empresa em formato binário (BYTEA) - sem LOB';