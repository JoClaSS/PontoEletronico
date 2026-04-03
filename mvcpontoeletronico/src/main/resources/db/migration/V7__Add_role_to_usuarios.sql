-- Adiciona coluna role na tabela usuarios
ALTER TABLE usuarios 
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'FUNCIONARIO';

-- Criar índice para melhor performance
CREATE INDEX idx_usuarios_role ON usuarios(role);