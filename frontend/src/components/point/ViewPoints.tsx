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
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import 'dayjs/locale/pt-br';
import {
  Person as PersonIcon,
  Assessment as ReportIcon,
  Schedule as ScheduleIcon,
  PictureAsPdf as PdfIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import jsPDF from 'jspdf';
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { useAuth } from '../../contexts/AuthContext';
import type { FiltrosPontos, PontoAgrupado } from '../../types';

// Configurar dayjs para português
dayjs.locale('pt-br');

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
  const { selectedUser, setSelectedUser, usuarios, setUsuarios, pontosUpdateTrigger } = useAppContext();
  const { useUsuarios, usePontos, useRelatorios } = useApi();
  const { user, isAdmin, isFuncionario } = useAuth();
  const usuariosHook = useUsuarios();
  const pontosHook = usePontos(selectedUser?.id);
  const relatoriosHook = useRelatorios();

  const [tabValue, setTabValue] = useState(0);
  const [filtros, setFiltros] = useState<FiltrosPontos>({
    dataInicio: dayjs().format('YYYY-MM-DD'),
    dataFim: dayjs().format('YYYY-MM-DD')
  });
  const [relatorioLocal, setRelatorioLocal] = useState<{
    totalHoras: string;
    diasTrabalhados: number;
    gerado: boolean;
  } | null>(null);

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

  const handleUserChange = (event: SelectChangeEvent<string>) => {
    const userId = event.target.value;
    const user = usuarios.find(u => u.id === userId);
    setSelectedUser(user || null);
  };

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const buscarPontos = useCallback(() => {
    if (!selectedUser || !filtros.dataInicio || !filtros.dataFim) return;
    
    if (filtros.dataInicio === filtros.dataFim) {
      pontosHook.loadPontosPorData?.(selectedUser.id, filtros.dataInicio);
    } else {
      pontosHook.loadPontosPorPeriodo?.(selectedUser.id, filtros);
    }
  }, [selectedUser?.id, filtros.dataInicio, filtros.dataFim]);

  // Carrega pontos quando usuário for selecionado pela primeira vez
  useEffect(() => {
    if (selectedUser && !pontosHook.data) {
      buscarPontos();
    }
  }, [selectedUser?.id, buscarPontos]);

  // Limpa relatório local quando usuário ou filtros mudam
  useEffect(() => {
    setRelatorioLocal(null);
  }, [selectedUser?.id, filtros.dataInicio, filtros.dataFim]);

  // Escuta notificações de atualizações de pontos e recarrega dados
  useEffect(() => {
    if (pontosUpdateTrigger > 0 && selectedUser) {
      console.log('[ViewPoints] Recebida notificação de atualização de pontos, recarregando...');
      buscarPontos();
    }
  }, [pontosUpdateTrigger, selectedUser, buscarPontos]);

  const gerarRelatorio = () => {
    if (!selectedUser || !filtros.dataInicio || !filtros.dataFim) return;
    
    // Verificar se há dados carregados
    if (!pontosHook.data || pontosHook.data.length === 0) {
      alert('Primeiro busque os pontos para o período desejado.');
      return;
    }
    
    // Usar a mesma função de agrupamento da tabela
    const registrosAgrupados = agruparPontosPorData();
    
    // Calcular estatísticas baseadas nos registros agrupados
    const diasTrabalhados = registrosAgrupados.length;
    let totalMinutosTrabalhados = 0;
    
    registrosAgrupados.forEach(registro => {
      if (registro.horasTrabalhadas && registro.horasTrabalhadas !== '00:00') {
        const [horas, minutos] = registro.horasTrabalhadas.split(':').map(Number);
        totalMinutosTrabalhados += (horas * 60) + minutos;
      }
    });
    
    const totalHorasTrabalhadas = minutosParaHora(totalMinutosTrabalhados);
    
    // Atualizar estado local do relatório
    setRelatorioLocal({
      totalHoras: totalHorasTrabalhadas,
      diasTrabalhados,
      gerado: true
    });
  };

  const gerarPDF = () => {
    if (!pontosHook.data || pontosHook.data.length === 0) {
      alert('Nenhum dado encontrado para gerar o PDF. Primeiro busque os pontos.');
      return;
    }

    console.log('Dados dos pontos:', pontosHook.data); // Debug
    
    const doc = new jsPDF('landscape');
    const usuario = selectedUser;
    
    // Usar a mesma função de agrupamento da tabela
    const registrosAgrupados = agruparPontosPorData();
    
    console.log('Registros agrupados:', registrosAgrupados.length); // Debug
    
    // Verificação de segurança
    if (!usuario) {
      alert('Dados do usuário não encontrados');
      return;
    }
    
    // Calcular estatísticas baseadas nos registros agrupados
    const diasTrabalhados = registrosAgrupados.length;
    let totalMinutosTrabalhados = 0;
    
    registrosAgrupados.forEach(registro => {
      if (registro.horasTrabalhadas && registro.horasTrabalhadas !== '00:00') {
        const [horas, minutos] = registro.horasTrabalhadas.split(':').map(Number);
        totalMinutosTrabalhados += (horas * 60) + minutos;
      }
    });
    
    const totalHorasTrabalhadas = minutosParaHora(totalMinutosTrabalhados);
    
    // Cabeçalho
    doc.setFontSize(16);
    doc.text('RELATÓRIO DE FREQUÊNCIA', 150, 20, { align: 'center' });
    
    doc.setFontSize(12);
    doc.text(`Funcionário: ${usuario.nome || 'Nome não informado'}`, 20, 40);
    
    // Corrigir formato das datas
    const dataInicioFormatada = dayjs(filtros.dataInicio).format('DD/MM/YYYY');
    const dataFimFormatada = dayjs(filtros.dataFim).format('DD/MM/YYYY');
    
    doc.text(`Período: ${dataInicioFormatada} a ${dataFimFormatada}`, 20, 50);
    doc.text(`Total de Horas: ${totalHorasTrabalhadas}`, 20, 60);
    doc.text(`Dias Trabalhados: ${diasTrabalhados}`, 20, 70);
    
    // Linha separadora
    doc.line(20, 75, 280, 75);
    
    // Cabeçalho da tabela
    doc.setFontSize(9);
    doc.text('Data', 20, 85);
    doc.text('Entrada 1', 55, 85);
    doc.text('Saída 1', 85, 85);
    doc.text('Entrada 2', 115, 85);
    doc.text('Saída 2', 145, 85);
    doc.text('Entrada 3', 175, 85);
    doc.text('Saída 3', 205, 85);
    doc.text('Horas', 235, 85);
    doc.text('Observação', 260, 85);
    
    // Linha do cabeçalho
    doc.line(20, 87, 280, 87);
    
    let currentY = 95;
    
    // Verificar se há pontos para exibir
    if (registrosAgrupados.length === 0) {
      doc.setFontSize(10);
      doc.text('Nenhum registro encontrado para o período selecionado.', 20, currentY);
      console.log('Nenhum registro agrupado encontrado para exibir'); // Debug
    } else {
      console.log(`Total de ${registrosAgrupados.length} registros agrupados para exibir`); // Debug
      
      // Adicionar registros (já estão ordenados por data mais recente primeiro, vamos inverter para PDF)
      const registrosOrdenados = [...registrosAgrupados].reverse();
      
      registrosOrdenados.forEach((registro, index) => {
        console.log(`Adicionando registro ${index}:`, registro); // Debug
        
        // Verificar se precisa de nova página
        if (currentY > 180) {
          doc.addPage();
          currentY = 20;
          
          // Repetir cabeçalho na nova página
          doc.setFontSize(9);
          doc.text('Data', 20, currentY);
          doc.text('Entrada 1', 55, currentY);
          doc.text('Saída 1', 85, currentY);
          doc.text('Entrada 2', 115, currentY);
          doc.text('Saída 2', 145, currentY);
          doc.text('Entrada 3', 175, currentY);
          doc.text('Saída 3', 205, currentY);
          doc.text('Horas', 235, currentY);
          doc.text('Observação', 260, currentY);
          doc.line(20, currentY + 2, 280, currentY + 2);
          currentY += 10;
        }
        
        doc.setFontSize(8);
        const dataFormatada = dayjs(registro.data).format('DD/MM/YYYY');
        
        doc.text(dataFormatada, 20, currentY);
        doc.text(registro.entrada1 || '-', 55, currentY);
        doc.text(registro.saida1 || '-', 85, currentY);
        doc.text(registro.entrada2 || '-', 115, currentY);
        doc.text(registro.saida2 || '-', 145, currentY);
        doc.text(registro.entrada3 || '-', 175, currentY);
        doc.text(registro.saida3 || '-', 205, currentY);
        doc.text(registro.horasTrabalhadas || '-', 235, currentY);
        doc.text((registro.observacao || '').substring(0, 20), 260, currentY);
        
        currentY += 7;
      });
    }
    
    // Rodapé
    const totalPages = doc.getNumberOfPages();
    for (let i = 1; i <= totalPages; i++) {
      doc.setPage(i);
      doc.setFontSize(8);
      doc.text(`Página ${i} de ${totalPages}`, 150, 200, { align: 'center' });
      doc.text(`Gerado em: ${dayjs().format('DD/MM/YYYY HH:mm')}`, 280, 200, { align: 'right' });
    }
    
    // Salvar o PDF
    const nomeArquivo = `relatorio-${(usuario.nome || 'usuario').replace(/\s+/g, '-')}-${dayjs().format('DDMMYYYY')}.pdf`;
    doc.save(nomeArquivo);
    
    console.log('PDF gerado com sucesso:', nomeArquivo); // Debug
  };

  // Função para agrupar pontos por data
  const agruparPontosPorData = (): PontoAgrupado[] => {
    if (!pontosHook.data) return [];
    
    const grupos: Map<string, PontoAgrupado> = new Map();
    
    pontosHook.data.forEach(ponto => {
      // Corrigir problema de timezone - extrair apenas a data string sem interpretação de timezone
      let dataStr: string;
      
      // Se dataHora já contém a data no formato esperado, extrair diretamente
      if (typeof ponto.dataHora === 'string') {
        // Se é ISO string (ex: "2024-04-14T10:30:00"), extrair apenas a parte da data
        if (ponto.dataHora.includes('T')) {
          dataStr = ponto.dataHora.split('T')[0]; // "2024-04-14"
        }
        // Se já é formato de data (ex: "2024-04-14") 
        else if (ponto.dataHora.match(/^\d{4}-\d{2}-\d{2}$/)) {
          dataStr = ponto.dataHora;
        }
        // Fallback para outros formatos
        else {
          const dataObj = new Date(ponto.dataHora);
          // Usar toISOString e pegar apenas a parte da data para evitar problemas de timezone
          dataStr = dataObj.toISOString().split('T')[0];
        }
      } else {
        // Se dataHora é um objeto Date
        const dataObj = ponto.dataHora as Date;
        dataStr = dataObj.toISOString().split('T')[0];
      }
      
      // Extrair hora dos dados de dataHora
      const dataObj = new Date(ponto.dataHora);
      const horaStr = format(dataObj, 'HH:mm');
      
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
    
    // Calcula horas trabalhadas para cada grupo
    grupos.forEach((grupo) => {
      grupo.horasTrabalhadas = calcularHorasTrabalhadas(grupo);
    });
    
    // Converte para array e ordena por data (mais recente primeiro)
    return Array.from(grupos.values()).sort((a, b) => 
      new Date(b.data).getTime() - new Date(a.data).getTime()
    );
  };

  // Função para calcular horas trabalhadas baseado nos 3 períodos
  const calcularHorasTrabalhadas = (registro: PontoAgrupado): string => {
    let totalMinutos = 0;

    // Período 1 (entrada1 - saida1)
    if (registro.entrada1 && registro.saida1) {
      const entrada1 = horaParaMinutos(registro.entrada1);
      const saida1 = horaParaMinutos(registro.saida1);
      if (saida1 > entrada1) {
        totalMinutos += saida1 - entrada1;
      }
    }

    // Período 2 (entrada2 - saida2)
    if (registro.entrada2 && registro.saida2) {
      const entrada2 = horaParaMinutos(registro.entrada2);
      const saida2 = horaParaMinutos(registro.saida2);
      if (saida2 > entrada2) {
        totalMinutos += saida2 - entrada2;
      }
    }

    // Período 3 (entrada3 - saida3)
    if (registro.entrada3 && registro.saida3) {
      const entrada3 = horaParaMinutos(registro.entrada3);
      const saida3 = horaParaMinutos(registro.saida3);
      if (saida3 > entrada3) {
        totalMinutos += saida3 - entrada3;
      }
    }

    return minutosParaHora(totalMinutos);
  };

  // Função auxiliar para converter hora (HH:mm) em minutos
  const horaParaMinutos = (hora: string): number => {
    const [horas, minutos] = hora.split(':').map(Number);
    return horas * 60 + minutos;
  };

  // Função auxiliar para converter minutos em hora (HH:mm)
  const minutosParaHora = (minutos: number): string => {
    const horas = Math.floor(minutos / 60);
    const mins = minutos % 60;
    return `${horas.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}`;
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
          {isAdmin() ? (
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
                {usuarios.filter(user => user.ativo !== false).map((user) => (
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
                  {user?.nome || 'Usuário'}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {user?.email}
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
              <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pt-br">
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
                  <DatePicker
                    label="Data Início"
                    value={dayjs(filtros.dataInicio)}
                    onChange={(newValue) => {
                      const formattedDate = newValue ? newValue.format('YYYY-MM-DD') : '';
                      setFiltros(prev => ({ ...prev, dataInicio: formattedDate }));
                    }}
                    format="DD/MM/YYYY"
                    slotProps={{
                      textField: {
                        size: 'medium',
                        sx: { minWidth: 200 }
                      }
                    }}
                  />
                  <DatePicker
                    label="Data Fim"
                    value={dayjs(filtros.dataFim)}
                    onChange={(newValue) => {
                      const formattedDate = newValue ? newValue.format('YYYY-MM-DD') : '';
                      setFiltros(prev => ({ ...prev, dataFim: formattedDate }));
                    }}
                    format="DD/MM/YYYY"
                    slotProps={{
                      textField: {
                        size: 'medium',
                        sx: { minWidth: 200 }
                      }
                    }}
                  />
                  <Button 
                    variant="contained" 
                    onClick={buscarPontos}
                    disabled={pontosHook.loading}
                  >
                    {pontosHook.loading ? 'Carregando...' : 'Buscar'}
                  </Button>
                </Stack>
              </LocalizationProvider>

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
                        <TableCell align="center"><strong>Horas Trabalhadas</strong></TableCell>
                        <TableCell><strong>Observação</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {agruparPontosPorData().map((registro) => (
                        <TableRow key={registro.data}>
                          <TableCell>
                            <strong>{dayjs(registro.data).format('DD/MM/YYYY')}</strong>
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
                          <TableCell align="center">
                            <Chip 
                              label={registro.horasTrabalhadas || '00:00'} 
                              color={registro.horasTrabalhadas && registro.horasTrabalhadas !== '00:00' ? 'primary' : 'default'}
                              size="small"
                              variant={registro.horasTrabalhadas && registro.horasTrabalhadas !== '00:00' ? 'filled' : 'outlined'}
                            />
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
              <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pt-br">
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
                  <DatePicker
                    label="Data Início"
                    value={dayjs(filtros.dataInicio)}
                    onChange={(newValue) => {
                      const formattedDate = newValue ? newValue.format('YYYY-MM-DD') : '';
                      setFiltros(prev => ({ ...prev, dataInicio: formattedDate }));
                    }}
                    format="DD/MM/YYYY"
                    slotProps={{
                      textField: {
                        size: 'medium',
                        sx: { minWidth: 200 }
                      }
                    }}
                  />
                  <DatePicker
                    label="Data Fim"
                    value={dayjs(filtros.dataFim)}
                    onChange={(newValue) => {
                      const formattedDate = newValue ? newValue.format('YYYY-MM-DD') : '';
                      setFiltros(prev => ({ ...prev, dataFim: formattedDate }));
                    }}
                    format="DD/MM/YYYY"
                    slotProps={{
                      textField: {
                        size: 'medium',
                        sx: { minWidth: 200 }
                      }
                    }}
                  />
                  <Button 
                    variant="contained" 
                    onClick={gerarRelatorio}
                    startIcon={<ReportIcon />}
                    sx={{ minWidth: 180 }}
                  >
                    Gerar Relatório
                  </Button>
                  {pontosHook.data && pontosHook.data.length > 0 && (
                    <Button 
                      variant="outlined" 
                      color="primary"
                      onClick={gerarPDF}
                      startIcon={<PdfIcon />}
                      sx={{ minWidth: 150 }}
                    >
                      Baixar PDF
                    </Button>
                  )}
                </Stack>
              </LocalizationProvider>

                            {relatorioLocal?.gerado ? (
                <Card>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      Relatório de Horas
                    </Typography>
                    <Stack spacing={1}>
                      <Typography><strong>Período:</strong> {dayjs(filtros.dataInicio).format('DD/MM/YYYY')} a {dayjs(filtros.dataFim).format('DD/MM/YYYY')}</Typography>
                      <Typography><strong>Total de Horas:</strong> {relatorioLocal.totalHoras}</Typography>
                      <Typography><strong>Dias Trabalhados:</strong> {relatorioLocal.diasTrabalhados}</Typography>
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