-- V5: Criação das tabelas para sistema de solicitações
-- Tabela de motivos das solicitações e tabela de solicitações

-- Tabela de motivos
CREATE TABLE motivos_solicitacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    descricao VARCHAR(100) NOT NULL UNIQUE,
    ativo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de solicitações
CREATE TABLE solicitacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    data_referencia DATE NOT NULL,
    usuario_id UUID NOT NULL,
    motivo_id UUID NOT NULL,
    descricao TEXT,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ABERTO', 'RESOLVIDO', 'CANCELADO')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (motivo_id) REFERENCES motivos_solicitacao(id)
);

-- Inserção dos motivos padrão
INSERT INTO motivos_solicitacao (descricao) VALUES 
('Justificar ausência'),
('Sem acesso ao ponto'),
('Correção de ponto');

-- Índices para performance
CREATE INDEX idx_solicitacoes_usuario_id ON solicitacoes(usuario_id);
CREATE INDEX idx_solicitacoes_status ON solicitacoes(status);
CREATE INDEX idx_solicitacoes_data_referencia ON solicitacoes(data_referencia);
CREATE INDEX idx_solicitacoes_created_at ON solicitacoes(created_at);