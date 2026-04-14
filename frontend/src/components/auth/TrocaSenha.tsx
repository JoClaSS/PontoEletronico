import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Alert,
  InputAdornment,
  IconButton
} from '@mui/material';
import {
  Lock as LockIcon,
  Visibility,
  VisibilityOff,
  Security as SecurityIcon
} from '@mui/icons-material';
import authService from '../../services/authService';

interface TrocaSenhaProps {
  onSucesso: () => void;
}

export interface TrocaSenhaRequest {
  senhaAtual: string;
  novaSenha: string;
  confirmarSenha: string;
}

const TrocaSenha: React.FC<TrocaSenhaProps> = ({ onSucesso }) => {
  const [formData, setFormData] = useState({
    senhaAtual: '',
    novaSenha: '',
    confirmarSenha: ''
  });

  const [showPasswords, setShowPasswords] = useState({
    atual: false,
    nova: false,
    confirmar: false
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleInputChange = (field: keyof typeof formData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: event.target.value
    }));
    setError(''); // Limpa erro quando usuário digita
  };

  const togglePasswordVisibility = (field: keyof typeof showPasswords) => {
    setShowPasswords(prev => ({
      ...prev,
      [field]: !prev[field]
    }));
  };

  const validateForm = (): string | null => {
    if (!formData.senhaAtual.trim()) {
      return 'Senha atual é obrigatória';
    }

    if (!formData.novaSenha.trim()) {
      return 'Nova senha é obrigatória';
    }

    if (formData.novaSenha.length < 8) {
      return 'Nova senha deve ter no mínimo 8 caracteres';
    }

    if (!/^(?=.*[a-zA-Z])(?=.*\d).+$/.test(formData.novaSenha)) {
      return 'Nova senha deve conter pelo menos uma letra e um número';
    }

    if (formData.novaSenha !== formData.confirmarSenha) {
      return 'Nova senha e confirmação não coincidem';
    }

    return null;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setError('');

    try {
      await authService.trocarSenha(
        formData.senhaAtual,
        formData.novaSenha,
        formData.confirmarSenha
      );

      onSucesso();
    } catch (error: any) {
      setError(error.message || 'Erro ao alterar senha');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      minHeight="100vh"
      sx={{ bgcolor: '#f5f5f5', p: 2 }}
    >
      <Card elevation={8} sx={{ width: '100%', maxWidth: 500 }}>
        <CardContent sx={{ p: 4 }}>
          <Box textAlign="center" mb={4}>
            <SecurityIcon sx={{ fontSize: 64, color: 'warning.main', mb: 2 }} />
            <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
              Redefinição de Senha
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              Esta é sua primeira vez no sistema. Por segurança, você deve definir uma nova senha.
            </Typography>
          </Box>

          <Alert severity="info" sx={{ mb: 3 }}>
            <strong>Requisitos da nova senha:</strong>
            <br />• Mínimo 8 caracteres
            <br />• Pelo menos uma letra
            <br />• Pelo menos um número
          </Alert>

          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Senha Atual"
              type={showPasswords.atual ? 'text' : 'password'}
              value={formData.senhaAtual}
              onChange={handleInputChange('senhaAtual')}
              margin="normal"
              variant="outlined"
              required
              disabled={loading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => togglePasswordVisibility('atual')}
                      edge="end"
                      disabled={loading}
                    >
                      {showPasswords.atual ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 2 }}
            />

            <TextField
              fullWidth
              label="Nova Senha"
              type={showPasswords.nova ? 'text' : 'password'}
              value={formData.novaSenha}
              onChange={handleInputChange('novaSenha')}
              margin="normal"
              variant="outlined"
              required
              disabled={loading}
              helperText="Mínimo 8 caracteres, com letras e números"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => togglePasswordVisibility('nova')}
                      edge="end"
                      disabled={loading}
                    >
                      {showPasswords.nova ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 2 }}
            />

            <TextField
              fullWidth
              label="Confirmar Nova Senha"
              type={showPasswords.confirmar ? 'text' : 'password'}
              value={formData.confirmarSenha}
              onChange={handleInputChange('confirmarSenha')}
              margin="normal"
              variant="outlined"
              required
              disabled={loading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => togglePasswordVisibility('confirmar')}
                      edge="end"
                      disabled={loading}
                    >
                      {showPasswords.confirmar ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 3 }}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{
                py: 1.5,
                fontSize: '1.1rem',
                fontWeight: 'bold'
              }}
            >
              {loading ? 'Alterando...' : 'Alterar Senha'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
};

export default TrocaSenha;