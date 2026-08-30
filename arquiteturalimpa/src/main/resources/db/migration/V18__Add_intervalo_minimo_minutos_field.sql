-- Migration V18: Adiciona campo intervalo_minimo_minutos na tabela configuracoes_empresa
-- Objetivo: Armazenar o intervalo mínimo em minutos entre registros de ponto

-- Adiciona a nova coluna com valor padrão 0 (sem restrição de intervalo)
ALTER TABLE configuracoes_empresa 
ADD COLUMN intervalo_minimo_minutos INTEGER NOT NULL DEFAULT 0;

-- Adiciona comentário para documentação
COMMENT ON COLUMN configuracoes_empresa.intervalo_minimo_minutos IS 'Intervalo mínimo em minutos entre registros de ponto (0 = sem restrição)';

-- Atualiza registros existentes para garantir que tenham o valor padrão
UPDATE configuracoes_empresa 
SET intervalo_minimo_minutos = 0 
WHERE intervalo_minimo_minutos IS NULL;