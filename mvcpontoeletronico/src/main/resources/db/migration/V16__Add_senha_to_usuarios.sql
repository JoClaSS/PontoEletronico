

-- Add role column if not exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'usuarios' AND column_name = 'senha') THEN
        ALTER TABLE usuarios ADD COLUMN senha VARCHAR(255);
    END IF;
END $$;

-- Add role column if not exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'usuarios' AND column_name = 'role') THEN
        ALTER TABLE usuarios ADD COLUMN role VARCHAR(50) DEFAULT 'FUNCIONARIO';
    END IF;
END $$;

-- Add ativo column if not exists  
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'usuarios' AND column_name = 'ativo') THEN
        ALTER TABLE usuarios ADD COLUMN ativo BOOLEAN DEFAULT true;
    END IF;
END $$;

-- Update existing users with default password "123456" using MD5
UPDATE usuarios SET 
    senha = MD5('123456'),
    role = 'FUNCIONARIO',
    ativo = true
WHERE senha IS NULL;

-- Insert admin user with password "admin123" using MD5
INSERT INTO usuarios (id, nome, email, cpf, senha, role, ativo) VALUES 
    (gen_random_uuid(), 'Administrador', 'admin@empresa.com', '000.000.000-00', MD5('admin123'), 'ADMIN', true)
ON CONFLICT (email) DO NOTHING;