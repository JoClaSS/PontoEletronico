import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Container,
  Divider,
  TextField,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton
} from '@mui/material';
import {
  Login as LoginIcon,
  Security as SecurityIcon,
  Person as PersonIcon,
  Lock as LockIcon,
  Visibility,
  VisibilityOff
} from '@mui/icons-material';
import { useKeycloak } from '../../contexts/KeycloakContext';
import keycloakService from '../../services/keycloakService';

const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { refreshAuthState } = useKeycloak();
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!username.trim() || !password.trim()) {
      setError('Por favor, preencha todos os campos');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const success = await keycloakService.loginDirect(username.trim(), password);
      
      if (success) {
        // Atualiza o contexto de autenticação
        refreshAuthState();
      } else {
        setError('Usuário ou senha incorretos');
      }
    } catch (error) {
      console.error('Erro no login:', error);
      setError('Erro ao conectar com o servidor de autenticação');
    } finally {
      setLoading(false);
    }
  };

  const handleTogglePassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <Container maxWidth="sm">
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        minHeight="100vh"
        gap={3}
      >
        <Card 
          elevation={8}
          sx={{ 
            width: '100%', 
            borderRadius: 3,
            overflow: 'visible'
          }}
        >
          <CardContent sx={{ p: 4 }}>
            {/* Header */}
            <Box textAlign="center" mb={4}>
              <SecurityIcon 
                sx={{ 
                  fontSize: 64, 
                  color: 'primary.main',
                  mb: 2
                }} 
              />
              <Typography 
                variant="h4" 
                component="h1" 
                fontWeight="bold"
                color="primary"
                gutterBottom
              >
                Ponto Eletrônico
              </Typography>
              <Typography 
                variant="subtitle1" 
                color="text.secondary"
                sx={{ mb: 2 }}
              >
                Sistema de Controle de Ponto
              </Typography>
              
              <Divider sx={{ my: 3 }} />
            </Box>

            {/* Erro */}
            {error && (
              <Alert 
                severity="error" 
                sx={{ mb: 3 }}
                onClose={() => setError('')}
              >
                {error}
              </Alert>
            )}

            {/* Form */}
            <Box 
              component="form" 
              onSubmit={handleSubmit}
              sx={{ width: '100%' }}
            >
              <TextField
                fullWidth
                label="Usuário ou E-mail"
                variant="outlined"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
                required
                sx={{ mb: 3 }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <PersonIcon color="action" />
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                fullWidth
                label="Senha"
                type={showPassword ? 'text' : 'password'}
                variant="outlined"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
                required
                sx={{ mb: 4 }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockIcon color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleTogglePassword}
                        edge="end"
                        disabled={loading}
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              {/* Login Button */}
              <Button
                type="submit"
                variant="contained"
                size="large"
                fullWidth
                disabled={loading}
                sx={{
                  py: 1.5,
                  fontSize: '1.1rem',
                  fontWeight: 'bold',
                  borderRadius: 2,
                  textTransform: 'none',
                  boxShadow: 3,
                  '&:hover': {
                    boxShadow: 6,
                  }
                }}
              >
                {loading ? (
                  <CircularProgress size={24} color="inherit" sx={{ mr: 1 }} />
                ) : (
                  <LoginIcon sx={{ mr: 1 }} />
                )}
                {loading ? 'Entrando...' : 'Entrar no Sistema'}
              </Button>

              {/* Forgot Password Link */}
              <Box textAlign="center" mt={3}>
                <Button
                  variant="text"
                  size="small"
                  onClick={() => keycloakService.openResetPassword()}
                  sx={{
                    textTransform: 'none',
                    fontSize: '0.9rem',
                    color: 'primary.main',
                    '&:hover': {
                      backgroundColor: 'transparent',
                      textDecoration: 'underline'
                    }
                  }}
                  disabled={loading}
                >
                  Esqueceu a senha?
                </Button>
              </Box>
            </Box>

            {/* Footer */}
            <Box textAlign="center" mt={4}>
              <Typography 
                variant="caption" 
                color="text.secondary"
                display="block"
              >
                Sistema protegido por autenticação Keycloak
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default Login;