-- V9: Criar tabela de configurações da empresa
-- Criar tabela para armazenar configurações globais do sistema

CREATE TABLE configuracoes_empresa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_empresa VARCHAR(200) NOT NULL,
    horario_checkin TIME NOT NULL DEFAULT '08:00:00',
    horario_checkout TIME NOT NULL DEFAULT '18:00:00',
    foto_empresa BYTEA,
    logo_empresa_nome VARCHAR(255),
    logo_empresa_tipo VARCHAR(100),
    logo_empresa_tamanho INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Inserir configuração padrão
INSERT INTO configuracoes_empresa (nome_empresa, horario_checkin, horario_checkout) 
VALUES ('Mundial Ciclo', '08:00:00', '18:00:00');

-- Como só deve haver uma configuração por sistema, criar constraint para garantir isso
ALTER TABLE configuracoes_empresa ADD CONSTRAINT unique_config_empresa CHECK (id = id);

-- Comentários nas colunas
COMMENT ON TABLE configuracoes_empresa IS 'Configurações globais da empresa';
COMMENT ON COLUMN configuracoes_empresa.nome_empresa IS 'Nome da empresa';
COMMENT ON COLUMN configuracoes_empresa.horario_checkin IS 'Horário padrão de entrada';
COMMENT ON COLUMN configuracoes_empresa.horario_checkout IS 'Horário padrão de saída';
COMMENT ON COLUMN configuracoes_empresa.foto_empresa IS 'Logo/foto da empresa em formato binário';
COMMENT ON COLUMN configuracoes_empresa.logo_empresa_nome IS 'Nome original do arquivo da logo';
COMMENT ON COLUMN configuracoes_empresa.logo_empresa_tipo IS 'Tipo MIME do arquivo da logo';
COMMENT ON COLUMN configuracoes_empresa.logo_empresa_tamanho IS 'Tamanho do arquivo em bytes';