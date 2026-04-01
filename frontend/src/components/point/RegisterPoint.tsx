import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Snackbar,
  List,
  ListItem,
  ListItemText,
  Chip,
  Divider
} from '@mui/material';
import type { SelectChangeEvent } from '@mui/material';
import {
  AccessTime as ClockIcon,
  Person as PersonIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { TipoPonto } from '../../types';
import type { TipoPonto as TipoPontoType } from '../../types';

const RegisterPoint: React.FC = () => {
  const { selectedUser, setSelectedUser, usuarios, setUsuarios } = useAppContext();
  const { useUsuarios, usePontos } = useApi();
  const usuariosHook = useUsuarios();
  const pontosHook = usePontos(selectedUser?.id);

  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  const [currentTime, setCurrentTime] = useState(new Date());

  // Atualiza o horário a cada segundo
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  // Carrega usuários na inicialização
  useEffect(() => {
    if (usuarios.length === 0) {
      usuariosHook.loadUsuarios();
    }
  }, []);

  // Atualiza lista de usuários no contexto quando carregados
  useEffect(() => {
    if (usuariosHook.data) {
      setUsuarios(usuariosHook.data);
    }
  }, [usuariosHook.data, setUsuarios]);

  // Carrega pontos do usuário quando selecionado
  useEffect(() => {
    if (selectedUser?.id) {
      const hoje = format(new Date(), 'yyyy-MM-dd');
      pontosHook.loadPontosPorData(selectedUser.id, hoje);
    }
  }, [selectedUser]);

  const handleUserChange = (event: SelectChangeEvent<string>) => {
    const userId = event.target.value;
    const user = usuarios.find(u => u.id === userId);
    setSelectedUser(user || null);
  };

  const handleRegistrarPonto = async () => {
    console.log('[RegisterPoint] selectedUser:', selectedUser);
    
    if (!selectedUser) {
      setSnackbar({
        open: true,
        message: 'Selecione um usuário primeiro',
        severity: 'error'
      });
      return;
    }

    if (!selectedUser.id) {
      console.error('[RegisterPoint] Usuario selecionado sem ID:', selectedUser);
      setSnackbar({
        open: true,
        message: 'Usuário selecionado é inválido (sem ID)',
        severity: 'error'
      });
      return;
    }

    try {
      const pontoData = {
        usuarioId: selectedUser.id
      };
      
      console.log('[RegisterPoint] Enviando dados:', pontoData);

      const novoPonto = await pontosHook.registrarPonto(pontoData);

      if (novoPonto) {
        setSnackbar({
          open: true,
          message: `Ponto registrado com sucesso! Tipo: ${getTipoDisplayName(novoPonto.tipo)}`,
          severity: 'success'
        });
        
        // Força uma atualização manual da lista após o sucesso
        const hoje = format(new Date(), 'yyyy-MM-dd');
        pontosHook.loadPontosPorData?.(selectedUser.id, hoje);
      }
    } catch (error: any) {
      const errorMessage = error.userMessage || error.message || 'Erro ao registrar ponto';
      console.error('[RegisterPoint] Erro:', error);
      setSnackbar({
        open: true,
        message: errorMessage,
        severity: 'error'
      });
    }
  };

  const getProximoTipoPonto = (): string => {
    if (!pontosHook.data || pontosHook.data.length === 0) {
      return 'ENTRADA 1';
    }

    const ultimoPonto = pontosHook.data[pontosHook.data.length - 1];
    
    switch (ultimoPonto.tipo) {
      case TipoPonto.ENTRADA_1:
        return 'SAÍDA 1';
      case TipoPonto.SAIDA_1:
        return 'ENTRADA 2';
      case TipoPonto.ENTRADA_2:
        return 'SAÍDA 2';
      case TipoPonto.SAIDA_2:
        return 'ENTRADA 3';
      case TipoPonto.ENTRADA_3:
        return 'SAÍDA 3';
      case TipoPonto.SAIDA_3:
        return 'ENTRADA 1';
      default:
        return 'ENTRADA 1';
    }
  };

  const getChipColor = (tipo: TipoPontoType) => {
    if (!tipo) return 'default';
    
    switch (tipo) {
      case TipoPonto.ENTRADA_1:
        return 'success';
      case TipoPonto.SAIDA_1:
        return 'warning';
      case TipoPonto.ENTRADA_2:
        return 'success';
      case TipoPonto.SAIDA_2:
        return 'warning';
      case TipoPonto.ENTRADA_3:
        return 'success';
      case TipoPonto.SAIDA_3:
        return 'error';
      default:
        return 'default';
    }
  };

  const getTipoDisplayName = (tipo: TipoPontoType): string => {
    if (!tipo) return 'N/A';
    
    switch (tipo) {
      case TipoPonto.ENTRADA_1:
        return 'Entrada 1';
      case TipoPonto.SAIDA_1:
        return 'Saída 1';
      case TipoPonto.ENTRADA_2:
        return 'Entrada 2';
      case TipoPonto.SAIDA_2:
        return 'Saída 2';
      case TipoPonto.ENTRADA_3:
        return 'Entrada 3';
      case TipoPonto.SAIDA_3:
        return 'Saída 3';
      default:
        return String(tipo).replace(/_/g, ' ');
    }
  };

  return (
    <Box>
      {/* Título */}
      <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'black' }}>
        Registrar Ponto
      </Typography>

      <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
        {/* Card Principal - Registro de Ponto */}
        <Card sx={{ flex: 1, minWidth: 400 }}>
          <CardContent>
            <Typography variant="h6" component="h2" gutterBottom sx={{ color: 'black' }}>
              <ClockIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
              Registrar Ponto
            </Typography>

            {/* Horário Atual */}
            <Typography variant="h3" color="primary" align="center" sx={{ my: 3 }}>
              {format(currentTime, 'HH:mm:ss', { locale: ptBR })}
            </Typography>

            <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
              {format(currentTime, "EEEE, dd 'de' MMMM 'de' yyyy", { locale: ptBR })}
            </Typography>

            {/* Seleção de Usuário */}
            <FormControl fullWidth sx={{ mb: 3 }}>
              <InputLabel id="user-select-label">
                <PersonIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                Selecione o Usuário
              </InputLabel>
              <Select
                labelId="user-select-label"
                value={selectedUser?.id || ''}
                onChange={handleUserChange}
                label="Selecione o Usuário"
                disabled={usuariosHook.loading}
              >
                {usuarios.map((user) => (
                  <MenuItem key={user.id} value={user.id}>
                    {user.nome} ({user.email})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {/* Próximo Tipo de Ponto */}
            {selectedUser && (
              <Alert severity="info" sx={{ mb: 2 }}>
                Próximo registro: <strong>{getProximoTipoPonto()}</strong>
              </Alert>
            )}

            {/* Botão de Registro */}
            <Button
              fullWidth
              variant="contained"
              size="large"
              onClick={handleRegistrarPonto}
              disabled={!selectedUser || pontosHook.loading}
              sx={{ py: 2 }}
            >
              {pontosHook.loading ? 'Registrando...' : 'Registrar Ponto'}
            </Button>
          </CardContent>
        </Card>

        {/* Card Lateral - Pontos de Hoje */}
        <Card sx={{ width: 350 }}>
          <CardContent>
            <Typography variant="h6" component="h2" gutterBottom sx={{ color: 'black' }}>
              Pontos de Hoje
            </Typography>

            {selectedUser ? (
              <>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  {selectedUser.nome}
                </Typography>

                {pontosHook.loading ? (
                  <Typography>Carregando...</Typography>
                ) : pontosHook.data && pontosHook.data.length > 0 ? (
                  <List dense>
                    {pontosHook.data.map((ponto, index) => (
                      <React.Fragment key={ponto.id}>
                        <ListItem>
                          <ListItemText
                            primary={
                              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <Chip
                                  label={getTipoDisplayName(ponto.tipo)}
                                  color={getChipColor(ponto.tipo)}
                                  size="small"
                                />
                                <Typography variant="body2">
                                  {ponto.dataHora ? format(new Date(ponto.dataHora), 'HH:mm') : 'N/A'}
                                </Typography>
                              </Box>
                            }
                            secondary={ponto.observacao}
                          />
                        </ListItem>
                        {index < pontosHook.data!.length - 1 && <Divider />}
                      </React.Fragment>
                    ))}
                  </List>
                ) : (
                  <Typography color="text.secondary">
                    Nenhum ponto registrado hoje
                  </Typography>
                )}
              </>
            ) : (
              <Typography color="text.secondary">
                Selecione um usuário para ver os pontos
              </Typography>
            )}
          </CardContent>
        </Card>
      </Box>

      {/* Snackbar para mensagens */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
      >
        <Alert 
          severity={snackbar.severity}
          onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default RegisterPoint;