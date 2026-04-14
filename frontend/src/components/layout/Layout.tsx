import React, { useState, useEffect } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  Container,
  Chip,
  Avatar
} from '@mui/material';
import {
  Home as HomeIcon,
  Schedule as ScheduleIcon,
  PersonAdd as PersonAddIcon,
  Assignment as AssignmentIcon,
  Logout as LogoutIcon,
  AdminPanelSettings as AdminIcon,
  Settings as SettingsIcon
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { apiService } from '../../services/apiService';
import type { ConfiguracaoEmpresa } from '../../types';

const Layout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout, isAdmin } = useAuth();
  const [logoEmpresa, setLogoEmpresa] = useState<string | null>(null);
  const [nomeEmpresa, setNomeEmpresa] = useState<string>('Mundial Ciclo');

  // Carregar configurações da empresa para pegar a logo
  useEffect(() => {
    const carregarLogoEmpresa = async () => {
      try {
        const configuracoes = await apiService.getConfiguracoes();
        if (configuracoes.fotoEmpresa) {
          setLogoEmpresa(configuracoes.fotoEmpresa);
        }
        if (configuracoes.nomeEmpresa) {
          setNomeEmpresa(configuracoes.nomeEmpresa);
        }
      } catch (error) {
        console.log('Não foi possível carregar logo da empresa:', error);
      }
    };

    carregarLogoEmpresa();
  }, []);

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
          <Box 
            sx={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: 1,
              flexGrow: 1 
            }}
          >
            {/* Logo da Empresa */}
            {logoEmpresa && (
              <Avatar
                src={logoEmpresa}
                alt={`Logo ${nomeEmpresa}`}
                sx={{
                  width: { xs: 32, sm: 36, md: 40 },
                  height: { xs: 32, sm: 36, md: 40 },
                  border: '2px solid rgba(255,255,255,0.2)'
                }}
              />
            )}
            
            {/* Texto do Sistema */}
            <Typography 
              variant="h6" 
              component="div" 
              sx={{ 
                fontSize: { xs: '16px', sm: '18px', md: '20px' }
              }}
            >
              {/* Desktop/Tablet: Mostra nome completo */}
              <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Ponto Eletrônico</Box>
              
              {/* Mobile: Mostra logo OU texto "Ponto" */}
              <Box sx={{ display: { xs: logoEmpresa ? 'none' : 'inline', sm: 'none' } }}>Ponto</Box>
            </Typography>
          </Box>

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

            {/* Botão Usuários - só para ADMIN e MASTER */}
            {isAdmin() && (
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
            )}

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

            {/* Botão Configurações - só para ADMIN e MASTER */}
            {isAdmin() && (
              <Button
                color="inherit"
                startIcon={<SettingsIcon />}
                onClick={() => handleNavigation('/configuracoes')}
                variant={isCurrentPath('/configuracoes') ? 'outlined' : 'text'}
                sx={{
                  backgroundColor: isCurrentPath('/configuracoes') ? 'rgba(255,255,255,0.1)' : 'transparent',
                  fontSize: { xs: '12px', sm: '14px', md: '16px' },
                  minWidth: { xs: 'auto', sm: '64px' },
                  px: { xs: 1, sm: 2 }
                }}
              >
                <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Configurações</Box>
              </Button>
            )}

            {/* Usuário Logado e Status de Admin */}
            {user && (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, ml: 2 }}>
                {/* Nome do usuário */}
                <Typography 
                  variant="body2" 
                  sx={{ 
                    display: { xs: 'none', md: 'block' },
                    color: 'rgba(255, 255, 255, 0.9)'
                  }}
                >
                  Olá, {user.nome.split(' ')[0]}
                </Typography>
                
                {/* Badge de Admin */}
                {isAdmin() && (
                  <Chip
                    icon={<AdminIcon />}
                    label="Admin"
                    size="small"
                    sx={{
                      backgroundColor: 'rgba(255, 193, 7, 0.2)',
                      color: '#ffc107',
                      border: '1px solid rgba(255, 193, 7, 0.3)',
                      display: { xs: 'none', sm: 'flex' }
                    }}
                  />
                )}
                
                {/* Botão de Logout */}
                <Button
                  color="inherit"
                  startIcon={<LogoutIcon />}
                  onClick={logout}
                  variant="outlined"
                  size="small"
                  sx={{
                    borderColor: 'rgba(255, 255, 255, 0.3)',
                    '&:hover': {
                      borderColor: 'rgba(255, 255, 255, 0.7)',
                      backgroundColor: 'rgba(255, 255, 255, 0.1)'
                    },
                    fontSize: { xs: '12px', sm: '14px' },
                    minWidth: { xs: 'auto', sm: '64px' },
                    px: { xs: 1, sm: 2 }
                  }}
                >
                  <Box sx={{ display: { xs: 'none', sm: 'inline' } }}>Sair</Box>
                </Button>
              </Box>
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