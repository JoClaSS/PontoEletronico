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
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Snackbar,
  IconButton,
  Menu,
  ListItemIcon,
  ListItemText,
  RadioGroup,
  FormControlLabel,
  Radio,
  FormLabel,
  Pagination
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { ptBR as dateFnsLocale } from 'date-fns/locale';
import type { SelectChangeEvent } from '@mui/material';
import {
  Person as PersonIcon,
  Add as AddIcon,
  Visibility as ViewIcon,
  AttachFile as AttachFileIcon,
  Download as DownloadIcon,
  MoreVert as MoreVertIcon,
  Check as CheckIcon,
  Cancel as CancelIcon
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { useAuth } from '../../contexts/AuthContext';
import { StatusSolicitacao } from '../../types';
import type { Solicitacao, MotivoSolicitacao, CriarSolicitacaoRequest } from '../../types';

const Solicitacoes: React.FC = () => {
  const { selectedUser, setSelectedUser, usuarios, setUsuarios } = useAppContext();
  const { useUsuarios } = useApi();
  const { user, isAdmin, isFuncionario } = useAuth();
  const usuariosHook = useUsuarios();

  const [solicitacoes, setSolicitacoes] = useState<Solicitacao[]>([]);
  const [motivos, setMotivos] = useState<MotivoSolicitacao[]>([]);
  const [loading, setLoading] = useState(false);
  const [dialogAberto, setDialogAberto] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  const [arquivoSelecionado, setArquivoSelecionado] = useState<File | null>(null);
  const [motivoSelecionado, setMotivoSelecionado] = useState<MotivoSolicitacao | null>(null);
  const [dataReferencia, setDataReferencia] = useState<Date | null>(null);
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
  const [solicitacaoSelecionada, setSolicitacaoSelecionada] = useState<Solicitacao | null>(null);
  const [modalVisualizacao, setModalVisualizacao] = useState(false);
  const [modalResolucao, setModalResolucao] = useState(false);
  const [modalConfirmacao, setModalConfirmacao] = useState(false);
  const [pontosReferencia, setPontosReferencia] = useState({
    entrada1: '',
    saida1: '',
    entrada2: '',
    saida2: '',
    entrada3: '',
    saida3: ''
  });
  const [observacaoResolucao, setObservacaoResolucao] = useState<string>('');

  // Estados dos filtros
  const [dataInicial, setDataInicial] = useState<Date | null>(null);
  const [dataFinal, setDataFinal] = useState<Date | null>(null);
  const [statusSelecionado, setStatusSelecionado] = useState<string>('TODOS');

  // Estados para paginação
  const [paginaAtual, setPaginaAtual] = useState(1);
  const SOLICITACOES_POR_PAGINA = 5;

  // Formulário de nova solicitação
  const [novasSolicitacao, setNovaSolicitacao] = useState<CriarSolicitacaoRequest>({
    dataReferencia: '',
    usuarioId: '',
    motivoId: '',
    descricao: '',
    diasConsecutivos: false,
    quantidadeDias: undefined
  });

  // Estados para dias consecutivos
  const [diasConsecutivos, setDiasConsecutivos] = useState<boolean>(false);
  
  // Estado para contagem de solicitações em aberto (apenas para admin/master)
  const [solicitacoesEmAberto, setSolicitacoesEmAberto] = useState<number>(0);
  const [solicitacaoMaisRecente, setSolicitacaoMaisRecente] = useState<Solicitacao | null>(null);

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
    carregarMotivos();
    carregarContagemSolicitacoesEmAberto();
    
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

  // Carrega solicitações quando usuário é selecionado
  useEffect(() => {
    if (selectedUser) {
      carregarSolicitacoes();
    }
  }, [selectedUser]);

  const handleUserChange = (event: SelectChangeEvent<string>) => {
    const userId = event.target.value;
    const user = usuarios.find(u => u.id === userId);
    setSelectedUser(user || null);
  };

  const carregarMotivos = async () => {
    try {
      const { apiMVCService } = await import('../../services/apiMVC');
      const data = await apiMVCService.getMotivos();
      setMotivos(data);
    } catch (error) {
      console.error('Erro ao carregar motivos:', error);
    }
  };

  const carregarContagemSolicitacoesEmAberto = async () => {
    if (!isAdmin()) return;
    
    try {
      const { apiMVCService } = await import('../../services/apiMVC');
      const [contagemData, recenteData] = await Promise.all([
        apiMVCService.contarSolicitacoesEmAberto(),
        apiMVCService.buscarSolicitacaoMaisRecenteAberta()
      ]);
      setSolicitacoesEmAberto(contagemData.quantidade);
      setSolicitacaoMaisRecente(recenteData.solicitacao);
    } catch (error) {
      console.error('Erro ao carregar dados de solicitações em aberto:', error);
    }
  };

  const carregarSolicitacoes = async () => {
    if (!selectedUser) return;
    
    setLoading(true);
    try {
      const { apiMVCService } = await import('../../services/apiMVC');
      const data = await apiMVCService.getSolicitacoesPorUsuario(selectedUser.id);
      setSolicitacoes(data);
    } catch (error) {
      console.error('Erro ao carregar solicitações:', error);
      setSnackbar({
        open: true,
        message: 'Erro ao carregar solicitações',
        severity: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleAbrirDialog = () => {
    if (selectedUser) {
      setNovaSolicitacao(prev => ({
        ...prev,
        usuarioId: selectedUser.id
      }));
      setDialogAberto(true);
    }
  };

  const handleFecharDialog = () => {
    setDialogAberto(false);
    setNovaSolicitacao({
      dataReferencia: '',
      usuarioId: selectedUser?.id || '',
      motivoId: '',
      descricao: '',
      diasConsecutivos: false,
      quantidadeDias: undefined
    });
    setDataReferencia(null);
    setArquivoSelecionado(null);
    setMotivoSelecionado(null);
    setDiasConsecutivos(false);
  };

  const handleSubmitSolicitacao = async () => {
    try {
      // Valida campos obrigatórios
      if (!dataReferencia) {
        throw new Error('Data de referência é obrigatória');
      }
      
      if (!novasSolicitacao.descricao?.trim()) {
        throw new Error('Descrição é obrigatória');
      }
      
      if (motivoSelecionado?.requerAnexo && !arquivoSelecionado) {
        throw new Error('Anexo é obrigatório para este motivo');
      }
      
      // Valida campos de dias consecutivos
      if (diasConsecutivos && (!novasSolicitacao.quantidadeDias || novasSolicitacao.quantidadeDias < 2)) {
        throw new Error('Quantidade de dias é obrigatória e deve ser pelo menos 2');
      }
      
      if (diasConsecutivos && novasSolicitacao.quantidadeDias && novasSolicitacao.quantidadeDias > 30) {
        throw new Error('Quantidade de dias não pode exceder 30');
      }
      
      // Formato da data para envio
      const dataFormatada = format(dataReferencia, 'yyyy-MM-dd');
      
      const { apiMVCService } = await import('../../services/apiMVC');
      
      // Se o motivo requer anexo ou se um anexo foi fornecido, usa a API de multipart
      if (motivoSelecionado?.requerAnexo || arquivoSelecionado) {        
        const formData = new FormData();
        formData.append('usuarioId', novasSolicitacao.usuarioId);
        formData.append('dataReferencia', dataFormatada);
        formData.append('motivoId', novasSolicitacao.motivoId);
        formData.append('descricao', novasSolicitacao.descricao.trim());
        
        if (diasConsecutivos) {
          formData.append('diasConsecutivos', 'true');
          if (novasSolicitacao.quantidadeDias) {
            formData.append('quantidadeDias', novasSolicitacao.quantidadeDias.toString());
          }
        } else {
          formData.append('diasConsecutivos', 'false');
        }
        
        if (arquivoSelecionado) {
          formData.append('anexo', arquivoSelecionado);
        }
        
        await apiMVCService.criarSolicitacaoComAnexo(formData);
      } else {
        // Usa a API JSON normal
        await apiMVCService.criarSolicitacao({
          ...novasSolicitacao,
          dataReferencia: dataFormatada,
          descricao: novasSolicitacao.descricao.trim(),
          diasConsecutivos,
          quantidadeDias: diasConsecutivos ? novasSolicitacao.quantidadeDias : undefined
        });
      }

      setSnackbar({
        open: true,
        message: 'Solicitação criada com sucesso!',
        severity: 'success'
      });
      handleFecharDialog();
      carregarSolicitacoes();
      carregarContagemSolicitacoesEmAberto();
    } catch (error: any) {
      setSnackbar({
        open: true,
        message: error.message || 'Erro ao criar solicitação',
        severity: 'error'
      });
    }
  };

  const handleMotivoChange = (event: SelectChangeEvent<string>) => {
    const motivoId = event.target.value;
    const motivo = motivos.find(m => m.id === motivoId);
    setMotivoSelecionado(motivo || null);
    setNovaSolicitacao(prev => ({ ...prev, motivoId }));
    
    // Limpa arquivo selecionado se mudou o motivo
    setArquivoSelecionado(null);
  };

  const handleArquivoChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const arquivo = event.target.files?.[0];
    setArquivoSelecionado(arquivo || null);
  };

  const handleDownloadAnexo = async (solicitacaoId: string, nomeArquivo: string) => {
    try {
      const { apiMVCService } = await import('../../services/apiMVC');
      const blob = await apiMVCService.baixarAnexoSolicitacao(solicitacaoId);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = nomeArquivo;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Erro ao baixar anexo:', error);
      setSnackbar({
        open: true,
        message: 'Erro ao baixar anexo',
        severity: 'error'
      });
    }
  };

  const handleFecharMenu = () => {
    setMenuAnchor(null);
  };

  const handleLimparSelecao = () => {
    setSolicitacaoSelecionada(null);
  };

  const handleVisualizarSolicitacao = () => {
    setModalVisualizacao(true);
    handleFecharMenu();
  };

  const handleAbrirResolucao = async (solicitacao?: any) => {
    const solicitacaoParaUsar = solicitacao || solicitacaoSelecionada;
    //console.log('[Solicitacoes] Abrindo resolução para:', solicitacaoParaUsar);
    if (!solicitacaoParaUsar) return;
    
    try {
      const { apiMVCService } = await import('../../services/apiMVC');
      // Busca os pontos da data referente
      //console.log('[Solicitacoes] Buscando pontos para usuário:', solicitacaoParaUsar.usuarioId, 'data:', solicitacaoParaUsar.dataReferencia);
      const pontos = await apiMVCService.getPontosPorData(solicitacaoParaUsar.usuarioId, solicitacaoParaUsar.dataReferencia);
      //console.log('[Solicitacoes] Pontos carregados:', pontos);
      
      if (pontos && pontos.length > 0) {
        // Se há pontos, preenche os campos
        const pontosOrdenados = pontos.sort((a: { dataHora: string | number | Date; }, b: { dataHora: string | number | Date; }) => 
          new Date(a.dataHora).getTime() - new Date(b.dataHora).getTime()
        ).map((ponto: { dataHora: string | number | Date; }) => ({
          ...ponto,
          horario: format(new Date(ponto.dataHora), 'HH:mm')
        }));
        
        //console.log('[Solicitacoes] Pontos processados:', pontosOrdenados);
        
        setPontosReferencia({
          entrada1: pontosOrdenados[0]?.horario || '',
          saida1: pontosOrdenados[1]?.horario || '',
          entrada2: pontosOrdenados[2]?.horario || '',
          saida2: pontosOrdenados[3]?.horario || '',
          entrada3: pontosOrdenados[4]?.horario || '',
          saida3: pontosOrdenados[5]?.horario || ''
        });
      } else {
        //console.log('[Solicitacoes] Nenhum ponto encontrado, campos vazios');
        // Se não há pontos, deixa os campos vazios para serem preenchidos
        setPontosReferencia({
          entrada1: '',
          saida1: '',
          entrada2: '',
          saida2: '',
          entrada3: '',
          saida3: ''
        });
      }
      
      setObservacaoResolucao('');
      setModalResolucao(true);
    } catch (error) {
      console.error('Erro ao carregar pontos:', error);
      setSnackbar({
        open: true,
        message: 'Erro ao carregar pontos da data referente',
        severity: 'error'
      });
    }
    
    handleFecharMenu();
  };

  const handleResolverSolicitacao = async () => {
    if (!solicitacaoSelecionada) return;
    
    // Validação: observação é obrigatória
    if (!observacaoResolucao.trim()) {
      setSnackbar({
        open: true,
        message: 'Observação é obrigatória para resolver a solicitação',
        severity: 'error'
      });
      return;
    }
    
    try {
      const { apiMVCService } = await import('../../services/apiMVC');
      // Inclui os pontos editados e observação na requisição
      await apiMVCService.resolverSolicitacao(solicitacaoSelecionada.id, {
        pontos: pontosReferencia,
        observacao: observacaoResolucao.trim(),
        dataReferencia: solicitacaoSelecionada.dataReferencia
      });

      setSnackbar({
        open: true,
        message: 'Solicitação resolvida com sucesso!',
        severity: 'success'
      });
      setModalResolucao(false);
      handleLimparSelecao();
      carregarSolicitacoes();
      carregarContagemSolicitacoesEmAberto();
    } catch (error: any) {
      setSnackbar({
        open: true,
        message: error.message || 'Erro ao resolver solicitação',
        severity: 'error'
      });
    }
  };

  const handleAbrirConfirmacao = () => {
    setModalConfirmacao(true);
    handleFecharMenu();
  };

  const handleCancelarSolicitacao = async () => {
    if (!solicitacaoSelecionada) return;
    
    // Verifica se a solicitação já foi resolvida
    if (solicitacaoSelecionada.status === StatusSolicitacao.RESOLVIDO) {
      setSnackbar({
        open: true,
        message: 'Não é possível cancelar uma solicitação que já foi resolvida',
        severity: 'error'
      });
      setModalConfirmacao(false);
      return;
    }
    
    // Verifica se a solicitação já foi cancelada
    if (solicitacaoSelecionada.status === StatusSolicitacao.CANCELADO) {
      setSnackbar({
        open: true,
        message: 'Esta solicitação já foi cancelada',
        severity: 'error'
      });
      setModalConfirmacao(false);
      return;
    }
    
    try {
      const { apiMVCService } = await import('../../services/apiMVC');
      await apiMVCService.excluirSolicitacao(solicitacaoSelecionada.id);

      setSnackbar({
        open: true,
        message: 'Solicitação cancelada com sucesso!',
        severity: 'success'
      });
      setModalConfirmacao(false);
      handleLimparSelecao();
      carregarSolicitacoes();
      carregarContagemSolicitacoesEmAberto();
    } catch (error: any) {
      setSnackbar({
        open: true,
        message: error.message || 'Erro ao cancelar solicitação',
        severity: 'error'
      });
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case StatusSolicitacao.ABERTO:
        return 'warning';
      case StatusSolicitacao.RESOLVIDO:
        return 'success';
      case StatusSolicitacao.CANCELADO:
        return 'error';
      default:
        return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case StatusSolicitacao.ABERTO:
        return 'Aberto';
      case StatusSolicitacao.RESOLVIDO:
        return 'Resolvido';
      case StatusSolicitacao.CANCELADO:
        return 'Cancelado';
      default:
        return status;
    }
  };

  // Função para filtrar solicitações
  const getsolicitacoesFiltradas = () => {
    let solicitacoesFiltradas = [...solicitacoes];

    // Filtro por data
    if (dataInicial || dataFinal) {
      solicitacoesFiltradas = solicitacoesFiltradas.filter(solicitacao => {
        const dataSolicitacao = new Date(solicitacao.dataReferencia + 'T00:00:00');
        
        if (dataInicial && dataFinal) {
          const dataIni = new Date(dataInicial);
          dataIni.setHours(0, 0, 0, 0);
          const dataFim = new Date(dataFinal);
          dataFim.setHours(23, 59, 59, 999);
          return dataSolicitacao >= dataIni && dataSolicitacao <= dataFim;
        } else if (dataInicial) {
          const dataIni = new Date(dataInicial);
          dataIni.setHours(0, 0, 0, 0);
          return dataSolicitacao >= dataIni;
        } else if (dataFinal) {
          const dataFim = new Date(dataFinal);
          dataFim.setHours(23, 59, 59, 999);
          return dataSolicitacao <= dataFim;
        }
        return true;
      });
    }

    // Filtro por status
    if (statusSelecionado !== 'TODOS') {
      solicitacoesFiltradas = solicitacoesFiltradas.filter(
        solicitacao => solicitacao.status === statusSelecionado
      );
    }

    return solicitacoesFiltradas;
  };

  // Função para obter solicitações da página atual
  const obterSolicitacoesPaginadas = () => {
    const solicitacoesFiltradas = getsolicitacoesFiltradas();
    const inicio = (paginaAtual - 1) * SOLICITACOES_POR_PAGINA;
    const fim = inicio + SOLICITACOES_POR_PAGINA;
    return solicitacoesFiltradas.slice(inicio, fim);
  };

  // Função para calcular total de páginas
  const calcularTotalPaginas = (): number => {
    const solicitacoesFiltradas = getsolicitacoesFiltradas();
    return Math.ceil(solicitacoesFiltradas.length / SOLICITACOES_POR_PAGINA);
  };

  const limparFiltros = () => {
    setDataInicial(null);
    setDataFinal(null);
    setStatusSelecionado('TODOS');
    setPaginaAtual(1); // Volta para primeira página ao limpar filtros
  };

  const handleMudancaPagina = (event: React.ChangeEvent<unknown>, novaPagina: number) => {
    setPaginaAtual(novaPagina);
  };

  // Resetar página quando filtros mudarem
  useEffect(() => {
    setPaginaAtual(1);
  }, [dataInicial, dataFinal, statusSelecionado]);

  return (
    <Box>
      {/* Título */}
      <Typography variant="h4" component="h1" sx={{ color: 'black', mb: 2 }}>
        Solicitações
      </Typography>

      {/* Aviso de solicitações em aberto - apenas para admin/master */}
      {isAdmin() && solicitacaoMaisRecente && (
        <Alert 
          severity="warning"
          sx={{ mb: 2 }}
        >
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
            <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
              Solicitação mais recente em aberto ({solicitacoesEmAberto} total{solicitacoesEmAberto > 1 ? '' : ''})
            </Typography>
            
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
              <Typography variant="body2">
                <strong>Data:</strong> {format(parseISO(solicitacaoMaisRecente.dataReferencia + 'T00:00:00'), "dd/MM/yyyy", { locale: ptBR })}
              </Typography>
              <Typography variant="body2">
                <strong>Usuário:</strong> {solicitacaoMaisRecente.nomeUsuario}
              </Typography>
              <Typography variant="body2">
                <strong>Motivo:</strong> {solicitacaoMaisRecente.motivo.descricao}
              </Typography>
            </Box>
            
            <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
              <Button
                size="small"
                variant="outlined"
                startIcon={<ViewIcon />}
                onClick={() => {
                  setSolicitacaoSelecionada(solicitacaoMaisRecente);
                  setModalVisualizacao(true);
                }}
              >
                Visualizar
              </Button>
              
              <Button
                size="small"
                variant="contained"
                color="success"
                startIcon={<CheckIcon />}
                onClick={() => {
                  setSolicitacaoSelecionada(solicitacaoMaisRecente);
                  handleAbrirResolucao(solicitacaoMaisRecente);
                }}
              >
                Resolver
              </Button>
              
              <Button
                size="small"
                variant="outlined"
                color="error"
                startIcon={<CancelIcon />}
                onClick={() => {
                  setSolicitacaoSelecionada(solicitacaoMaisRecente);
                  setModalConfirmacao(true);
                }}
              >
                Cancelar
              </Button>
            </Box>
          </Box>
        </Alert>
      )}

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
            {/* Botão Abrir Solicitação - oculto para ADMIN */}
            <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6">
                Solicitações de {selectedUser.nome}
              </Typography>
              {!isAdmin() && (
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={handleAbrirDialog}
                >
                  Abrir Solicitação
                </Button>
              )}
            </Box>

            {/* Filtros */}
            <Card sx={{ mb: 3, backgroundColor: '#f5f5f5' }}>
              <CardContent>
                <Typography variant="subtitle1" gutterBottom>
                  Filtros
                </Typography>
                <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={dateFnsLocale}>
                  <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
                    <DatePicker
                      label="Data Inicial"
                      value={dataInicial}
                      onChange={(newValue) => {
                        if (newValue) {
                          setDataInicial(newValue instanceof Date ? newValue : new Date(newValue.toString()));
                        } else {
                          setDataInicial(null);
                        }
                      }}
                      format="dd/MM/yyyy"
                      slotProps={{
                        textField: {
                          size: 'small',
                          sx: { minWidth: 150 }
                        }
                      }}
                    />
                    <DatePicker
                      label="Data Final"
                      value={dataFinal}
                      onChange={(newValue) => {
                        if (newValue) {
                          setDataFinal(newValue instanceof Date ? newValue : new Date(newValue.toString()));
                        } else {
                          setDataFinal(null);
                        }
                      }}
                      format="dd/MM/yyyy"
                      slotProps={{
                        textField: {
                          size: 'small',
                          sx: { minWidth: 150 }
                        }
                      }}
                    />
                    <FormControl size="small" sx={{ minWidth: 150 }}>
                      <InputLabel>Status</InputLabel>
                      <Select
                        value={statusSelecionado}
                        label="Status"
                        onChange={(e) => setStatusSelecionado(e.target.value)}
                      >
                        <MenuItem value="TODOS">Todos</MenuItem>
                        <MenuItem value={StatusSolicitacao.ABERTO}>Aberto</MenuItem>
                        <MenuItem value={StatusSolicitacao.RESOLVIDO}>Resolvido</MenuItem>
                        <MenuItem value={StatusSolicitacao.CANCELADO}>Cancelado</MenuItem>
                      </Select>
                    </FormControl>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={limparFiltros}
                    >
                      Limpar Filtros
                    </Button>
                  </Box>
                </LocalizationProvider>
              </CardContent>
            </Card>

            {/* Tabela de Solicitações */}
            {loading ? (
              <Typography>Carregando...</Typography>
            ) : getsolicitacoesFiltradas().length > 0 ? (
              <>
                <TableContainer component={Paper}>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell><strong>Data Referência</strong></TableCell>
                        <TableCell><strong>Motivo</strong></TableCell>
                        <TableCell><strong>Status</strong></TableCell>
                        <TableCell><strong>Criado em</strong></TableCell>
                        <TableCell><strong>Ações</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {obterSolicitacoesPaginadas().map((solicitacao) => (
                        <TableRow key={solicitacao.id}>
                          <TableCell>
                            {format(parseISO(solicitacao.dataReferencia + 'T00:00:00'), "dd/MM/yyyy", { locale: ptBR })}
                          </TableCell>
                          <TableCell>{solicitacao.motivo.descricao}</TableCell>
                          <TableCell>
                            <Chip
                              label={getStatusLabel(solicitacao.status)}
                              color={getStatusColor(solicitacao.status)}
                              size="small"
                            />
                          </TableCell>
                          <TableCell>
                            {format(new Date(solicitacao.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                          </TableCell>
                          <TableCell>
                            <IconButton
                              size="small"
                              onClick={(event) => {
                                setMenuAnchor(event.currentTarget);
                                setSolicitacaoSelecionada(solicitacao);
                              }}
                            >
                              <MoreVertIcon />
                            </IconButton>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
                
                {/* Informações de paginação e controles */}
                {(() => {
                  const totalPaginas = calcularTotalPaginas();
                  const solicitacoesFiltradas = getsolicitacoesFiltradas();
                  
                  return totalPaginas > 1 && (
                    <Box sx={{ 
                      display: 'flex', 
                      justifyContent: 'space-between', 
                      alignItems: 'center', 
                      mt: 2,
                      px: 2 
                    }}>
                      <Typography variant="body2" color="text.secondary">
                        Mostrando {((paginaAtual - 1) * SOLICITACOES_POR_PAGINA) + 1} - {Math.min(paginaAtual * SOLICITACOES_POR_PAGINA, solicitacoesFiltradas.length)} de {solicitacoesFiltradas.length} solicitações
                      </Typography>
                      
                      <Pagination
                        count={totalPaginas}
                        page={paginaAtual}
                        onChange={handleMudancaPagina}
                        color="primary"
                        shape="rounded"
                        showFirstButton
                        showLastButton
                      />
                    </Box>
                  );
                })()
                }
              </>
            ) : solicitacoes.length === 0 ? (
              <Typography color="text.secondary">
                Nenhuma solicitação encontrada para este usuário
              </Typography>
            ) : (
              <Typography color="text.secondary">
                Nenhuma solicitação encontrada com os filtros aplicados
              </Typography>
            )}
          </CardContent>
        </Card>
      )}

      {/* Menu de Ações */}
      <Menu
        anchorEl={menuAnchor}
        open={Boolean(menuAnchor)}
        onClose={handleFecharMenu}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        <MenuItem onClick={handleVisualizarSolicitacao}>
          <ListItemIcon>
            <ViewIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Visualizar</ListItemText>
        </MenuItem>
        {isAdmin() && solicitacaoSelecionada?.status === StatusSolicitacao.ABERTO && (
          <MenuItem onClick={handleAbrirResolucao}>
            <ListItemIcon>
              <CheckIcon fontSize="small" />
            </ListItemIcon>
            <ListItemText>Resolver</ListItemText>
          </MenuItem>
        )}
        {isAdmin() && (
          <MenuItem onClick={handleAbrirConfirmacao}>
            <ListItemIcon>
              <CancelIcon fontSize="small" />
            </ListItemIcon>
            <ListItemText>Cancelar</ListItemText>
          </MenuItem>
        )}
      </Menu>

      {/* Modal de Visualização */}
      <Dialog open={modalVisualizacao} onClose={() => { setModalVisualizacao(false); handleLimparSelecao(); }} maxWidth="md" fullWidth>
        <DialogTitle>Detalhes da Solicitação</DialogTitle>
        <DialogContent>
          {solicitacaoSelecionada && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
              <Box>
                <Typography variant="subtitle2">Data de Referência:</Typography>
                <Typography variant="body1">
                  {format(parseISO(solicitacaoSelecionada.dataReferencia + 'T00:00:00'), "dd/MM/yyyy", { locale: ptBR })}
                </Typography>
              </Box>
              
              <Box>
                <Typography variant="subtitle2">Motivo:</Typography>
                <Typography variant="body1">{solicitacaoSelecionada.motivo.descricao}</Typography>
              </Box>
              
              <Box>
                <Typography variant="subtitle2">Status:</Typography>
                <Chip
                  label={getStatusLabel(solicitacaoSelecionada.status)}
                  color={getStatusColor(solicitacaoSelecionada.status)}
                  size="small"
                />
              </Box>
              
              <Box>
                <Typography variant="subtitle2">Descrição:</Typography>
                <Typography variant="body1">{solicitacaoSelecionada.descricao}</Typography>
              </Box>
              
              {solicitacaoSelecionada.diasConsecutivos && (
                <Box>
                  <Typography variant="subtitle2">Dias Consecutivos:</Typography>
                  <Typography variant="body1">
                    Sim - {solicitacaoSelecionada.quantidadeDias} dias
                  </Typography>
                </Box>
              )}
              
              {solicitacaoSelecionada.temAnexo && (
                <Box>
                  <Typography variant="subtitle2">Anexo:</Typography>
                  <Button
                    variant="outlined"
                    startIcon={<DownloadIcon />}
                    onClick={() => handleDownloadAnexo(solicitacaoSelecionada.id, solicitacaoSelecionada.anexoNome || 'anexo')}
                    sx={{ mt: 1 }}
                  >
                    {solicitacaoSelecionada.anexoNome}
                  </Button>
                </Box>
              )}
              
              <Box>
                <Typography variant="subtitle2">Criado em:</Typography>
                <Typography variant="body1">
                  {format(new Date(solicitacaoSelecionada.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                </Typography>
              </Box>
              
              {solicitacaoSelecionada.updatedAt && solicitacaoSelecionada.updatedAt !== solicitacaoSelecionada.createdAt && (
                <Box>
                  <Typography variant="subtitle2">Atualizado em:</Typography>
                  <Typography variant="body1">
                    {format(new Date(solicitacaoSelecionada.updatedAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                  </Typography>
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => { setModalVisualizacao(false); handleLimparSelecao(); }}>Fechar</Button>
        </DialogActions>
      </Dialog>

      {/* Dialog para Nova Solicitação */}
      <Dialog open={dialogAberto} onClose={handleFecharDialog} maxWidth="sm" fullWidth>
        <DialogTitle color='black'>Abrir Nova Solicitação</DialogTitle>
        <DialogContent>
          <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={dateFnsLocale}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
              <DatePicker
                label="Data de Referência *"
                value={dataReferencia}
                onChange={(newValue) => {
                  if (newValue) {
                    setDataReferencia(newValue instanceof Date ? newValue : new Date(newValue.toString()));
                  } else {
                    setDataReferencia(null);
                  }
                }}
                format="dd/MM/yyyy"
                slotProps={{
                  textField: {
                    required: true,
                    fullWidth: true
                  }
                }}
              />
            
            <FormControl fullWidth required>
              <InputLabel>Motivo *</InputLabel>
              <Select
                value={novasSolicitacao.motivoId}
                onChange={handleMotivoChange}
                label="Motivo *"
                required
              >
                {motivos.map((motivo) => (
                  <MenuItem key={motivo.id} value={motivo.id}>
                    {motivo.descricao} {motivo.requerAnexo && <AttachFileIcon sx={{ ml: 1, fontSize: 'small' }} />}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              label="Descrição"
              multiline
              rows={4}
              value={novasSolicitacao.descricao}
              onChange={(e) => setNovaSolicitacao(prev => ({ ...prev, descricao: e.target.value }))}
              required
              fullWidth
            />

            {/* Campo de Dias Consecutivos */}
            <Box>
              <FormLabel component="legend">Dias consecutivos?</FormLabel>
              <RadioGroup
                row
                value={diasConsecutivos ? 'sim' : 'nao'}
                onChange={(e) => {
                  const valor = e.target.value === 'sim';
                  setDiasConsecutivos(valor);
                  setNovaSolicitacao(prev => ({
                    ...prev,
                    diasConsecutivos: valor,
                    quantidadeDias: valor ? prev.quantidadeDias : undefined
                  }));
                }}
              >
                <FormControlLabel value="nao" control={<Radio />} label="Não" />
                <FormControlLabel value="sim" control={<Radio />} label="Sim" />
              </RadioGroup>
            </Box>

            {/* Campo de Quantidade de Dias - só aparece se dias consecutivos = Sim */}
            {diasConsecutivos && (
              <TextField
                label="Quantidade de dias *"
                type="number"
                InputProps={{ 
                  inputProps: { min: 2, max: 30 }
                }}
                value={novasSolicitacao.quantidadeDias || ''}
                onChange={(e) => {
                  const valor = e.target.value ? parseInt(e.target.value) : undefined;
                  setNovaSolicitacao(prev => ({ ...prev, quantidadeDias: valor }));
                }}
                required={diasConsecutivos}
                fullWidth
                helperText="Mínimo 2 dias, máximo 30 dias"
              />
            )}

            {/* Campo de Anexo - só aparece se o motivo requer anexo */}
            {motivoSelecionado?.requerAnexo && (
              <Box>
                <Typography variant="subtitle2" sx={{ mb: 1, color: 'error.main' }}>
                  <AttachFileIcon sx={{ verticalAlign: 'middle', mr: 0.5 }} />
                  Anexo Obrigatório
                </Typography>
                <Button
                  variant="outlined"
                  component="label"
                  startIcon={<AttachFileIcon />}
                  fullWidth
                  sx={{ 
                    justifyContent: 'flex-start',
                    textTransform: 'none',
                    color: arquivoSelecionado ? 'success.main' : 'primary.main'
                  }}
                >
                  {arquivoSelecionado ? arquivoSelecionado.name : 'Selecionar Arquivo'}
                  <input
                    type="file"
                    hidden
                    onChange={handleArquivoChange}
                    accept=".pdf,.doc,.docx,.jpg,.jpeg,.png,.txt"
                  />
                </Button>
                {arquivoSelecionado && (
                  <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: 'block' }}>
                    Tamanho: {(arquivoSelecionado.size / 1024 / 1024).toFixed(2)} MB
                  </Typography>
                )}
              </Box>
            )}
            </Box>
          </LocalizationProvider>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleFecharDialog}>Cancelar</Button>
          <Button 
            onClick={handleSubmitSolicitacao}
            variant="contained"
            disabled={
              !dataReferencia ||
              !novasSolicitacao.motivoId || 
              !novasSolicitacao.descricao?.trim() ||
              (motivoSelecionado?.requerAnexo && !arquivoSelecionado)
            }
          >
            Criar Solicitação
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal de Resolução */}
      <Dialog open={modalResolucao} onClose={() => { setModalResolucao(false); handleLimparSelecao(); }} maxWidth="md" fullWidth>
        <DialogTitle>Resolver Solicitação</DialogTitle>
        <DialogContent>
          {solicitacaoSelecionada && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
              <Box>
                <Typography variant="subtitle2">Data de Referência:</Typography>
                <Typography variant="body1">
                  {format(parseISO(solicitacaoSelecionada.dataReferencia + 'T00:00:00'), "dd/MM/yyyy", { locale: ptBR })}
                </Typography>
              </Box>
              
              <Box>
                <Typography variant="subtitle2">Motivo:</Typography>
                <Typography variant="body1">{solicitacaoSelecionada.motivo.descricao}</Typography>
              </Box>
              
              <Box>
                <Typography variant="subtitle2">Descrição:</Typography>
                <Typography variant="body1">{solicitacaoSelecionada.descricao}</Typography>
              </Box>
              
              {solicitacaoSelecionada.temAnexo && (
                <Box>
                  <Typography variant="subtitle2">Anexo:</Typography>
                  <Button
                    variant="outlined"
                    startIcon={<DownloadIcon />}
                    onClick={() => handleDownloadAnexo(solicitacaoSelecionada.id, solicitacaoSelecionada.anexoNome || 'anexo')}
                    sx={{ mt: 1 }}
                  >
                    {solicitacaoSelecionada.anexoNome}
                  </Button>
                </Box>
              )}
              
              <Box>
                <Typography variant="subtitle2" sx={{ mb: 2 }}>Pontos da Data Referente:</Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                    <TextField
                      label="1ª Entrada"
                      type="time"
                      value={pontosReferencia.entrada1}
                      onChange={(e) => setPontosReferencia(prev => ({ ...prev, entrada1: e.target.value }))}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                    <TextField
                      label="1ª Saída"
                      type="time"
                      value={pontosReferencia.saida1}
                      onChange={(e) => setPontosReferencia(prev => ({ ...prev, saida1: e.target.value }))}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                  </Box>
                  <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                    <TextField
                      label="2ª Entrada"
                      type="time"
                      value={pontosReferencia.entrada2}
                      onChange={(e) => setPontosReferencia(prev => ({ ...prev, entrada2: e.target.value }))}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                    <TextField
                      label="2ª Saída"
                      type="time"
                      value={pontosReferencia.saida2}
                      onChange={(e) => setPontosReferencia(prev => ({ ...prev, saida2: e.target.value }))}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                  </Box>
                  <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                    <TextField
                      label="3ª Entrada"
                      type="time"
                      value={pontosReferencia.entrada3}
                      onChange={(e) => setPontosReferencia(prev => ({ ...prev, entrada3: e.target.value }))}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                    <TextField
                      label="3ª Saída"
                      type="time"
                      value={pontosReferencia.saida3}
                      onChange={(e) => setPontosReferencia(prev => ({ ...prev, saida3: e.target.value }))}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                  </Box>
                </Box>
              </Box>
              
              <TextField
                label="Observação *"
                value={observacaoResolucao}
                onChange={(e) => setObservacaoResolucao(e.target.value)}
                multiline
                rows={3}
                fullWidth
                margin="normal"
                required
                error={!observacaoResolucao.trim()}
                helperText={!observacaoResolucao.trim() ? 'Observação é obrigatória' : ''}
              />
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => { setModalResolucao(false); handleLimparSelecao(); }}>Cancelar</Button>
          <Button 
            onClick={handleResolverSolicitacao} 
            variant="contained" 
            color="success"
            disabled={!observacaoResolucao.trim()}
          >
            Resolver
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal de Confirmação para Cancelar */}
      <Dialog open={modalConfirmacao} onClose={() => { setModalConfirmacao(false); handleLimparSelecao(); }}>
        <DialogTitle>Confirmar Cancelamento</DialogTitle>
        <DialogContent>
          <Typography>
            Tem certeza que deseja cancelar esta solicitação? Esta ação não pode ser desfeita.
          </Typography>
          {solicitacaoSelecionada && (
            <Box sx={{ mt: 2, p: 2, bgcolor: 'grey.100', borderRadius: 1 }}>
              <Typography variant="subtitle2">Solicitação:</Typography>
              <Typography variant="body2">
                {solicitacaoSelecionada.motivo.descricao} - 
                {format(parseISO(solicitacaoSelecionada.dataReferencia + 'T00:00:00'), "dd/MM/yyyy", { locale: ptBR })}
              </Typography>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => { setModalConfirmacao(false); handleLimparSelecao(); }}>Voltar</Button>
          <Button onClick={handleCancelarSolicitacao} variant="contained" color="warning">
            Cancelar Solicitação
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <Alert severity={snackbar.severity}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Solicitacoes;