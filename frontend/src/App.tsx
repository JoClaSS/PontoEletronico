import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { ptBR } from '@mui/material/locale';
import { KeycloakProvider, useKeycloak } from './contexts/KeycloakContext';
import { AppProvider } from './contexts/AppContext';
import Layout from './components/layout/Layout';
import Login from './components/auth/Login';
import RegisterPoint from './components/point/RegisterPoint';
import ViewPoints from './components/point/ViewPoints';
import Solicitacoes from './components/point/Solicitacoes';
import RegisterUser from './components/user/RegisterUser';
import UserManagement from './components/user/UserManagement';

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

// Componente interno que usa o contexto do Keycloak
const AppContent: React.FC = () => {
  const { isAuthenticated } = useKeycloak();

  // Se não estiver autenticado, mostra a tela de login
  if (!isAuthenticated) {
    return <Login />;
  }

  // Se estiver autenticado, mostra o sistema principal
  return (
    <AppProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<RegisterPoint />} />
            <Route path="frequencia" element={<ViewPoints />} />
            <Route path="usuarios" element={<UserManagement />} />
            <Route path="usuarios/cadastrar" element={<RegisterUser />} />
            <Route path="solicitacoes" element={<Solicitacoes />} />
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
      <KeycloakProvider>
        <AppContent />
      </KeycloakProvider>
    </ThemeProvider>
  );
}

export default App;
