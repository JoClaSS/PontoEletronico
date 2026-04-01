-- V4: Refatoração da tabela pontos_eletronicos
-- Adiciona colunas de entrada/saída (entrada1, saida1, entrada2, saida2, entrada3, saida3)
-- Remove coluna tipo_ponto

-- Adicionar as novas colunas para os pontos
ALTER TABLE pontos_eletronicos
ADD COLUMN entrada1 TIMESTAMP NULL,
ADD COLUMN saida1 TIMESTAMP NULL,
ADD COLUMN entrada2 TIMESTAMP NULL,
ADD COLUMN saida2 TIMESTAMP NULL,
ADD COLUMN entrada3 TIMESTAMP NULL,
ADD COLUMN saida3 TIMESTAMP NULL;

-- Migrar dados existentes (opcional - manter comentado se não necessário)
-- UPDATE pontos_eletronicos 
-- SET entrada1 = data_hora 
-- WHERE tipo_ponto IN ('ENTRADA', 'ENTRADA_1');
--
-- UPDATE pontos_eletronicos 
-- SET saida1 = data_hora 
-- WHERE tipo_ponto IN ('SAIDA', 'SAIDA_1');
--
-- UPDATE pontos_eletronicos 
-- SET entrada2 = data_hora 
-- WHERE tipo_ponto = 'ENTRADA_2';
--
-- UPDATE pontos_eletronicos 
-- SET saida2 = data_hora 
-- WHERE tipo_ponto = 'SAIDA_2';
--
-- UPDATE pontos_eletronicos 
-- SET entrada3 = data_hora 
-- WHERE tipo_ponto = 'ENTRADA_3';
--
-- UPDATE pontos_eletronicos 
-- SET saida3 = data_hora 
-- WHERE tipo_ponto = 'SAIDA_3';

-- Remover a coluna tipo_ponto e data_hora (agora redundante)
ALTER TABLE pontos_eletronicos
DROP COLUMN tipo_ponto,
DROP COLUMN data_hora;

-- Criar índices para performance nas consultas por data
CREATE INDEX idx_pontos_eletronicos_usuario_entrada1 ON pontos_eletronicos(usuario_id, DATE(entrada1));
CREATE INDEX idx_pontos_eletronicos_usuario_created_at ON pontos_eletronicos(usuario_id, DATE(created_at));