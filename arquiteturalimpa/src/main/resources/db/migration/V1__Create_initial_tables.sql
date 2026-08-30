-- Create Users table
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Work Journey table
CREATE TABLE jornadas_trabalho (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    horas_semanais INTEGER NOT NULL,
    dias_trabalhados INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Time Clock table
CREATE TABLE pontos_eletronicos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    tipo_ponto VARCHAR(20) NOT NULL CHECK (tipo_ponto IN ('ENTRADA', 'SAIDA', 'SAIDA_ALMOCO', 'RETORNO_ALMOCO')),
    localizacao VARCHAR(200),
    observacao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_pontos_usuario_data ON pontos_eletronicos(usuario_id, DATE(data_hora));
CREATE INDEX idx_pontos_data_hora ON pontos_eletronicos(data_hora);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_cpf ON usuarios(cpf);