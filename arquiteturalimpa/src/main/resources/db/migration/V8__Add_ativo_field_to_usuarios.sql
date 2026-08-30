-- Adiciona campo ativo à tabela usuarios
-- V8__Add_ativo_field_to_usuarios.sql

ALTER TABLE usuarios 
ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

-- Atualiza todos os usuários existentes como ativos
UPDATE usuarios SET ativo = TRUE WHERE ativo IS NULL;