import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  CircularProgress,
  Container
} from '@mui/material';
import {
  Home as HomeIcon,
  Schedule as ScheduleIcon,
  PersonAdd as PersonAddIcon,
  Assignment as AssignmentIcon
} from '@mui/icons-material';
import { useAppContext } from '../../contexts/AppContext';

const Layout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { loading } = useAppContext();

  const handleNavigation = (path: string) => {
    navigate(path);
  };

  const isCurrentPath = (path: string) => location.pathname === path;

  return (
    <Box 
      sx={{ 
        flexGrow: 1, 
        backgroundColor: '#1e3a8a', // azul marinho
        minHeight: '100vh' 
      }}
    >
      {/* AppBar */}
      <AppBar 
        position="static"
        sx={{
          backgroundColor: '#1e40af' // azul um pouco mais claro que o background
        }}
      >
        <Toolbar>
          {/* Logo/Nome do Sistema */}
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ 
              flexGrow: 1,
              fontSize: { xs: '16px', sm: '18px', md: '20px' }
            }}
          >
            <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Ponto Eletrônico</Box>
            <Box sx={{ display: { xs: 'inline', sm: 'none' } }}>Ponto</Box>
          </Typography>

          {/* Navegação */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            {/* Botão Home */}
            <Button
              color="inherit"
              startIcon={<HomeIcon />}
              onClick={() => handleNavigation('/')}
              variant={isCurrentPath('/') ? 'outlined' : 'text'}
              sx={{
                backgroundColor: isCurrentPath('/') ? 'rgba(255,255,255,0.1)' : 'transparent',
                fontSize: { xs: '12px', sm: '14px', md: '16px' },
                minWidth: { xs: 'auto', sm: '64px' },
                px: { xs: 1, sm: 2 }
              }}
            >
              <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Home</Box>
            </Button>

            {/* Botão Visualizar Frequência */}
            <Button
              color="inherit"
              startIcon={<ScheduleIcon />}
              onClick={() => handleNavigation('/frequencia')}
              variant={isCurrentPath('/frequencia') ? 'outlined' : 'text'}
              sx={{
                backgroundColor: isCurrentPath('/frequencia') ? 'rgba(255,255,255,0.1)' : 'transparent',
                fontSize: { xs: '12px', sm: '14px', md: '16px' },
                minWidth: { xs: 'auto', sm: '64px' },
                px: { xs: 1, sm: 2 }
              }}
            >
              <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Frequência</Box>
            </Button>

            {/* Botão Usuários */}
            <Button
              color="inherit"
              startIcon={<PersonAddIcon />}
              onClick={() => handleNavigation('/usuarios')}
              variant={isCurrentPath('/usuarios') || isCurrentPath('/usuarios/cadastrar') ? 'outlined' : 'text'}
              sx={{
                backgroundColor: isCurrentPath('/usuarios') || isCurrentPath('/usuarios/cadastrar') ? 'rgba(255,255,255,0.1)' : 'transparent',
                fontSize: { xs: '12px', sm: '14px', md: '16px' },
                minWidth: { xs: 'auto', sm: '64px' },
                px: { xs: 1, sm: 2 }
              }}
            >
              <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Usuários</Box>
            </Button>

            {/* Botão Solicitações */}
            <Button
              color="inherit"
              startIcon={<AssignmentIcon />}
              onClick={() => handleNavigation('/solicitacoes')}
              variant={isCurrentPath('/solicitacoes') ? 'outlined' : 'text'}
              sx={{
                backgroundColor: isCurrentPath('/solicitacoes') ? 'rgba(255,255,255,0.1)' : 'transparent',
                fontSize: { xs: '12px', sm: '14px', md: '16px' },
                minWidth: { xs: 'auto', sm: '64px' },
                px: { xs: 1, sm: 2 }
              }}
            >
              <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Solicitações</Box>
            </Button>

            {/* Indicador de Loading */}
            {loading && (
              <CircularProgress 
                size={24} 
                sx={{ color: 'white' }} 
              />
            )}
          </Box>
        </Toolbar>
      </AppBar>

      {/* Conteúdo Principal */}
      <Container 
        maxWidth="lg" 
        sx={{ 
          mt: { xs: 1, sm: 2, md: 4 }, 
          mb: { xs: 1, sm: 2, md: 4 },
          mx: { xs: 1, sm: 2, md: 'auto' },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderRadius: 2,
          p: { xs: 1, sm: 2, md: 4 },
          minHeight: 'calc(100vh - 200px)',
          width: { xs: 'calc(100% - 16px)', sm: 'calc(100% - 32px)', md: 'auto' }
        }}
      >
        <Outlet />
      </Container>
    </Box>
  );
};


export default Layout;