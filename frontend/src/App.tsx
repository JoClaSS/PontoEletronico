import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { ptBR } from '@mui/material/locale';
import { AppProvider } from './contexts/AppContext';
import Layout from './components/layout/Layout';
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

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
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
    </ThemeProvider>
  );
}

export default App;
