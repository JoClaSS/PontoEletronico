import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Alert,
  Snackbar
} from '@mui/material';
import {
  PersonAdd as PersonAddIcon,
  Save as SaveIcon,
  Clear as ClearIcon
} from '@mui/icons-material';
import { useApi } from '../../hooks/useApi';
import type { Usuario } from '../../types';

interface FormData {
  nome: string;
  email: string;
  cpf: string;
}

const RegisterUser: React.FC = () => {
  const { useUsuarios } = useApi();
  const { criarUsuario } = useUsuarios();

  const [formData, setFormData] = useState<FormData>({
    nome: '',
    email: '',
    cpf: ''
  });

  const [snackbar, setSnackbar] = useState({ 
    open: false, 
    message: '', 
    severity: 'success' as 'success' | 'error' | 'warning' 
  });

  const [loading, setLoading] = useState(false);

  const handleInputChange = (field: keyof FormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: event.target.value
    }));
  };

  const validateForm = (): string | null => {
    if (!formData.nome.trim()) {
      return 'Nome é obrigatório';
    }

    if (!formData.email.trim()) {
      return 'Email é obrigatório';
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      return 'Email deve ter um formato válido';
    }

    if (!formData.cpf.trim()) {
      return 'CPF é obrigatório';
    }

    return null;
  };

  const formatCPF = (value: string): string => {
    // Remove tudo que não for número
    const numbers = value.replace(/\D/g, '');
    
    // Aplica a máscara XXX.XXX.XXX-XX
    if (numbers.length <= 11) {
      return numbers
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }
    
    return value;
  };

  const handleCPFChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatCPF(event.target.value);
    setFormData(prev => ({
      ...prev,
      cpf: formatted
    }));
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      setSnackbar({ 
        open: true, 
        message: validationError, 
        severity: 'warning' 
      });
      return;
    }

    setLoading(true);

    try {
      // Prepara dados para MVC
      const userData: Omit<Usuario, 'id' | 'createdAt' | 'updatedAt'> = {
        nome: formData.nome.trim(),
        email: formData.email.trim(),
        cpf: formData.cpf.replace(/\D/g, '')
      };

      await criarUsuario(userData);

      setSnackbar({ 
        open: true, 
        message: 'Usuário criado com sucesso!', 
        severity: 'success' 
      });

      // Limpa o formulário
      setFormData({
        nome: '',
        email: '',
        cpf: ''
      });
    } catch (error: any) {
      const errorMessage = error.message || 'Erro ao criar usuário';
      setSnackbar({ 
        open: true, 
        message: errorMessage, 
        severity: 'error' 
      });
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setFormData({
      nome: '',
      email: '',
      cpf: ''
    });
  };

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto' }}>
      <Card elevation={3}>
        <CardContent>
          {/* Cabeçalho */}
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
            <PersonAddIcon sx={{ mr: 2, fontSize: 32, color: 'primary.main' }} />
            <Typography variant="h4" component="h1" sx={{ color: 'black' }}>
              Cadastrar Usuário
            </Typography>
          </Box>

          {/* Formulário */}
          <Box component="form" onSubmit={handleSubmit}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              {/* Linha 1: Nome e Email */}
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Box sx={{ flex: 1, minWidth: 280 }}>
                  <TextField
                    fullWidth
                    label="Nome"
                    value={formData.nome}
                    onChange={handleInputChange('nome')}
                    required
                    variant="outlined"
                  />
                </Box>
                <Box sx={{ flex: 1, minWidth: 280 }}>
                  <TextField
                    fullWidth
                    label="Email"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange('email')}
                    required
                    variant="outlined"
                  />
                </Box>
              </Box>

              {/* Linha 2: CPF */}
              <Box sx={{ flex: 1, minWidth: 280 }}>
                <TextField
                  fullWidth
                  label="CPF"
                  value={formData.cpf}
                  onChange={handleCPFChange}
                  placeholder="000.000.000-00"
                  required
                  variant="outlined"
                  inputProps={{ maxLength: 14 }}
                />
              </Box>

              {/* Botões de ação */}
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                <Button
                  variant="outlined"
                  startIcon={<ClearIcon />}
                  onClick={handleClear}
                  disabled={loading}
                >
                  Limpar
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={<SaveIcon />}
                  disabled={loading}
                >
                  {loading ? 'Salvando...' : 'Salvar'}
                </Button>
              </Box>
            </Box>
          </Box>
        </CardContent>
      </Card>

      {/* Snackbar para mensagens */}
      <Snackbar 
        open={snackbar.open} 
        autoHideDuration={6000} 
        onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert 
          onClose={() => setSnackbar(prev => ({ ...prev, open: false }))} 
          severity={snackbar.severity}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default RegisterUser;