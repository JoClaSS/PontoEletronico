import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Container,
  InputAdornment,
  Alert,
  Divider
} from '@mui/material';
import {
  Person as PersonIcon,
  Lock as LockIcon,
  Login as LoginIcon
} from '@mui/icons-material';
import { useAppContext } from '../../contexts/AppContext';

const Login: React.FC = () => {
  const { handleLogin } = useAppContext();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      [field]: event.target.value
    }));
    // Limpa erro quando o usuário começa a digitar
    if (error) setError(null);
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    
    // Validações básicas
    if (!formData.email.trim()) {
      setError('E-mail é obrigatório');
      return;
    }
    
    if (!formData.password.trim()) {
      setError('Senha é obrigatória');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Simula uma chamada de API (por enquanto sempre permite login)
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Por enquanto, qualquer e-mail/senha é aceito
      handleLogin({
        id: '1',
        nome: 'Usuário Logado',
        email: formData.email
      });
      
    } catch (err: any) {
      setError(err.message || 'Erro ao fazer login');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoLogin = () => {
    setFormData({
      email: 'admin@pontoeletronico.com',
      password: '123456'
    });
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        backgroundColor: '#1e3a8a',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        p: 2
      }}
    >
      <Container maxWidth="sm">
        <Card
          elevation={12}
          sx={{
            borderRadius: 3,
            overflow: 'hidden'
          }}
        >
          {/* Cabeçalho */}
          <Box
            sx={{
              backgroundColor: '#1e40af',
              color: 'white',
              p: 4,
              textAlign: 'center'
            }}
          >
            <Typography variant="h4" component="h1" gutterBottom>
              Ponto Eletrônico
            </Typography>
            <Typography variant="subtitle1" sx={{ opacity: 0.9 }}>
              Sistema de Controle de Ponto
            </Typography>
          </Box>

          {/* Formulário */}
          <CardContent sx={{ p: 4 }}>
            <form onSubmit={handleSubmit}>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                
                {error && (
                  <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                  </Alert>
                )}

                <TextField
                  label="E-mail"
                  type="email"
                  value={formData.email}
                  onChange={handleChange('email')}
                  fullWidth
                  required
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PersonIcon color="action" />
                      </InputAdornment>
                    )
                  }}
                />

                <TextField
                  label="Senha"
                  type="password"
                  value={formData.password}
                  onChange={handleChange('password')}
                  fullWidth
                  required
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <LockIcon color="action" />
                      </InputAdornment>
                    )
                  }}
                />

                <Button
                  type="submit"
                  variant="contained"
                  size="large"
                  fullWidth
                  disabled={loading}
                  startIcon={<LoginIcon />}
                  sx={{
                    mt: 2,
                    py: 1.5,
                    fontSize: '1.1rem'
                  }}
                >
                  {loading ? 'Entrando...' : 'Entrar'}
                </Button>

                <Divider sx={{ my: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Para demonstração
                  </Typography>
                </Divider>

                <Button
                  variant="outlined"
                  onClick={handleDemoLogin}
                  disabled={loading}
                  sx={{
                    textTransform: 'none'
                  }}
                >
                  Preencher dados de exemplo
                </Button>

              </Box>
            </form>
          </CardContent>
        </Card>

        {/* Informações do Sistema */}
        <Box sx={{ mt: 3, textAlign: 'center' }}>
          <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.8)' }}>
            Sistema de gerenciamento de ponto eletrônico
          </Typography>
          <Typography variant="caption" sx={{ color: 'rgba(255, 255, 255, 0.6)', display: 'block', mt: 1 }}>
            Versão 1.0 - © 2026
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default Login;