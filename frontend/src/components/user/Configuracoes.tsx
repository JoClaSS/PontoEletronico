import React, { useState, useRef, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Alert,
  Snackbar,
  Avatar,
  IconButton,
  Stack,
  CircularProgress
} from '@mui/material';
import {
  Settings as SettingsIcon,
  Save as SaveIcon,
  PhotoCamera,
  Delete as DeleteIcon
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { apiService } from '../../services/apiService';
import type { ConfiguracaoEmpresa, AtualizarConfiguracaoRequest } from '../../types';

interface FormData {
  nomeEmpresa: string;
  horarioCheckin: string;
  horarioCheckout: string;
  fotoEmpresa?: string;
  logoEmpresaNome?: string;
  logoEmpresaTipo?: string;
  logoEmpresaTamanho?: number;
}

const Configuracoes: React.FC = () => {
  const { user, isAdmin, isFuncionario } = useAuth();
  
  // Verificar permissão antes de renderizar qualquer coisa
  const hasPermission = isAdmin();
  
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [formData, setFormData] = useState<FormData>({
    nomeEmpresa: 'Mundial Ciclo',
    horarioCheckin: '08:00',
    horarioCheckout: '18:00',
  });

  const [loading, setLoading] = useState(false);
  const [loadingConf, setLoadingConf] = useState(false);
  const [configuracaoCarregada, setConfiguracaoCarregada] = useState(false);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success' as 'success' | 'error'
  });

  // Carrega configurações automaticamente quando o componente for montado
  useEffect(() => {
    if (hasPermission && !configuracaoCarregada) {
      carregarConfiguracoes(false); // Carrega silenciosamente na primeira vez
    }
  }, [hasPermission]);

  // Função para carregar configurações manualmente (sem useEffect)
  const carregarConfiguracoes = async (showSuccessMessage = true) => {
    if (loadingConf) return; // Evita múltiplas chamadas simultâneas
    
    setLoadingConf(true);
    try {
      const configuracoes = await apiService.getConfiguracoes();
      
      setFormData({
        nomeEmpresa: configuracoes.nomeEmpresa || 'Mundial Ciclo',
        horarioCheckin: configuracoes.horarioCheckin || '08:00',
        horarioCheckout: configuracoes.horarioCheckout || '18:00',
        fotoEmpresa: configuracoes.fotoEmpresa,
        logoEmpresaNome: configuracoes.logoEmpresaNome,
        logoEmpresaTipo: configuracoes.logoEmpresaTipo,
        logoEmpresaTamanho: configuracoes.logoEmpresaTamanho,
      });
      
      setConfiguracaoCarregada(true);
      
      if (showSuccessMessage) {
        setSnackbar({
          open: true,
          message: 'Configurações carregadas com sucesso!',
          severity: 'success'
        });
      }
    } catch (error) {
      console.error('Erro ao carregar configurações:', error);
      if (showSuccessMessage) {
        setSnackbar({
          open: true,
          message: 'Erro ao carregar configurações. Usando valores padrão.',
          severity: 'error'
        });
      }
    } finally {
      setLoadingConf(false);
    }
  };

  const handleInputChange = (field: keyof FormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: event.target.value
    }));
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validar tipo de arquivo
    const tiposPermitidos = ['image/jpeg', 'image/png', 'image/jpg'];
    if (!tiposPermitidos.includes(file.type)) {
      setSnackbar({
        open: true,
        message: 'Apenas arquivos JPG, JPEG e PNG são permitidos',
        severity: 'error'
      });
      return;
    }

    // Validar tamanho (máximo 2MB)
    const tamanhoMaximo = 2 * 1024 * 1024;
    if (file.size > tamanhoMaximo) {
      setSnackbar({
        open: true,
        message: 'Arquivo deve ter no máximo 2MB',
        severity: 'error'
      });
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      const base64 = e.target?.result as string;
      setFormData(prev => ({
        ...prev,
        fotoEmpresa: base64,
        logoEmpresaNome: file.name,
        logoEmpresaTipo: file.type,
        logoEmpresaTamanho: file.size
      }));
    };
    reader.readAsDataURL(file);
  };

  const handleRemoveFoto = () => {
    setFormData(prev => ({
      ...prev,
      fotoEmpresa: undefined,
      logoEmpresaNome: undefined,
      logoEmpresaTipo: undefined,
      logoEmpresaTamanho: undefined
    }));
  };

  const validateForm = (): string | null => {
    if (!formData.nomeEmpresa.trim()) {
      return 'Nome da empresa é obrigatório';
    }

    if (!formData.horarioCheckin) {
      return 'Horário de check-in é obrigatório';
    }

    if (!formData.horarioCheckout) {
      return 'Horário de checkout é obrigatório';
    }

    // Validar se horário de checkout é depois do check-in
    const checkin = new Date(`2000-01-01T${formData.horarioCheckin}:00`);
    const checkout = new Date(`2000-01-01T${formData.horarioCheckout}:00`);
    
    if (checkout <= checkin) {
      return 'Horário de checkout deve ser posterior ao horário de check-in';
    }

    return null;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      setSnackbar({
        open: true,
        message: validationError,
        severity: 'error'
      });
      return;
    }

    setLoading(true);
    try {
      const request: AtualizarConfiguracaoRequest = {
        nomeEmpresa: formData.nomeEmpresa.trim(),
        horarioCheckin: formData.horarioCheckin,
        horarioCheckout: formData.horarioCheckout,
        // Garantir que campos opcionais sejam omitidos se undefined ou vazios
        ...(formData.fotoEmpresa && { fotoEmpresa: formData.fotoEmpresa }),
        ...(formData.logoEmpresaNome && { logoEmpresaNome: formData.logoEmpresaNome }),
        ...(formData.logoEmpresaTipo && { logoEmpresaTipo: formData.logoEmpresaTipo }),
        ...(formData.logoEmpresaTamanho && { logoEmpresaTamanho: formData.logoEmpresaTamanho })
      };

      // Log para debug
      console.log('Dados sendo enviados:', JSON.stringify(request, null, 2));

      await apiService.salvarConfiguracoes(request);

      setSnackbar({
        open: true,
        message: 'Configurações salvas com sucesso!',
        severity: 'success'
      });
      
      // Recarrega os dados atualizados do servidor (sem mensagem adicional)
      await carregarConfiguracoes(false);
    } catch (error: any) {
      console.error('Erro ao salvar configurações:', error);
      
      let errorMessage = 'Erro ao salvar configurações. Tente novamente.';
      
      // Extrai mensagem específica do erro
      if (error?.response?.data?.message) {
        errorMessage = `Erro: ${error.response.data.message}`;
      } else if (error?.response?.status === 500) {
        errorMessage = 'Erro interno do servidor. Verifique os logs do backend.';
      } else if (error?.response?.status === 400) {
        errorMessage = 'Dados inválidos. Verifique os campos preenchidos.';
      } else if (error?.message) {
        errorMessage = `Erro: ${error.message}`;
      }
      
      setSnackbar({
        open: true,
        message: errorMessage,
        severity: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar(prev => ({ ...prev, open: false }));
  };

  // Verificar se o usuário tem permissão ANTES de qualquer renderização
  if (!hasPermission) {
    return (
      <Box sx={{ textAlign: 'center', mt: 4 }}>
        <Alert severity="warning">
          Você não tem permissão para acessar as configurações do sistema.
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <SettingsIcon sx={{ mr: 2, color: 'primary.main', fontSize: 28 }} />
              <Typography variant="h5" component="h1" color='black'>
                Configurações da Empresa
              </Typography>
            </Box>
            
            {/* Botão para carregar configurações 
            <Button
              variant="outlined"
              onClick={() => carregarConfiguracoes(true)}
              disabled={loadingConf}
              startIcon={loadingConf ? <CircularProgress size={16} /> : <SettingsIcon />}
              size="small"
            >
              {loadingConf ? 'Carregando...' : configuracaoCarregada ? 'Recarregar' : 'Carregar Configurações'}
            </Button> */}
          </Box>

          <Box component="form" onSubmit={handleSubmit}>
            {/* Alert informativo */}
            {!configuracaoCarregada && (
              <Alert severity="info" sx={{ mb: 3 }}>
                Clique em "Carregar Configurações" para buscar as configurações salvas no servidor ou use os valores padrão.
              </Alert>
            )}
            
            <Stack spacing={3}>
              {/* Nome da Empresa */}
              <TextField
                fullWidth
                label="Nome da Empresa"
                value={formData.nomeEmpresa}
                onChange={handleInputChange('nomeEmpresa')}
                required
                variant="outlined"
                placeholder="Digite o nome da empresa"
              />

              {/* Horários */}
              <Box sx={{ display: 'flex', gap: 2 }}>
                <TextField
                  fullWidth
                  label="Horário de Check-in"
                  type="time"
                  value={formData.horarioCheckin}
                  onChange={handleInputChange('horarioCheckin')}
                  required
                  variant="outlined"
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
                <TextField
                  fullWidth
                  label="Horário de Checkout"
                  type="time"
                  value={formData.horarioCheckout}
                  onChange={handleInputChange('horarioCheckout')}
                  required
                  variant="outlined"
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
              </Box>

              {/* Logo da Empresa */}
              <Box>
                <Typography variant="h6" gutterBottom>
                  Logo da Empresa
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Avatar
                    src={formData.fotoEmpresa || ''}
                    sx={{ 
                      width: 80, 
                      height: 80,
                      bgcolor: formData.fotoEmpresa ? 'transparent' : 'grey.300'
                    }}
                  >
                    {!formData.fotoEmpresa && <SettingsIcon />}
                  </Avatar>
                  
                  <Box sx={{ flexGrow: 1 }}>
                    <input
                      accept="image/*"
                      style={{ display: 'none' }}
                      id="foto-empresa-input"
                      type="file"
                      ref={fileInputRef}
                      onChange={handleFileChange}
                    />
                    <label htmlFor="foto-empresa-input">
                      <Button
                        variant="outlined"
                        component="span"
                        startIcon={<PhotoCamera />}
                        sx={{ mr: 1 }}
                      >
                        Selecionar Foto
                      </Button>
                    </label>
                    
                    {formData.fotoEmpresa && (
                      <Button
                        variant="outlined"
                        color="error"
                        startIcon={<DeleteIcon />}
                        onClick={handleRemoveFoto}
                      >
                        Remover
                      </Button>
                    )}
                    
                    <Typography variant="caption" display="block" sx={{ mt: 1 }}>
                      {formData.logoEmpresaNome ? 
                        `${formData.logoEmpresaNome} (${Math.round((formData.logoEmpresaTamanho || 0) / 1024)}KB)` :
                        'Formatos aceitos: JPG, PNG. Tamanho máximo: 2MB'
                      }
                    </Typography>
                  </Box>
                </Box>
              </Box>

              {/* Botão de Salvar */}
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2 }}>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <SaveIcon />}
                  disabled={loading}
                  size="large"
                >
                  {loading ? 'Salvando...' : 'Salvar Configurações'}
                </Button>
              </Box>
            </Stack>
          </Box>
        </CardContent>
      </Card>

      {/* Snackbar para mensagens */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Configuracoes;