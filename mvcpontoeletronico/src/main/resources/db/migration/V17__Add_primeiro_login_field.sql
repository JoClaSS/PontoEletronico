-- Adicionar campo primeiro_login para controlar redefinição obrigatória de senha
ALTER TABLE usuarios ADD COLUMN primeiro_login BOOLEAN DEFAULT false NOT NULL;

-- Apenas novos usuários precisarão redefinir senha (usuários existentes continuam normais)
-- UPDATE usuarios SET primeiro_login = false; (já é false por padrão)

-- Comentário sobre o campo
COMMENT ON COLUMN usuarios.primeiro_login IS 'Indica se é o primeiro login do usuário (requer redefinição de senha)';