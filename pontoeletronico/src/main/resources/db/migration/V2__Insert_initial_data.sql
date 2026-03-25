-- Inserção da jornada padrão
INSERT INTO jornadas_trabalho (
    id, 
    nome, 
    inicio_expediente, 
    fim_expediente, 
    inicio_intervalo, 
    fim_intervalo, 
    carga_horaria_diaria, 
    permite_hora_extra, 
    limite_hora_extra
) VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Jornada Padrão 8h',
    '08:00:00',
    '17:00:00', 
    '12:00:00',
    '13:00:00',
    '8 hours',
    true,
    '2 hours'
);

-- Inserção de usuários de exemplo
INSERT INTO usuarios (
    id,
    email,
    nome,
    cargo,
    departamento,
    ativo,
    jornada_id
) VALUES 
(
    'f1e2d3c4-b5a6-7890-cdef-123456789012',
    'jose@empresa.com',
    'José Silva',
    'Desenvolvedor',
    'TI',
    true,
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890'
),
(
    '12345678-9abc-def0-1234-56789abcdef0',
    'maria@empresa.com',
    'Maria Santos',
    'Analista de RH',
    'Recursos Humanos',
    true,
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890'
),
(
    'abcdef01-2345-6789-abcd-ef0123456789',
    'carlos@empresa.com',
    'Carlos Oliveira',
    'Gerente de Projetos',
    'TI',
    true,
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890'
);

-- Jornada meio período para diferentes tipos de funcionário
INSERT INTO jornadas_trabalho (
    id,
    nome,
    inicio_expediente,
    fim_expediente,
    inicio_intervalo,
    fim_intervalo,
    carga_horaria_diaria,
    permite_hora_extra,
    limite_hora_extra
) VALUES (
    'b2c3d4e5-f6g7-8901-bcde-f23456789012',
    'Jornada Meio Período',
    '14:00:00',
    '18:00:00',
    null,
    null,
    '4 hours',
    false,
    '0 hours'
);