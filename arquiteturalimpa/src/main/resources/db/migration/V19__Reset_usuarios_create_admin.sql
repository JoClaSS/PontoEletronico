-- Migration V19: Reset usuarios ativos 
-- Objetivo: Limpar usuários ativos existentes para permitir criação limpa do admin

-- Deletar todos os usuários ativos (mantém histórico através de foreign keys se configurado)
DELETE FROM usuarios WHERE ativo = true;

-- Comentário para documentação
COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema - resetada em V19 para configuração inicial limpa';

-- Nota: O usuário administrador será criado via código Java usando as variáveis de ambiente
-- Verificar a classe AdminUserInitializer para a criação automática do usuário admin