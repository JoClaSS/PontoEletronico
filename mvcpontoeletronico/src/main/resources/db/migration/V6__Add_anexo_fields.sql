-- V6: Adiciona campos para anexos no sistema de solicitações
-- Adiciona campo anexo na tabela solicitacoes e campo requer_anexo na tabela motivos

-- Adicionar campo requer_anexo na tabela motivos_solicitacao
ALTER TABLE motivos_solicitacao
ADD COLUMN requer_anexo BOOLEAN DEFAULT false;

-- Adicionar campo anexo na tabela solicitacoes
ALTER TABLE solicitacoes
ADD COLUMN anexo_nome VARCHAR(255),
ADD COLUMN anexo_tipo VARCHAR(100),
ADD COLUMN anexo_tamanho BIGINT,
ADD COLUMN anexo_conteudo BYTEA;

-- Atualizar o motivo "Justificar ausência" para requerer anexo
UPDATE motivos_solicitacao 
SET requer_anexo = true 
WHERE UPPER(descricao) = UPPER('Justificar ausência');

-- Criar índice para otimizar consultas por requer_anexo
CREATE INDEX idx_motivos_requer_anexo ON motivos_solicitacao(requer_anexo);