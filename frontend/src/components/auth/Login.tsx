import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Container,
  Divider
} from '@mui/material';
import {
  Login as LoginIcon,
  Security as SecurityIcon
} from '@mui/icons-material';
import { useKeycloak } from '../../contexts/KeycloakContext';

const Login: React.FC = () => {
  const { login } = useKeycloak();

  const handleLogin = () => {
    login();
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

            {/* Login Info */}
            <Box textAlign="center" mb={4}>
              <Typography 
                variant="h6" 
                gutterBottom
                color="text.primary"
              >
                Autenticação Segura
              </Typography>
              <Typography 
                variant="body2" 
                color="text.secondary"
                sx={{ mb: 3 }}
              >
                Entre com suas credenciais para acessar o sistema
              </Typography>
            </Box>

            {/* Login Button */}
            <Button
              onClick={handleLogin}
              variant="contained"
              size="large"
              startIcon={<LoginIcon />}
              fullWidth
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
              Entrar no Sistema
            </Button>

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