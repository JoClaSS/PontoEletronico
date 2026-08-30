-- V20: Adiciona novo tipo de solicitação - Solicitação de férias
-- Insere novo motivo de solicitação para férias

INSERT INTO motivos_solicitacao (descricao, ativo, requer_anexo) 
VALUES ('Solicitação de férias', false, false);