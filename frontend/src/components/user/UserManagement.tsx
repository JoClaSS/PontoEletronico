import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  Paper,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Snackbar,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  IconButton,
  Tooltip,
  Chip,
  DialogContentText
} from '@mui/material';
import {
  People as PeopleIcon,
  PersonAdd as PersonAddIcon,
  Save as SaveIcon,
  Clear as ClearIcon,
  Close as CloseIcon,
  Visibility as VisibilityIcon,
  Delete as DeleteIcon,
  Warning as WarningIcon
} from '@mui/icons-material';
import { useApi } from '../../hooks/useApi';
import type { Usuario, CriarUsuarioRequest, RoleType } from '../../types';

interface FormData {
  nome: string;
  email: string;
  cpf: string;
  role: RoleType;
}

const UserManagement: React.FC = () => {
  const { useUsuarios } = useApi();
  const { data: usuarios, loading, error, loadUsuarios, criarUsuario, desativarUsuario } = useUsuarios();

  const [modalOpen, setModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<Usuario | null>(null);
  const [formData, setFormData] = useState<FormData>({
    nome: '',
    email: '',
    cpf: '',
    role: 'FUNCIONARIO'
  });

  const [snackbar, setSnackbar] = useState({ 
    open: false, 
    message: '', 
    severity: 'success' as 'success' | 'error' | 'warning' 
  });

  const [submitLoading, setSubmitLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // Estados para filtros
  const [filtroNome, setFiltroNome] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('todos');

  // Carrega usuários ao montar o componente
  useEffect(() => {
    loadUsuarios();
  }, []);

  const handleInputChange = (field: keyof FormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: event.target.value
    }));
  };

  const formatCPF = (value: string): string => {
    // Remove tudo que não for número
    const numbers = value.replace(/\D/g, '');
    
    // Aplica a máscara XXX.XXX.XXX-XX
    if (numbers.length <= 11) {
      return numbers
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }
    
    return value;
  };

  const handleCPFChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatCPF(event.target.value);
    setFormData(prev => ({
      ...prev,
      cpf: formatted
    }));
  };

  const validateForm = (): string | null => {
    if (!formData.nome.trim()) {
      return 'Nome é obrigatório';
    }

    if (!formData.email.trim()) {
      return 'Email é obrigatório';
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      return 'Email deve ter um formato válido';
    }

    if (!formData.cpf.trim()) {
      return 'CPF é obrigatório';
    }

    return null;
  };

  const handleOpenModal = () => {
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setFormData({
      nome: '',
      email: '',
      cpf: '',
      role: 'FUNCIONARIO'
    });
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      setSnackbar({ 
        open: true, 
        message: validationError, 
        severity: 'warning' 
      });
      return;
    }

    setSubmitLoading(true);

    try {
      // Prepara dados para MVC
      const userData: CriarUsuarioRequest = {
        nome: formData.nome.trim(),
        email: formData.email.trim(),
        cpf: formData.cpf.replace(/\D/g, ''),
        role: formData.role
      };

      await criarUsuario(userData);

      setSnackbar({ 
        open: true, 
        message: 'Usuário criado com sucesso!', 
        severity: 'success' 
      });

      handleCloseModal();
    } catch (error: any) {
      const errorMessage = error.message || 'Erro ao criar usuário';
      setSnackbar({ 
        open: true, 
        message: errorMessage, 
        severity: 'error' 
      });
    } finally {
      setSubmitLoading(false);
    }
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  const handleViewUser = (usuario: Usuario) => {
    setSelectedUser(usuario);
    setViewModalOpen(true);
  };

  const handleDeleteUser = (usuario: Usuario) => {
    setSelectedUser(usuario);
    setDeleteModalOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedUser) return;

    setDeleteLoading(true);
    try {
      await desativarUsuario(selectedUser.id);
      setSnackbar({
        open: true,
        message: 'Usuário desativado com sucesso!',
        severity: 'success'
      });
      setDeleteModalOpen(false);
      setSelectedUser(null);
    } catch (error: any) {
      setSnackbar({
        open: true,
        message: error.message || 'Erro ao desativar usuário',
        severity: 'error'
      });
    } finally {
      setDeleteLoading(false);
    }
  };

  // Função para filtrar usuários
  const filtrarUsuarios = (): Usuario[] => {
    if (!usuarios) return [];

    return usuarios.filter(usuario => {
      // Filtro por nome
      const nomeMatch = usuario.nome.toLowerCase().includes(filtroNome.toLowerCase());
      
      // Filtro por status
      let statusMatch = true;
      if (filtroStatus === 'ativo') {
        statusMatch = usuario.ativo !== false;
      } else if (filtroStatus === 'inativo') {
        statusMatch = usuario.ativo === false;
      }
      // Se filtroStatus === 'todos', statusMatch permanece true

      return nomeMatch && statusMatch;
    });
  };

  const handleLimparFiltros = () => {
    setFiltroNome('');
    setFiltroStatus('todos');
  };

  const renderUserTable = () => {
    if (loading) {
      return (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
        </Box>
      );
    }

    if (error) {
      return (
        <Alert severity="error" sx={{ m: 2 }}>
          {error}
        </Alert>
      );
    }

    const usuariosFiltrados = filtrarUsuarios();

    if (!usuarios || usuarios.length === 0) {
      return (
        <Box sx={{ textAlign: 'center', p: 4 }}>
          <Typography variant="h6" color="text.secondary">
            Nenhum usuário encontrado
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Clique em "Cadastrar Usuário" para adicionar o primeiro usuário
          </Typography>
        </Box>
      );
    }

    if (usuariosFiltrados.length === 0) {
      return (
        <Box sx={{ textAlign: 'center', p: 4 }}>
          <Typography variant="h6" color="text.secondary">
            Nenhum usuário encontrado com os filtros aplicados
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Tente ajustar os filtros ou limpar para ver todos os usuários
          </Typography>
        </Box>
      );
    }

    return (
      <TableContainer component={Paper} sx={{ mt: 2 }}>
        <Table>
          <TableHead sx={{ backgroundColor: 'grey.100' }}>
            <TableRow>
              <TableCell><strong>Nome</strong></TableCell>
              <TableCell><strong>Role</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              <TableCell><strong>Data Criação</strong></TableCell>
              <TableCell align="center"><strong>Ações</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {usuariosFiltrados.map((usuario) => (
              <TableRow key={usuario.id} hover>
                <TableCell>{usuario.nome}</TableCell>
                <TableCell>
                  {usuario.role === 'ADMIN' ? 'Administrador' : usuario.role === 'FUNCIONARIO' ? 'Funcionário' : usuario.role || '-'}
                </TableCell>
                <TableCell>
                  <Chip 
                    label={usuario.ativo !== false ? 'Ativo' : 'Inativo'}
                    color={usuario.ativo !== false ? 'success' : 'error'}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {usuario.createdAt ? formatDate(usuario.createdAt) : '-'}
                </TableCell>
                <TableCell align="center">
                  <Tooltip title="Visualizar">
                    <IconButton 
                      onClick={() => handleViewUser(usuario)} 
                      color="primary"
                      size="small"
                    >
                      <VisibilityIcon />
                    </IconButton>
                  </Tooltip>
                  {usuario.ativo !== false && (
                    <Tooltip title="Desativar Usuário">
                      <IconButton 
                        onClick={() => handleDeleteUser(usuario)} 
                        color="error"
                        size="small"
                        sx={{ ml: 1 }}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Tooltip>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  };

  return (
    <Box sx={{ maxWidth: '100%', mx: 'auto' }}>
      <Card elevation={3}>
        <CardContent>
          {/* Cabeçalho */}
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <PeopleIcon sx={{ mr: 2, fontSize: 32, color: 'primary.main' }} />
              <Typography variant="h4" component="h1" sx={{ color: 'black' }}>
                Gerenciar Usuários
              </Typography>
            </Box>
            
            <Button
              variant="contained"
              startIcon={<PersonAddIcon />}
              onClick={handleOpenModal}
              size="large"
            >
              Cadastrar Usuário
            </Button>
          </Box>

          {/* Filtros */}
          <Box sx={{ 
            display: 'flex', 
            gap: 2, 
            mb: 3, 
            p: 2, 
            backgroundColor: 'grey.50', 
            borderRadius: 1,
            flexWrap: 'wrap',
            alignItems: 'center'
          }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 'bold', color: 'text.primary' }}>
              Filtros:
            </Typography>
            
            <TextField
              size="small"
              label="Buscar por nome"
              value={filtroNome}
              onChange={(e) => setFiltroNome(e.target.value)}
              sx={{ minWidth: 200 }}
              placeholder="Digite o nome..."
            />
            
            <FormControl size="small" sx={{ minWidth: 150 }}>
              <InputLabel>Status</InputLabel>
              <Select
                value={filtroStatus}
                label="Status"
                onChange={(e) => setFiltroStatus(e.target.value)}
              >
                <MenuItem value="todos">Todos</MenuItem>
                <MenuItem value="ativo">Ativos</MenuItem>
                <MenuItem value="inativo">Inativos</MenuItem>
              </Select>
            </FormControl>
            
            <Button
              variant="outlined"
              size="small"
              onClick={handleLimparFiltros}
              startIcon={<ClearIcon />}
            >
              Limpar Filtros
            </Button>
            
            {(filtroNome || filtroStatus !== 'todos') && (
              <Typography variant="body2" color="text.secondary">
                {filtrarUsuarios().length} usuário(s) encontrado(s)
              </Typography>
            )}
          </Box>

          {/* Tabela de usuários */}
          {renderUserTable()}
        </CardContent>
      </Card>

      {/* Modal de cadastro */}
      <Dialog 
        open={modalOpen} 
        onClose={handleCloseModal} 
        maxWidth="md" 
        fullWidth
        PaperProps={{
          sx: { minHeight: 400 }
        }}
      >
        <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <PersonAddIcon sx={{ mr: 1, color: 'primary.main' }} />
            <Typography variant="h6" color='black'>
              Cadastrar Usuário
            </Typography>
          </Box>
          <Button
            onClick={handleCloseModal}
            sx={{ minWidth: 'auto', p: 1 }}
          >
            <CloseIcon />
          </Button>
        </DialogTitle>
        
        <form onSubmit={handleSubmit}>
          <DialogContent>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              {/* Linha 1: Nome e Email */}
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Box sx={{ flex: 1, minWidth: 280 }}>
                  <TextField
                    fullWidth
                    label="Nome"
                    value={formData.nome}
                    onChange={handleInputChange('nome')}
                    required
                    variant="outlined"
                  />
                </Box>
                <Box sx={{ flex: 1, minWidth: 280 }}>
                  <TextField
                    fullWidth
                    label="Email"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange('email')}
                    required
                    variant="outlined"
                  />
                </Box>
              </Box>

              {/* Linha 2: CPF e Role */}
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Box sx={{ flex: 1, minWidth: 280 }}>
                  <TextField
                    fullWidth
                    label="CPF"
                    value={formData.cpf}
                    onChange={handleCPFChange}
                    placeholder="000.000.000-00"
                    required
                    variant="outlined"
                    inputProps={{ maxLength: 14 }}
                  />
                </Box>
                <Box sx={{ flex: 1, minWidth: 280 }}>
                  <FormControl fullWidth required>
                    <InputLabel>Role</InputLabel>
                    <Select
                      value={formData.role}
                      label="Role"
                      onChange={(e) => setFormData(prev => ({ ...prev, role: e.target.value as RoleType }))}
                    >
                      <MenuItem value="FUNCIONARIO">Funcionário</MenuItem>
                      <MenuItem value="ADMIN">Administrador</MenuItem>
                    </Select>
                  </FormControl>
                </Box>
              </Box>
            </Box>
          </DialogContent>

          <DialogActions sx={{ px: 3, pb: 3 }}>
            <Button
              variant="outlined"
              startIcon={<ClearIcon />}
              onClick={handleCloseModal}
              disabled={submitLoading}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="contained"
              startIcon={<SaveIcon />}
              disabled={submitLoading}
            >
              {submitLoading ? 'Salvando...' : 'Salvar'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Modal de visualização de usuário */}
      <Dialog 
        open={viewModalOpen} 
        onClose={() => setViewModalOpen(false)} 
        maxWidth="md" 
        fullWidth
      >
        <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <VisibilityIcon sx={{ mr: 1, color: 'primary.main' }} />
            <Typography variant="h6" color='black'>
              Detalhes do Usuário
            </Typography>
          </Box>
          <Button
            onClick={() => setViewModalOpen(false)}
            sx={{ minWidth: 'auto', p: 1 }}
          >
            <CloseIcon />
          </Button>
        </DialogTitle>
        
        <DialogContent>
          {selectedUser && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <br></br>
              <TextField
                fullWidth
                label="Nome"
                value={selectedUser.nome}
                variant="outlined"
                InputProps={{ readOnly: true }}
              />
              <TextField
                fullWidth
                label="Email"
                value={selectedUser.email}
                variant="outlined"
                InputProps={{ readOnly: true }}
              />
              <TextField
                fullWidth
                label="CPF"
                value={selectedUser.cpf || 'Não informado'}
                variant="outlined"
                InputProps={{ readOnly: true }}
              />
              <TextField
                fullWidth
                label="Role"
                value={selectedUser.role === 'ADMIN' ? 'Administrador' : selectedUser.role === 'FUNCIONARIO' ? 'Funcionário' : selectedUser.role || 'Não informada'}
                variant="outlined"
                InputProps={{ readOnly: true }}
              />
              <TextField
                fullWidth
                label="Status"
                value={selectedUser.ativo !== false ? 'Ativo' : 'Inativo'}
                variant="outlined"
                InputProps={{ readOnly: true }}
              />
              <TextField
                fullWidth
                label="Data de Criação"
                value={selectedUser.createdAt ? formatDate(selectedUser.createdAt) : 'Não informada'}
                variant="outlined"
                InputProps={{ readOnly: true }}
              />
              {selectedUser.updatedAt && (
                <TextField
                  fullWidth
                  label="Última Atualização"
                  value={formatDate(selectedUser.updatedAt)}
                  variant="outlined"
                  InputProps={{ readOnly: true }}
                />
              )}
            </Box>
          )}
        </DialogContent>

        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button
            variant="outlined"
            onClick={() => setViewModalOpen(false)}
          >
            Fechar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal de confirmação de exclusão */}
      <Dialog 
        open={deleteModalOpen} 
        onClose={() => setDeleteModalOpen(false)} 
        maxWidth="sm" 
        fullWidth
      >
        <DialogTitle sx={{ display: 'flex', alignItems: 'center', color: 'error.main' }}>
          <WarningIcon sx={{ mr: 1 }} />
          Confirmar Desativação
        </DialogTitle>
        
        <DialogContent>
          <DialogContentText>
            {selectedUser && (
              <>
                Tem certeza que deseja <strong>desativar</strong> o usuário <strong>{selectedUser.nome}</strong>?
                <br /><br />
                Esta ação irá:
                <br />
                • Impedir que o usuário faça login no sistema
                <br />
                • Manter os dados históricos do usuário
                <br />
                • Permitir reativação posterior se necessário
              </>
            )}
          </DialogContentText>
        </DialogContent>

        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button
            variant="outlined"
            onClick={() => setDeleteModalOpen(false)}
            disabled={deleteLoading}
          >
            Cancelar
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={handleConfirmDelete}
            disabled={deleteLoading}
            startIcon={deleteLoading ? <CircularProgress size={16} /> : <DeleteIcon />}
          >
            {deleteLoading ? 'Desativando...' : 'Desativar'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar para mensagens */}
      <Snackbar 
        open={snackbar.open} 
        autoHideDuration={6000} 
        onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert 
          onClose={() => setSnackbar(prev => ({ ...prev, open: false }))} 
          severity={snackbar.severity}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default UserManagement;