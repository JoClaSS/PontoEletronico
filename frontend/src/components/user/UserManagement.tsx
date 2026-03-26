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
  Chip,
  CircularProgress
} from '@mui/material';
import {
  People as PeopleIcon,
  PersonAdd as PersonAddIcon,
  Save as SaveIcon,
  Clear as ClearIcon,
  Close as CloseIcon
} from '@mui/icons-material';
import { useAppContext } from '../../contexts/AppContext';
import { useApi } from '../../hooks/useApi';
import { Backend } from '../../types';
import type { Usuario } from '../../types';

interface FormData {
  nome: string;
  email: string;
  cpf: string;
  cargo: string;
  departamento: string;
}

const UserManagement: React.FC = () => {
  const { selectedBackend } = useAppContext();
  const { useUsuarios } = useApi();
  const { data: usuarios, loading, error, loadUsuarios, criarUsuario } = useUsuarios();

  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState<FormData>({
    nome: '',
    email: '',
    cpf: '',
    cargo: '',
    departamento: ''
  });

  const [snackbar, setSnackbar] = useState({ 
    open: false, 
    message: '', 
    severity: 'success' as 'success' | 'error' | 'warning' 
  });

  const [submitLoading, setSubmitLoading] = useState(false);

  // Carrega usuários ao montar o componente
  useEffect(() => {
    loadUsuarios();
  }, []);

  // Recarrega usuários quando o backend mudar
  useEffect(() => {
    if (usuarios) {
      loadUsuarios();
    }
  }, [selectedBackend]);

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

    // CPF é obrigatório apenas para backend MVC
    if (selectedBackend === Backend.MVC && !formData.cpf.trim()) {
      return 'CPF é obrigatório para o backend MVC';
    }

    // Cargo e departamento são obrigatórios apenas para backend Clean
    if (selectedBackend === Backend.CLEAN && !formData.cargo.trim()) {
      return 'Cargo é obrigatório para o backend Clean Architecture';
    }

    if (selectedBackend === Backend.CLEAN && !formData.departamento.trim()) {
      return 'Departamento é obrigatório para o backend Clean Architecture';
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
      cargo: '',
      departamento: ''
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
      // Prepara dados baseado no backend selecionado
      let userData: Omit<Usuario, 'id' | 'createdAt' | 'updatedAt'>;

      if (selectedBackend === Backend.MVC) {
        userData = {
          nome: formData.nome.trim(),
          email: formData.email.trim(),
          cpf: formData.cpf.replace(/\D/g, '')
        };
      } else {
        userData = {
          nome: formData.nome.trim(),
          email: formData.email.trim(),
          cargo: formData.cargo.trim(),
          departamento: formData.departamento.trim(),
          ativo: true
        };
      }

      await criarUsuario(userData);

      setSnackbar({ 
        open: true, 
        message: 'Usuário criado com sucesso!', 
        severity: 'success' 
      });

      handleCloseModal();
    } catch (error) {
      setSnackbar({ 
        open: true, 
        message: error instanceof Error ? error.message : 'Erro ao criar usuário', 
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

    return (
      <TableContainer component={Paper} sx={{ mt: 2 }}>
        <Table>
          <TableHead sx={{ backgroundColor: 'grey.100' }}>
            <TableRow>
              <TableCell><strong>Nome</strong></TableCell>
              <TableCell><strong>Email</strong></TableCell>
              {selectedBackend === Backend.MVC && (
                <TableCell><strong>CPF</strong></TableCell>
              )}
              {selectedBackend === Backend.CLEAN && (
                <>
                  <TableCell><strong>Cargo</strong></TableCell>
                  <TableCell><strong>Departamento</strong></TableCell>
                  <TableCell><strong>Status</strong></TableCell>
                </>
              )}
              <TableCell><strong>Data Criação</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {usuarios.map((usuario) => (
              <TableRow key={usuario.id} hover>
                <TableCell>{usuario.nome}</TableCell>
                <TableCell>{usuario.email}</TableCell>
                {selectedBackend === Backend.MVC && (
                  <TableCell>{usuario.cpf || '-'}</TableCell>
                )}
                {selectedBackend === Backend.CLEAN && (
                  <>
                    <TableCell>{usuario.cargo || '-'}</TableCell>
                    <TableCell>{usuario.departamento || '-'}</TableCell>
                    <TableCell>
                      <Chip 
                        label={usuario.ativo ? 'Ativo' : 'Inativo'} 
                        color={usuario.ativo ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                  </>
                )}
                <TableCell>
                  {usuario.createdAt ? formatDate(usuario.createdAt) : '-'}
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
              <Typography variant="h4" component="h1">
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

          {/* Informação sobre backend ativo */}
          <Alert severity="info" sx={{ mb: 2 }}>
            Backend ativo: <strong>{selectedBackend === Backend.MVC ? 'MVC Architecture' : 'Clean Architecture'}</strong>
            {usuarios && ` • ${usuarios.length} usuário(s) encontrado(s)`}
          </Alert>

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
            <Typography variant="h6">
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
            {/* Informação sobre backend ativo */}
            <Alert severity="info" sx={{ mb: 3 }}>
              Backend ativo: <strong>{selectedBackend === Backend.MVC ? 'MVC Architecture' : 'Clean Architecture'}</strong>
              {selectedBackend === Backend.MVC ? 
                ' (CPF obrigatório)' : 
                ' (Cargo e Departamento obrigatórios)'
              }
            </Alert>

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

              {/* Linha 2: Campos específicos por backend */}
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                {/* CPF - visível apenas para backend MVC */}
                {selectedBackend === Backend.MVC && (
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
                )}

                {/* Cargo - visível apenas para backend Clean */}
                {selectedBackend === Backend.CLEAN && (
                  <Box sx={{ flex: 1, minWidth: 280 }}>
                    <TextField
                      fullWidth
                      label="Cargo"
                      value={formData.cargo}
                      onChange={handleInputChange('cargo')}
                      required
                      variant="outlined"
                    />
                  </Box>
                )}

                {/* Departamento - visível apenas para backend Clean */}
                {selectedBackend === Backend.CLEAN && (
                  <Box sx={{ flex: 1, minWidth: 280 }}>
                    <TextField
                      fullWidth
                      label="Departamento"
                      value={formData.departamento}
                      onChange={handleInputChange('departamento')}
                      required
                      variant="outlined"
                    />
                  </Box>
                )}
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