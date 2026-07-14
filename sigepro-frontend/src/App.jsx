import { Routes, Route, Navigate } from 'react-router-dom';
import DashboardLayout from './components/DashboardLayout';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Usuarios from './pages/Usuarios';
import Proyectos from './pages/Proyectos';
import ProyectoDetalle from './pages/ProyectoDetalle';
import Reportes from './pages/Reportes';
import Auditoria from './pages/Auditoria';
import { useAuth } from './context/AuthContext';

export default function App() {
  const { usuario } = useAuth();

  if (!usuario) return <Login />;

  return (
    <DashboardLayout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/usuarios" element={
          <ProtectedRoute roles={['ADMINISTRADOR']}><Usuarios /></ProtectedRoute>
        } />
        <Route path="/proyectos" element={<Proyectos />} />
        <Route path="/proyectos/:id" element={<ProyectoDetalle />} />
        <Route path="/reportes" element={<Reportes />} />
        <Route path="/auditoria" element={
          <ProtectedRoute roles={['ADMINISTRADOR']}><Auditoria /></ProtectedRoute>
        } />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </DashboardLayout>
  );
}
