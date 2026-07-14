import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import bgImage from '../assets/login_background.png';

export default function Login() {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(email, password);
    } catch (err) {
      const msg = err.response?.data?.mensaje
        || (typeof err.response?.data === 'object' ? Object.values(err.response.data).join(', ') : null)
        || 'Error al iniciar sesión — verifique que el backend esté corriendo';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-split">
      <div className="login-aside" style={{ backgroundImage: `url(${bgImage})` }}>
        <div className="aside-content">
          <h1>SIGEPRO</h1>
          <p>
            Sistema integral de gestión de proyectos para TransAndina S.A.
            Planificación estratégica, seguimiento de tareas, control presupuestario y análisis de desempeño en tiempo real.
          </p>
        </div>
        <div className="aside-footer">
          &copy; {new Date().getFullYear()} TransAndina S.A. Todos los derechos reservados.
        </div>
      </div>
      <div className="login-main">
        <div className="login-form-container">
          <div className="login-header">
            <div className="login-brand">
              <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/>
              </svg>
              <span>TRANSANDINA</span>
            </div>
            <h2>Iniciar sesión</h2>
            <p>Ingresa tus credenciales para acceder a la plataforma</p>
          </div>

          {error && <div className="alert alert-danger py-2 small" style={{ borderRadius: 8 }}>{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Correo electrónico</label>
              <input
                type="email"
                className="form-control login-input"
                placeholder="admin@transandina.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="mb-4">
              <label className="form-label">Contraseña</label>
              <input
                type="password"
                className="form-control login-input"
                placeholder="123456"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button
              type="submit"
              className="btn btn-primary w-100 py-2 btn-icon"
              style={{ borderRadius: 8, height: '44px', fontWeight: 500, fontSize: '0.95rem' }}
              disabled={loading}
            >
              {loading ? 'Ingresando...' : 'Ingresar'}
            </button>
            <div className="text-center mt-4">
              <span className="login-hint">
                Prueba: <strong>admin@transandina.com</strong> / <strong>123456</strong>
              </span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
