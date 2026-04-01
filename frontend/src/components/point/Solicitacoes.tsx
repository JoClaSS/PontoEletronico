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
  Snackbar
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
  Download as DownloadIcon
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { StatusSolicitacao } from '../../types';
import type { Solicitacao, MotivoSolicitacao, CriarSolicitacaoRequest } from '../../types';

const Solicitacoes: React.FC = () => {
  const { selectedUser, setSelectedUser, usuarios, setUsuarios } = useAppContext();
  const { useUsuarios } = useApi();
  const usuariosHook = useUsuarios();

  const [solicitacoes, setSolicitacoes] = useState<Solicitacao[]>([]);
  const [motivos, setMotivos] = useState<MotivoSolicitacao[]>([]);
  const [loading, setLoading] = useState(false);
  const [dialogAberto, setDialogAberto] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  const [arquivoSelecionado, setArquivoSelecionado] = useState<File | null>(null);
  const [motivoSelecionado, setMotivoSelecionado] = useState<MotivoSolicitacao | null>(null);
  const [dataReferencia, setDataReferencia] = useState<Date | null>(null);

  // Formulário de nova solicitação
  const [novasSolicitacao, setNovaSolicitacao] = useState<CriarSolicitacaoRequest>({
    dataReferencia: '',
    usuarioId: '',
    motivoId: '',
    descricao: ''
  });

  // Carrega usuários na inicialização
  useEffect(() => {
    if (usuarios.length === 0) {
      usuariosHook.loadUsuarios();
    }
    carregarMotivos();
  }, []);

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
      const response = await fetch('http://localhost:8081/api/solicitacoes/motivos');
      if (response.ok) {
        const data = await response.json();
        setMotivos(data);
      }
    } catch (error) {
      console.error('Erro ao carregar motivos:', error);
    }
  };

  const carregarSolicitacoes = async () => {
    if (!selectedUser) return;
    
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8081/api/solicitacoes/usuario/${selectedUser.id}`);
      if (response.ok) {
        const data = await response.json();
        setSolicitacoes(data);
      }
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
      descricao: ''
    });
    setDataReferencia(null);
    setArquivoSelecionado(null);
    setMotivoSelecionado(null);
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
      
      // Valida se a data não é futura
      const hoje = new Date();
      hoje.setHours(0, 0, 0, 0);
      const dataRef = new Date(dataReferencia);
      dataRef.setHours(0, 0, 0, 0);
      
      if (dataRef > hoje) {
        throw new Error('Não é possível criar solicitação para datas futuras');
      }
      
      if (motivoSelecionado?.requerAnexo && !arquivoSelecionado) {
        throw new Error('Anexo é obrigatório para este motivo');
      }
      
      // Formato da data para envio
      const dataFormatada = format(dataReferencia, 'yyyy-MM-dd');
      
      let response;
      
      // Se o motivo requer anexo ou se um anexo foi fornecido, usa a API de multipart
      if (motivoSelecionado?.requerAnexo || arquivoSelecionado) {        
        const formData = new FormData();
        formData.append('usuarioId', novasSolicitacao.usuarioId);
        formData.append('dataReferencia', dataFormatada);
        formData.append('motivoId', novasSolicitacao.motivoId);
        formData.append('descricao', novasSolicitacao.descricao.trim());
        
        if (arquivoSelecionado) {
          formData.append('anexo', arquivoSelecionado);
        }
        
        response = await fetch('http://localhost:8081/api/solicitacoes/com-anexo', {
          method: 'POST',
          body: formData,
        });
      } else {
        // Usa a API JSON normal
        response = await fetch('http://localhost:8081/api/solicitacoes', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            ...novasSolicitacao,
            dataReferencia: dataFormatada,
            descricao: novasSolicitacao.descricao.trim()
          }),
        });
      }

      if (response.ok) {
        setSnackbar({
          open: true,
          message: 'Solicitação criada com sucesso!',
          severity: 'success'
        });
        handleFecharDialog();
        carregarSolicitacoes();
      } else {
        const error = await response.json();
        throw new Error(error.error || 'Erro ao criar solicitação');
      }
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
      const response = await fetch(`http://localhost:8081/api/solicitacoes/${solicitacaoId}/anexo`);
      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = nomeArquivo;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      } else {
        throw new Error('Erro ao baixar anexo');
      }
    } catch (error) {
      console.error('Erro ao baixar anexo:', error);
      setSnackbar({
        open: true,
        message: 'Erro ao baixar anexo',
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

  return (
    <Box>
      {/* Título */}
      <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'black' }}>
        Solicitações
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
            {/* Botão Abrir Solicitação */}
            <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6">
                Solicitações de {selectedUser.nome}
              </Typography>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={handleAbrirDialog}
              >
                Abrir Solicitação
              </Button>
            </Box>

            {/* Tabela de Solicitações */}
            {loading ? (
              <Typography>Carregando...</Typography>
            ) : solicitacoes.length > 0 ? (
              <TableContainer component={Paper}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell><strong>Data Referência</strong></TableCell>
                      <TableCell><strong>Motivo</strong></TableCell>
                      <TableCell><strong>Status</strong></TableCell>
                      <TableCell><strong>Anexo</strong></TableCell>
                      <TableCell><strong>Criado em</strong></TableCell>
                      <TableCell><strong>Ações</strong></TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {solicitacoes.map((solicitacao) => (
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
                          {solicitacao.temAnexo ? (
                            <Button
                              size="small"
                              startIcon={<DownloadIcon />}
                              onClick={() => handleDownloadAnexo(solicitacao.id, solicitacao.anexoNome || 'anexo')}
                              color="primary"
                            >
                              {solicitacao.anexoNome}
                            </Button>
                          ) : (
                            <Typography variant="body2" color="text.secondary">
                              Sem anexo
                            </Typography>
                          )}
                        </TableCell>
                        <TableCell>
                          {format(new Date(solicitacao.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                        </TableCell>
                        <TableCell>
                          <Button
                            size="small"
                            startIcon={<ViewIcon />}
                            onClick={() => {/* TODO: Implementar visualização detalhada */}}
                          >
                            Ver Detalhes
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            ) : (
              <Typography color="text.secondary">
                Nenhuma solicitação encontrada para este usuário
              </Typography>
            )}
          </CardContent>
        </Card>
      )}

      {/* Dialog para Nova Solicitação */}
      <Dialog open={dialogAberto} onClose={handleFecharDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Abrir Nova Solicitação</DialogTitle>
        <DialogContent>
          <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={dateFnsLocale}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
              <DatePicker
                label="Data de Referência *"
                value={dataReferencia}
                onChange={(newValue) => setDataReferencia(newValue)}
                maxDate={new Date()}
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

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
      >
        <Alert severity={snackbar.severity}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Solicitacoes;