import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Tabs,
  Tab,
  Stack
} from '@mui/material';
import type { SelectChangeEvent } from '@mui/material';
import {
  Person as PersonIcon,
  Assessment as ReportIcon,
  Schedule as ScheduleIcon
} from '@mui/icons-material';
import { format, startOfWeek, endOfWeek, startOfMonth, endOfMonth } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { useKeycloak } from '../../contexts/KeycloakContext';
import type { FiltrosPontos, PontoAgrupado } from '../../types';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          {children}
        </Box>
      )}
    </div>
  );
}

const ViewPoints: React.FC = () => {
  const { selectedUser, setSelectedUser, usuarios, setUsuarios } = useAppContext();
  const { useUsuarios, usePontos, useRelatorios } = useApi();
  const { userProfile, isAdmin, isMaster } = useKeycloak();
  const usuariosHook = useUsuarios();
  const pontosHook = usePontos(selectedUser?.id);
  const relatoriosHook = useRelatorios();

  const [tabValue, setTabValue] = useState(0);
  const [filtros, setFiltros] = useState<FiltrosPontos>({
    dataInicio: format(new Date(), 'yyyy-MM-dd'),
    dataFim: format(new Date(), 'yyyy-MM-dd')
  });

  // Carrega usuários na inicialização
  useEffect(() => {
    if (usuarios.length === 0) {
      usuariosHook.loadUsuarios();
    }
    
    // Se for funcionário, encontra seu próprio usuário e seleciona automaticamente
    if (!isAdmin() && !isMaster() && userProfile && usuarios.length > 0) {
      const usuarioLogado = usuarios.find(u => u.email === userProfile.email);
      if (usuarioLogado && !selectedUser) {
        setSelectedUser(usuarioLogado);
      }
    }
  }, [usuarios.length, userProfile, isAdmin, isMaster]);

  // Atualiza lista de usuários no contexto quando carregados
  useEffect(() => {
    if (usuariosHook.data) {
      setUsuarios(usuariosHook.data);
    }
  }, [usuariosHook.data, setUsuarios]);

  const handleUserChange = (event: SelectChangeEvent<string>) => {
    const userId = event.target.value;
    const user = usuarios.find(u => u.id === userId);
    setSelectedUser(user || null);
  };

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleFiltroChange = (field: keyof FiltrosPontos) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFiltros(prev => ({
      ...prev,
      [field]: event.target.value
    }));
  };

  const aplicarFiltroRapido = (tipo: 'hoje' | 'semana' | 'mes') => {
    const hoje = new Date();
    
    switch (tipo) {
      case 'hoje':
        setFiltros({
          dataInicio: format(hoje, 'yyyy-MM-dd'),
          dataFim: format(hoje, 'yyyy-MM-dd')
        });
        break;
      case 'semana':
        setFiltros({
          dataInicio: format(startOfWeek(hoje, { locale: ptBR }), 'yyyy-MM-dd'),
          dataFim: format(endOfWeek(hoje, { locale: ptBR }), 'yyyy-MM-dd')
        });
        break;
      case 'mes':
        setFiltros({
          dataInicio: format(startOfMonth(hoje), 'yyyy-MM-dd'),
          dataFim: format(endOfMonth(hoje), 'yyyy-MM-dd')
        });
        break;
    }
  };

  const buscarPontos = useCallback(() => {
    if (!selectedUser || !filtros.dataInicio || !filtros.dataFim) return;
    
    if (filtros.dataInicio === filtros.dataFim) {
      // Pontos de um dia específico - usa a data dos filtros
      pontosHook.loadPontosPorData?.(selectedUser.id, filtros.dataInicio);
    } else {
      // Pontos de um período
      pontosHook.loadPontosPorPeriodo?.(selectedUser.id, filtros);
    }
  }, [selectedUser?.id, filtros.dataInicio, filtros.dataFim]);

  // Carrega pontos quando usuário ou filtros mudarem
  useEffect(() => {
    if (selectedUser) {
      buscarPontos();
    }
  }, [selectedUser, buscarPontos]);

  const gerarRelatorio = () => {
    if (!selectedUser || !filtros.dataInicio || !filtros.dataFim) return;
    
    relatoriosHook.loadRelatorioHoras(selectedUser.id, filtros);
  };

  // Função para agrupar pontos por data
  const agruparPontosPorData = (): PontoAgrupado[] => {
    if (!pontosHook.data) return [];
    
    const grupos: Map<string, PontoAgrupado> = new Map();
    
    pontosHook.data.forEach(ponto => {
      const dataStr = format(new Date(ponto.dataHora), 'yyyy-MM-dd');
      const horaStr = format(new Date(ponto.dataHora), 'HH:mm');
      
      if (!grupos.has(dataStr)) {
        grupos.set(dataStr, {
          data: dataStr
        });
      }
      
      const grupo = grupos.get(dataStr)!;
      
      // Mapeia o tipo do ponto para a coluna correspondente
      const tipoUpper = String(ponto.tipo || '').toUpperCase();
      
      switch (tipoUpper) {
        case 'ENTRADA_1':
          grupo.entrada1 = horaStr;
          break;
        case 'SAIDA_1':  
          grupo.saida1 = horaStr;
          break;
        case 'ENTRADA_2':
          grupo.entrada2 = horaStr;
          break;
        case 'SAIDA_2':
          grupo.saida2 = horaStr;
          break;
        case 'ENTRADA_3':
          grupo.entrada3 = horaStr;
          break;
        case 'SAIDA_3':
          grupo.saida3 = horaStr;
          break;
      }
      
      // Adiciona observação se houver
      if (ponto.observacao && !grupo.observacao) {
        grupo.observacao = ponto.observacao;
      }
    });
    
    // Converte para array e ordena por data (mais recente primeiro)
    return Array.from(grupos.values()).sort((a, b) => 
      new Date(b.data).getTime() - new Date(a.data).getTime()
    );
  };

  return (
    <Box>
      {/* Título */}
      <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'black' }}>
        Visualizar Frequência
      </Typography>

      {/* Seleção de Usuário */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          {(isAdmin() || isMaster()) ? (
            <FormControl fullWidth>
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
          ) : (
            // Para funcionários, mostra apenas informação read-only
            <Box sx={{ display: 'flex', alignItems: 'center', p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
              <PersonIcon sx={{ mr: 2, color: 'primary.main' }} />
              <Box>
                <Typography variant="h6" color="primary.main">
                  {userProfile?.firstName || userProfile?.username || 'Usuário'}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {userProfile?.email}
                </Typography>
              </Box>
            </Box>
          )}
        </CardContent>
      </Card>

      {selectedUser && (
        <Card>
          <CardContent>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="visualização de frequência">
              <Tab label="Lista de Registros" icon={<ScheduleIcon />} />
              <Tab label="Relatório de Horas" icon={<ReportIcon />} />
            </Tabs>

            {/* Aba: Lista de Registros */}
            <TabPanel value={tabValue} index={0}>
              {/* Filtros */}
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
                <TextField
                  label="Data Início"
                  type="date"
                  value={filtros.dataInicio}
                  onChange={handleFiltroChange('dataInicio')}
                  InputLabelProps={{ shrink: true }}
                  sx={{ minWidth: 200 }}
                />
                <TextField
                  label="Data Fim"
                  type="date"
                  value={filtros.dataFim}
                  onChange={handleFiltroChange('dataFim')}
                  InputLabelProps={{ shrink: true }}
                  sx={{ minWidth: 200 }}
                />
                <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', alignItems: 'center' }}>
                  <Button variant="outlined" onClick={() => aplicarFiltroRapido('hoje')}>
                    Hoje
                  </Button>
                  <Button variant="outlined" onClick={() => aplicarFiltroRapido('semana')}>
                    Esta Semana
                  </Button>
                  <Button variant="outlined" onClick={() => aplicarFiltroRapido('mes')}>
                    Este Mês
                  </Button>
                  <Button variant="contained" onClick={buscarPontos}>
                    Buscar
                  </Button>
                </Box>
              </Stack>

              {/* Tabela de Pontos Agrupada por Data */}
              {pontosHook.loading ? (
                <Typography>Carregando...</Typography>
              ) : pontosHook.data && pontosHook.data.length > 0 ? (
                <TableContainer component={Paper}>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell><strong>Data</strong></TableCell>
                        <TableCell align="center"><strong>Entrada 1</strong></TableCell>
                        <TableCell align="center"><strong>Saída 1</strong></TableCell>
                        <TableCell align="center"><strong>Entrada 2</strong></TableCell>
                        <TableCell align="center"><strong>Saída 2</strong></TableCell>
                        <TableCell align="center"><strong>Entrada 3</strong></TableCell>
                        <TableCell align="center"><strong>Saída 3</strong></TableCell>
                        <TableCell><strong>Observação</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {agruparPontosPorData().map((registro) => (
                        <TableRow key={registro.data}>
                          <TableCell>
                            <strong>{format(new Date(registro.data), "dd/MM/yyyy", { locale: ptBR })}</strong>
                          </TableCell>
                          <TableCell align="center">
                            {registro.entrada1 ? (
                              <Chip label={registro.entrada1} color="success" size="small" />
                            ) : (
                              <span style={{ color: '#999' }}>-</span>
                            )}
                          </TableCell>
                          <TableCell align="center">
                            {registro.saida1 ? (
                              <Chip label={registro.saida1} color="warning" size="small" />
                            ) : (
                              <span style={{ color: '#999' }}>-</span>
                            )}
                          </TableCell>
                          <TableCell align="center">
                            {registro.entrada2 ? (
                              <Chip label={registro.entrada2} color="success" size="small" />
                            ) : (
                              <span style={{ color: '#999' }}>-</span>
                            )}
                          </TableCell>
                          <TableCell align="center">
                            {registro.saida2 ? (
                              <Chip label={registro.saida2} color="warning" size="small" />
                            ) : (
                              <span style={{ color: '#999' }}>-</span>
                            )}
                          </TableCell>
                          <TableCell align="center">
                            {registro.entrada3 ? (
                              <Chip label={registro.entrada3} color="success" size="small" />
                            ) : (
                              <span style={{ color: '#999' }}>-</span>
                            )}
                          </TableCell>
                          <TableCell align="center">
                            {registro.saida3 ? (
                              <Chip label={registro.saida3} color="error" size="small" />
                            ) : (
                              <span style={{ color: '#999' }}>-</span>
                            )}
                          </TableCell>
                          <TableCell>{registro.observacao || '-'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <Typography color="text.secondary">
                  Nenhum ponto encontrado no período selecionado
                </Typography>
              )}
            </TabPanel>

            {/* Aba: Relatório de Horas */}
            <TabPanel value={tabValue} index={1}>
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
                <TextField
                  label="Data Início"
                  type="date"
                  value={filtros.dataInicio}
                  onChange={handleFiltroChange('dataInicio')}
                  InputLabelProps={{ shrink: true }}
                  sx={{ minWidth: 200 }}
                />
                <TextField
                  label="Data Fim"
                  type="date"
                  value={filtros.dataFim}
                  onChange={handleFiltroChange('dataFim')}
                  InputLabelProps={{ shrink: true }}
                  sx={{ minWidth: 200 }}
                />
                <Button 
                  variant="contained" 
                  onClick={gerarRelatorio}
                  startIcon={<ReportIcon />}
                  sx={{ minWidth: 180 }}
                >
                  Gerar Relatório
                </Button>
              </Stack>

              {relatoriosHook.loading ? (
                <Typography>Gerando relatório...</Typography>
              ) : relatoriosHook.data ? (
                <Card>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      Relatório de Horas
                    </Typography>
                    <Stack spacing={1}>
                      <Typography><strong>Período:</strong> {filtros.dataInicio} a {filtros.dataFim}</Typography>
                      <Typography><strong>Total de Horas:</strong> {relatoriosHook.data.totalHoras}h</Typography>
                      <Typography><strong>Dias Trabalhados:</strong> {relatoriosHook.data.diasTrabalhados}</Typography>
                    </Stack>
                  </CardContent>
                </Card>
              ) : (
                <Typography color="text.secondary">
                  Clique em "Gerar Relatório" para ver o resumo de horas
                </Typography>
              )}
            </TabPanel>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default ViewPoints;