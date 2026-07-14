import { createContext, useContext, useState } from 'react';
import api from '../api/axios';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => {
    const stored = localStorage.getItem('usuario');
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const data = res.data;
    localStorage.setItem('token', data.token);
    localStorage.setItem('usuario', JSON.stringify(data));
    setUsuario(data);
    return data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    setUsuario(null);
  };

  const tieneRol = (roles) => {
    if (!usuario) return false;
    return roles.includes(usuario.rol);
  };

  return (
    <AuthContext.Provider value={{ usuario, login, logout, tieneRol }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
