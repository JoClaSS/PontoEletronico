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
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { Backend } from '../../types';
import type { Usuario } from '../../types';

interface FormData {
  nome: string;
  email: string;
  cpf: string;
  cargo: string;
  departamento: string;
}

const RegisterUser: React.FC = () => {
  const { selectedBackend } = useAppContext();
  const { useUsuarios } = useApi();
  const { criarUsuario } = useUsuarios();

  const [formData, setFormData] = useState<FormData>({
    nome: '',
    email: '',
    cpf: '',
    cargo: '',
    departamento: ''
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

    // CPF é obrigatório apenas para backend MVC
    if (selectedBackend === Backend.MVC && !formData.cpf.trim()) {
      return 'CPF é obrigatório para o backend MVC';
    }

    // Cargo e departamento são obrigatórios apenas para backend Clean
    if (selectedBackend === Backend.CLEAN && !formData.cargo.trim()) {
      return 'Cargo é obrigatório para o backend Clean Architecture';
    }

    if (selectedBackend === Backend.CLEAN && !formData.departamento.trim()) {
      return 'Departamento é obrigatório para o backend Clean Architecture';
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
      // Prepara dados baseado no backend selecionado
      let userData: Omit<Usuario, 'id' | 'createdAt' | 'updatedAt'>;

      if (selectedBackend === Backend.MVC) {
        userData = {
          nome: formData.nome.trim(),
          email: formData.email.trim(),
          cpf: formData.cpf.replace(/\D/g, '')
        };
      } else {
        userData = {
          nome: formData.nome.trim(),
          email: formData.email.trim(),
          cargo: formData.cargo.trim(),
          departamento: formData.departamento.trim(),
          ativo: true
        };
      }

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
        cpf: '',
        cargo: '',
        departamento: ''
      });
    } catch (error) {
      setSnackbar({ 
        open: true, 
        message: error instanceof Error ? error.message : 'Erro ao criar usuário', 
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
      cpf: '',
      cargo: '',
      departamento: ''
    });
  };

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto' }}>
      <Card elevation={3}>
        <CardContent>
          {/* Cabeçalho */}
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
            <PersonAddIcon sx={{ mr: 2, fontSize: 32, color: 'primary.main' }} />
            <Typography variant="h4" component="h1">
              Cadastrar Usuário
            </Typography>
          </Box>

          {/* Informação sobre backend ativo */}
          <Alert severity="info" sx={{ mb: 3 }}>
            Backend ativo: <strong>{selectedBackend === Backend.MVC ? 'MVC Architecture' : 'Clean Architecture'}</strong>
            {selectedBackend === Backend.MVC ? 
              ' (CPF obrigatório)' : 
              ' (Cargo e Departamento obrigatórios)'
            }
          </Alert>

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

              {/* Linha 2: Campos específicos por backend */}
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                {/* CPF - visível apenas para backend MVC */}
                {selectedBackend === Backend.MVC && (
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
                )}

                {/* Cargo - visível apenas para backend Clean */}
                {selectedBackend === Backend.CLEAN && (
                  <Box sx={{ flex: 1, minWidth: 280 }}>
                    <TextField
                      fullWidth
                      label="Cargo"
                      value={formData.cargo}
                      onChange={handleInputChange('cargo')}
                      required
                      variant="outlined"
                    />
                  </Box>
                )}

                {/* Departamento - visível apenas para backend Clean */}
                {selectedBackend === Backend.CLEAN && (
                  <Box sx={{ flex: 1, minWidth: 280 }}>
                    <TextField
                      fullWidth
                      label="Departamento"
                      value={formData.departamento}
                      onChange={handleInputChange('departamento')}
                      required
                      variant="outlined"
                    />
                  </Box>
                )}
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