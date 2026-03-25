-- Criação da tabela jornadas_trabalho
CREATE TABLE jornadas_trabalho (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    inicio_expediente TIME NOT NULL,
    fim_expediente TIME NOT NULL,
    inicio_intervalo TIME,
    fim_intervalo TIME,
    carga_horaria_diaria INTERVAL NOT NULL DEFAULT '8 hours',
    permite_hora_extra BOOLEAN NOT NULL DEFAULT false,
    limite_hora_extra INTERVAL DEFAULT '2 hours',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela usuarios
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    cargo VARCHAR(100),
    departamento VARCHAR(100),
    ativo BOOLEAN NOT NULL DEFAULT true,
    jornada_id UUID REFERENCES jornadas_trabalho(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela pontos_eletronicos  
CREATE TABLE pontos_eletronicos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    data_hora TIMESTAMP NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA', 'ENTRADA_ALMOCO', 'SAIDA_ALMOCO')),
    localizacao TEXT,
    observacao TEXT,
    dispositivo_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Criação de índices para melhor performance
CREATE INDEX idx_pontos_usuario_data ON pontos_eletronicos(usuario_id, DATE(data_hora));
CREATE INDEX idx_pontos_data_hora ON pontos_eletronicos(data_hora);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

-- Comentários nas tabelas
COMMENT ON TABLE jornadas_trabalho IS 'Configurações de jornada de trabalho dos funcionários';
COMMENT ON TABLE usuarios IS 'Funcionários do sistema de ponto eletrônico';
COMMENT ON TABLE pontos_eletronicos IS 'Registros de entrada e saída dos funcionários';

-- Comentários nas colunas principais
COMMENT ON COLUMN pontos_eletronicos.tipo IS 'Tipo do registro: ENTRADA, SAIDA, ENTRADA_ALMOCO, SAIDA_ALMOCO';
COMMENT ON COLUMN jornadas_trabalho.carga_horaria_diaria IS 'Carga horária esperada por dia em formato interval';
COMMENT ON COLUMN usuarios.ativo IS 'Indica se o usuário está ativo e pode registrar ponto';