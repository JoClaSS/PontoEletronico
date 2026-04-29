import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { ptBR } from '@mui/material/locale';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { AppProvider } from './contexts/AppContext';
import Layout from './components/layout/Layout';
import Login from './components/auth/Login';
import TrocaSenha from './components/auth/TrocaSenha';
import RegisterPoint from './components/point/RegisterPoint';
import ViewPoints from './components/point/ViewPoints';
import Solicitacoes from './components/point/Solicitacoes';
import RegisterUser from './components/user/RegisterUser';
import UserManagement from './components/user/UserManagement';
import Configuracoes from './components/user/Configuracoes';

// Tema do Material-UI
const theme = createTheme(
  {
    palette: {
      primary: {
        main: '#1976d2',
      },
      secondary: {
        main: '#dc004e',
      },
    },
  },
  ptBR // Localização em português
);

// Componente interno que usa o contexto de autenticação
const AppContent: React.FC = () => {
  const { isAuthenticated, primeiroLogin, setPrimeiroLogin, isVisitante } = useAuth();

  // Se não estiver autenticado, mostra a tela de login
  if (!isAuthenticated) {
    return <Login />;
  }

  // Se for primeiro login, mostra tela de redefinição de senha
  if (primeiroLogin) {
    return (
      <TrocaSenha 
        onSucesso={() => {
          setPrimeiroLogin(false);
          // Recarrega os dados do usuário para atualizar o estado
          window.location.reload();
        }} 
      />
    );
  }

  // Se estiver autenticado, mostra o sistema principal
  return (
    <AppProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Layout />}>
            {/* Rota principal - visitantes são redirecionados para frequência */}
            <Route index element={isVisitante() ? <Navigate to="/frequencia" replace /> : <RegisterPoint />} />
            <Route path="frequencia" element={<ViewPoints />} />
            {/* Rotas restritas - não acessíveis para visitantes */}
            <Route path="usuarios" element={isVisitante() ? <Navigate to="/frequencia" replace /> : <UserManagement />} />
            <Route path="usuarios/cadastrar" element={isVisitante() ? <Navigate to="/frequencia" replace /> : <RegisterUser />} />
            <Route path="solicitacoes" element={isVisitante() ? <Navigate to="/frequencia" replace /> : <Solicitacoes />} />
            <Route path="configuracoes" element={isVisitante() ? <Navigate to="/frequencia" replace /> : <Configuracoes />} />
          </Route>
        </Routes>
      </Router>
    </AppProvider>
  );
};

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
