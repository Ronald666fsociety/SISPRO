import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { usuario, logout, tieneRol } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path || (path !== '/' && location.pathname.startsWith(path));

  const rolLabels = { ADMINISTRADOR: 'Admin', JEFE_PROYECTO: 'Jefe Proy.', USUARIO: 'Usuario' };

  if (!usuario) return null;

  return (
    <nav className="navbar navbar-expand-lg navbar-dark mb-4">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">SIGEPRO</Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto">
            <li className="nav-item"><Link className={`nav-link${isActive('/') && location.pathname === '/' ? ' active' : ''}`} to="/">Dashboard</Link></li>
            <li className="nav-item"><Link className={`nav-link${isActive('/proyectos') ? ' active' : ''}`} to="/proyectos">Proyectos</Link></li>
            <li className="nav-item"><Link className={`nav-link${isActive('/reportes') ? ' active' : ''}`} to="/reportes">Reportes</Link></li>
            {tieneRol(['ADMINISTRADOR']) && (
              <>
                <li className="nav-item"><Link className={`nav-link${isActive('/usuarios') ? ' active' : ''}`} to="/usuarios">Usuarios</Link></li>
                <li className="nav-item"><Link className={`nav-link${isActive('/auditoria') ? ' active' : ''}`} to="/auditoria">Auditoria</Link></li>
              </>
            )}
          </ul>
          <span className="navbar-text me-3">
            <strong>{usuario.nombre}</strong> &middot; {rolLabels[usuario.rol] || usuario.rol}
          </span>
          <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>Salir</button>
        </div>
      </div>
    </nav>
  );
}
