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
  PersonAdd as PersonAddIcon
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
    <Box sx={{ flexGrow: 1 }}>
      {/* AppBar */}
      <AppBar position="static">
        <Toolbar>
          {/* Logo/Nome do Sistema */}
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Ponto Eletrônico
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
                backgroundColor: isCurrentPath('/') ? 'rgba(255,255,255,0.1)' : 'transparent'
              }}
            >
              Home
            </Button>

            {/* Botão Visualizar Pontos */}
            <Button
              color="inherit"
              startIcon={<ScheduleIcon />}
              onClick={() => handleNavigation('/pontos')}
              variant={isCurrentPath('/pontos') ? 'outlined' : 'text'}
              sx={{
                backgroundColor: isCurrentPath('/pontos') ? 'rgba(255,255,255,0.1)' : 'transparent'
              }}
            >
              Pontos
            </Button>

            {/* Botão Usuários */}
            <Button
              color="inherit"
              startIcon={<PersonAddIcon />}
              onClick={() => handleNavigation('/usuarios')}
              variant={isCurrentPath('/usuarios') || isCurrentPath('/usuarios/cadastrar') ? 'outlined' : 'text'}
              sx={{
                backgroundColor: isCurrentPath('/usuarios') || isCurrentPath('/usuarios/cadastrar') ? 'rgba(255,255,255,0.1)' : 'transparent'
              }}
            >
              Usuários
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
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Outlet />
      </Container>
    </Box>
  );
};


export default Layout;