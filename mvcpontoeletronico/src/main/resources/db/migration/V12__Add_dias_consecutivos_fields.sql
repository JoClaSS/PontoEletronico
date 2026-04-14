-- V12: Adiciona campos para solicitações de dias consecutivos
-- Adiciona campos dias_consecutivos e quantidade_dias na tabela solicitacoes

-- Adicionar campos para dias consecutivos
ALTER TABLE solicitacoes
ADD COLUMN dias_consecutivos BOOLEAN DEFAULT false NOT NULL,
ADD COLUMN quantidade_dias INTEGER;

-- Criar índice para consultas por dias consecutivos
CREATE INDEX idx_solicitacoes_dias_consecutivos ON solicitacoes(dias_consecutivos);

-- Adicionar comentários nas colunas
COMMENT ON COLUMN solicitacoes.dias_consecutivos IS 'Se a solicitação é para dias consecutivos';
COMMENT ON COLUMN solicitacoes.quantidade_dias IS 'Quantidade de dias consecutivos (só válido quando dias_consecutivos=true)';