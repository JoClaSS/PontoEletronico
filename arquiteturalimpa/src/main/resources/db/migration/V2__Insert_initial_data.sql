-- Insert sample work journeys
INSERT INTO jornadas_trabalho (id, nome, horas_semanais, dias_trabalhados) VALUES
    (gen_random_uuid(), 'Jornada Padrão 40h', 40, 5),
    (gen_random_uuid(), 'Jornada Parcial 20h', 20, 3),
    (gen_random_uuid(), 'Jornada 6x1', 44, 6);

-- Insert sample users
INSERT INTO usuarios (id, nome, email, cpf) VALUES 
    (gen_random_uuid(), 'João Silva', 'joao.silva@empresa.com', '123.456.789-01'),
    (gen_random_uuid(), 'Maria Santos', 'maria.santos@empresa.com', '987.654.321-02'),
    (gen_random_uuid(), 'Pedro Oliveira', 'pedro.oliveira@empresa.com', '456.789.123-03');