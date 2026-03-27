import React, { useState, useEffect } from 'react';
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
import { TipoPonto } from '../../types';
import type { TipoPonto as TipoPontoType, FiltrosPontos } from '../../types';

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
  }, []);

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

  const buscarPontos = () => {
    if (!selectedUser) return;
    
    if (filtros.dataInicio === filtros.dataFim) {
      // Pontos de um dia específico
      pontosHook.loadPontosDeHoje(selectedUser.id);
    } else {
      // Pontos de um período
      pontosHook.loadPontosPorPeriodo(selectedUser.id, filtros);
    }
  };

  const gerarRelatorio = () => {
    if (!selectedUser || !filtros.dataInicio || !filtros.dataFim) return;
    
    relatoriosHook.loadRelatorioHoras(selectedUser.id, filtros);
  };

  const getChipColor = (tipo: TipoPontoType) => {
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
        Visualizar Pontos
      </Typography>

      {/* Seleção de Usuário */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
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
        </CardContent>
      </Card>

      {selectedUser && (
        <Card>
          <CardContent>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="visualização de pontos">
              <Tab label="Lista de Pontos" icon={<ScheduleIcon />} />
              <Tab label="Relatório de Horas" icon={<ReportIcon />} />
            </Tabs>

            {/* Aba: Lista de Pontos */}
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

              {/* Tabela de Pontos */}
              {pontosHook.loading ? (
                <Typography>Carregando...</Typography>
              ) : pontosHook.data && pontosHook.data.length > 0 ? (
                <TableContainer component={Paper}>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>Data/Hora</TableCell>
                        <TableCell>Tipo</TableCell>
                        <TableCell>Localização</TableCell>
                        <TableCell>Observação</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {pontosHook.data.map((ponto) => (
                        <TableRow key={ponto.id}>
                          <TableCell>
                            {format(new Date(ponto.dataHora), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={ponto.tipo.replace('_', ' ')}
                              color={getChipColor(ponto.tipo)}
                              size="small"
                            />
                          </TableCell>
                          <TableCell>{ponto.localizacao || '-'}</TableCell>
                          <TableCell>{ponto.observacao || '-'}</TableCell>
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