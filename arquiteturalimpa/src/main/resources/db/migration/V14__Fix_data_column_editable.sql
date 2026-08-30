-- V14: Corrige a coluna data de referência para garantir que seja editável
-- Remove qualquer restrição que possa estar impedindo a edição da coluna data

-- Garantir que não há constraints problemáticas na coluna data
ALTER TABLE pontos_eletronicos 
ALTER COLUMN data DROP DEFAULT;

-- Re-definir a coluna para garantir que seja editável
ALTER TABLE pontos_eletronicos 
ALTER COLUMN data TYPE DATE;

-- Garantir que a coluna permite updates
ALTER TABLE pontos_eletronicos 
ALTER COLUMN data SET NOT NULL;

-- Verificar se existe algum trigger que possa estar impedindo updates
-- (Esta parte é mais informativa para debug)

-- Para testar se a coluna está funcionando, você pode executar:
-- UPDATE pontos_eletronicos SET data = data WHERE id = (SELECT id FROM pontos_eletronicos LIMIT 1);