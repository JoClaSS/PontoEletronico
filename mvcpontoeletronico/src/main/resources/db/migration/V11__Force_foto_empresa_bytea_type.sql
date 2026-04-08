-- V11: Garantir que a coluna foto_empresa seja tratada corretamente pelo Hibernate
-- Esta migração adicional força o tipo correto caso a V10 não tenha resolvido

-- Verifica e corrige o tipo da coluna se necessário
DO $$ 
BEGIN
    -- Remove qualquer constraint ou index que possa estar interferindo
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'configuracoes_empresa' 
               AND column_name = 'foto_empresa') THEN
        
        -- Força o tipo correto explicitamente
        ALTER TABLE configuracoes_empresa ALTER COLUMN foto_empresa TYPE BYTEA USING foto_empresa::BYTEA;
        
    END IF;
END $$;