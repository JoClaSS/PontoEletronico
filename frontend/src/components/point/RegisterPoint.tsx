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
  const { selectedUser, setSelectedUser, usuarios, setUsuarios, selectedBackend } = useAppContext();
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
  }, [selectedBackend]);

  // Atualiza lista de usuários no contexto quando carregados
  useEffect(() => {
    if (usuariosHook.data) {
      setUsuarios(usuariosHook.data);
    }
  }, [usuariosHook.data, setUsuarios]);

  // Carrega pontos do usuário quando selecionado
  useEffect(() => {
    if (selectedUser?.id) {
      pontosHook.loadPontosDeHoje(selectedUser.id);
    }
  }, [selectedUser, selectedBackend]);

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
          message: `Ponto registrado com sucesso! Tipo: ${novoPonto.tipo}`,
          severity: 'success'
        });
      }
    } catch (error) {
      setSnackbar({
        open: true,
        message: 'Erro ao registrar ponto',
        severity: 'error'
      });
    }
  };

  const getProximoTipoPonto = (): string => {
    if (!pontosHook.data || pontosHook.data.length === 0) {
      return 'ENTRADA';
    }

    const ultimoPonto = pontosHook.data[pontosHook.data.length - 1];
    
    switch (ultimoPonto.tipo) {
      case TipoPonto.ENTRADA:
        return 'SAÍDA ALMOÇO';
      case TipoPonto.SAIDA_ALMOCO:
        return 'RETORNO ALMOÇO';
      case TipoPonto.RETORNO_ALMOCO:
        return 'SAÍDA';
      case TipoPonto.SAIDA:
        return 'ENTRADA';
      default:
        return 'ENTRADA';
    }
  };

  const getChipColor = (tipo: TipoPontoType) => {
    if (!tipo) return 'default';
    
    switch (tipo) {
      case TipoPonto.ENTRADA:
        return 'success';
      case TipoPonto.SAIDA_ALMOCO:
        return 'warning';
      case TipoPonto.RETORNO_ALMOCO:
        return 'info';
      case TipoPonto.SAIDA:
        return 'error';
      default:
        return 'default';
    }
  };

  return (
    <Box>
      {/* Título */}
      <Typography variant="h4" component="h1" gutterBottom>
        Registrar Ponto
      </Typography>

      {/* Backend Atual */}
      <Alert severity="info" sx={{ mb: 3 }}>
        Conectado ao backend: <strong>{selectedBackend}</strong>
      </Alert>

      <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
        {/* Card Principal - Registro de Ponto */}
        <Card sx={{ flex: 1, minWidth: 400 }}>
          <CardContent>
            <Typography variant="h6" component="h2" gutterBottom>
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
            <Typography variant="h6" component="h2" gutterBottom>
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
                                  label={ponto.tipo ? ponto.tipo.replace('_', ' ') : 'N/A'}
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