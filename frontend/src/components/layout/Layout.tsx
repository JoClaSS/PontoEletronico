import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  FormControl,
  Select,
  MenuItem,
  InputLabel,
  CircularProgress,
  Container
} from '@mui/material';
import type { SelectChangeEvent } from '@mui/material';
import {
  Home as HomeIcon,
  Schedule as ScheduleIcon,
  PersonAdd as PersonAddIcon,
  SwapHoriz as SwapIcon
} from '@mui/icons-material';
import { useAppContext } from '../../contexts/AppContext';
import { BACKEND_CONFIGS } from '../../types';
import type { Backend as BackendType } from '../../types';

const Layout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { selectedBackend, setSelectedBackend, loading } = useAppContext();

  const handleBackendChange = (event: SelectChangeEvent<string>) => {
    setSelectedBackend(event.target.value as BackendType);
  };

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

            {/* Seletor de Backend */}
            <FormControl variant="outlined" size="small" sx={{ minWidth: 180 }}>
              <InputLabel id="backend-select-label" sx={{ color: 'white' }}>
                Backend
              </InputLabel>
              <Select
                labelId="backend-select-label"
                value={selectedBackend}
                onChange={handleBackendChange}
                label="Backend"
                startAdornment={<SwapIcon sx={{ mr: 1 }} />}
                sx={{
                  color: 'white',
                  '.MuiOutlinedInput-notchedOutline': {
                    borderColor: 'rgba(255, 255, 255, 0.23)',
                  },
                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'white',
                  },
                  '&:hover .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'white',
                  },
                  '.MuiSvgIcon-root': {
                    color: 'white',
                  },
                }}
              >
                {Object.values(BACKEND_CONFIGS).map((config) => (
                  <MenuItem key={config.type} value={config.type}>
                    {config.name} ({config.port})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

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