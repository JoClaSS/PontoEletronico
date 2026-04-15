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
  Person as PersonIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { useAuth } from '../../contexts/AuthContext';
import { TipoPonto } from '../../types';
import type { TipoPonto as TipoPontoType } from '../../types';
import { apiService } from '../../services/apiService';

const RegisterPoint: React.FC = () => {
  const { selectedUser, setSelectedUser, usuarios, setUsuarios, notifyPontosUpdate, pontosUpdateTrigger } = useAppContext();
  const { useUsuarios, usePontos } = useApi();
  const { user, isAdmin, isFuncionario } = useAuth();
  const usuariosHook = useUsuarios();
  const pontosHook = usePontos(selectedUser?.id);

  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  const [currentTime, setCurrentTime] = useState(new Date());
  const [horarioPermitido, setHorarioPermitido] = useState({ checkin: '08:00', checkout: '18:00' });

  // Carrega configurações de horário permitido
  useEffect(() => {
    const carregarHorarios = async () => {
      try {
        const config = await apiService.getConfiguracoes();
        setHorarioPermitido({
          checkin: config.horarioCheckin || '08:00',
          checkout: config.horarioCheckout || '18:00'
        });
      } catch (error) {
        console.log('Erro ao carregar horários - usando padrão:', error);
      }
    };
    carregarHorarios();
  }, []);

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
      // Se for admin, carrega apenas funcionários para seleção
      if (isAdmin()) {
        usuariosHook.loadFuncionarios();
      } else {
        usuariosHook.loadUsuarios();
      }
    }
    
    // Se for funcionário, encontra seu próprio usuário e seleciona automaticamente
    if (isFuncionario() && user && usuarios.length > 0) {
      const usuarioLogado = usuarios.find(u => u.email === user.email);
      if (usuarioLogado && !selectedUser) {
        setSelectedUser(usuarioLogado);
      }
    }
  }, [usuarios.length, user, isAdmin, isFuncionario]);

  // Atualiza lista de usuários no contexto quando carregados
  useEffect(() => {
    if (usuariosHook.data) {
      setUsuarios(usuariosHook.data);
    }
  }, [usuariosHook.data, setUsuarios]);

  // Carrega pontos do usuário quando selecionado
  useEffect(() => {
    console.log('[RegisterPoint] useEffect pontos - selectedUser:', selectedUser?.id);
    if (selectedUser?.id) {
      const hoje = format(new Date(), 'yyyy-MM-dd');
      console.log('[RegisterPoint] Carregando pontos para data:', hoje);
      pontosHook.loadPontosPorData(selectedUser.id, hoje);
    }
  }, [selectedUser]);

  // Escuta notificações de atualizações de pontos e recarrega dados
  useEffect(() => {
    if (pontosUpdateTrigger > 0 && selectedUser?.id) {
      console.log('[RegisterPoint] Recebida notificação de atualização de pontos, recarregando...');
      const hoje = format(new Date(), 'yyyy-MM-dd');
      pontosHook.loadPontosPorData(selectedUser.id, hoje);
    }
  }, [pontosUpdateTrigger, selectedUser?.id]);

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
        
        // Notifica outros componentes que pontos foram atualizados
        notifyPontosUpdate();
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

            {/* Horário Atual */}
            <Typography variant="h3" color="primary" align="center" sx={{ my: 3 }}>
              {format(currentTime, 'HH:mm:ss', { locale: ptBR })}
            </Typography>

            <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
              {format(currentTime, "EEEE, dd 'de' MMMM 'de' yyyy", { locale: ptBR })}
            </Typography>

            {/* Informações dos Horários Permitidos */}
            <Alert severity="info" sx={{ mb: 3 }}>
              <Typography variant="body2">
                <strong>Horários permitidos para registro:</strong> {horarioPermitido.checkin} às {horarioPermitido.checkout}
                <br />
              </Typography>
            </Alert>

            {/* Seleção de Usuário */}
            {isAdmin() ? (
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
                  {usuarios.filter(user => user.ativo !== false).map((user) => (
                    <MenuItem key={user.id} value={user.id}>
                      {user.nome} ({user.email})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            ) : (
              // Para funcionários, mostra apenas informação read-only
              <Box sx={{ display: 'flex', alignItems: 'center', p: 2, bgcolor: 'grey.50', borderRadius: 1, mb: 3 }}>
                <PersonIcon sx={{ mr: 2, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h6" color="primary.main">
                    {user?.nome || 'Usuário'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {user?.email}
                  </Typography>
                </Box>
              </Box>
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
              Frequência de Hoje
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
                      <React.Fragment key={`ponto-${ponto.id || index}-${index}`}>
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
                          />
                        </ListItem>
                        {index < pontosHook.data.length - 1 && <Divider />}
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