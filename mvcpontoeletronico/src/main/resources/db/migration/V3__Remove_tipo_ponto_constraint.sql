-- Remove the CHECK constraint for tipo_ponto to allow enum values from Java
-- This allows the TipoPonto enum to store values like ENTRADA_1, SAIDA_1, etc.

ALTER TABLE pontos_eletronicos DROP CONSTRAINT IF EXISTS pontos_eletronicos_tipo_ponto_check;