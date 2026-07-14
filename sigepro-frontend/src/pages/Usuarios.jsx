import { useState, useEffect } from 'react';

import api from '../api/axios';
import { confirmarEliminacion, notificar } from '../utils/feedback';

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({ nombre: '', email: '', password: '', rol: 'USUARIO' });
  const [error, setError] = useState('');
  const [nombreValido, setNombreValido] = useState(true);

  const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]*$/;

  const handleNombreChange = (e) => {
    const valor = e.target.value;
    setNombreValido(soloLetras.test(valor));
    setForm({...form, nombre: valor});
  };

  const load = () => api.get('/usuarios').then(r => setUsuarios(r.data));

  useEffect(() => { load(); }, []);

  const descargarReporteUsuarios = async () => {
    try {
      const r = await api.get('/exportar/pdf/usuarios', { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([r.data], { type: 'application/pdf' }));
      const a = document.createElement('a'); a.href = url; a.download = 'reporte_usuarios.pdf';
      document.body.appendChild(a); a.click(); document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (e) { alert('Error al descargar el reporte'); }
  };

  const openCreate = () => {
    setEditId(null);
    setForm({ nombre: '', email: '', password: '', rol: 'USUARIO' });
    setShowForm(true);
    setError('');
  };

  const openEdit = (u) => {
    setEditId(u.id);
    setForm({ nombre: u.nombre, email: u.email, password: '', rol: u.rol });
    setShowForm(true);
    setError('');
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      if (editId) {
        await api.put(`/usuarios/${editId}`, form);
        notificar.actualizado('Usuario');
      } else {
        await api.post('/usuarios', form);
        notificar.creado('Usuario');
      }
      setShowForm(false);
      load();
    } catch (err) {
      const errMsg = err.response?.data?.mensaje || Object.values(err.response?.data || {}).join(', ') || 'Error';
      setError(errMsg);
      notificar.error(errMsg);
    }
  };

  const handleDelete = (id, nombre) => {
    confirmarEliminacion({
      tipo: 'Usuario',
      itemNombre: nombre,
      alConfirmar: async () => {
        try {
          await api.delete(`/usuarios/${id}`);
          notificar.eliminado('Usuario');
          load();
        } catch (err) {
          notificar.error(err.response?.data?.mensaje || 'Error al eliminar el usuario');
        }
      }
    });
  };

  return (
    <div className="fade-in">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2>Usuarios</h2>
          <p style={{ color: 'var(--gray-500)', fontSize: '0.875rem', margin: 0 }}>Gestion de usuarios del sistema</p>
        </div>
        <button className="btn btn-primary btn-icon" onClick={openCreate}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg> Nuevo Usuario</button>
        <button className="btn btn-outline-danger btn-icon ms-2" onClick={descargarReporteUsuarios}>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg> Reporte PDF
        </button>
      </div>

      {showForm && (
        <div className="card mb-4">
          <div className="card-body">
            <h5 className="mb-3">{editId ? 'Editar' : 'Nuevo'} Usuario</h5>
            {error && <div className="alert alert-danger py-2">{error}</div>}
            <form onSubmit={handleSave}>
              <div className="row g-3">
                <div className="col-md-4">
                  <label className="form-label">Nombre</label>
                  <input className={`form-control ${!nombreValido ? 'is-invalid' : ''}`} placeholder="Nombre" value={form.nombre} onChange={handleNombreChange} required />
                  {!nombreValido && <div className="invalid-feedback">Solo se permiten letras y espacios</div>}
                </div>
                <div className="col-md-3">
                  <label className="form-label">Email</label>
                  <input type="email" className="form-control" placeholder="Email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} required />
                </div>
                <div className="col-md-2">
                  <label className="form-label">Contrasena</label>
                  <input type="password" className="form-control" placeholder={editId ? 'Nueva (opcional)' : 'Password'} value={form.password} onChange={e => setForm({...form, password: e.target.value})} required={!editId} />
                </div>
                <div className="col-md-2">
                  <label className="form-label">Rol</label>
                  <select className="form-select" value={form.rol} onChange={e => setForm({...form, rol: e.target.value})}>
                    <option value="ADMINISTRADOR">Administrador</option>
                    <option value="JEFE_PROYECTO">Jefe Proyecto</option>
                    <option value="USUARIO">Usuario</option>
                  </select>
                </div>
                <div className="col-md-3 d-flex align-items-end gap-2">
                  <button type="submit" className="btn btn-success btn-icon px-4"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg> Guardar</button>
                  <button type="button" className="btn btn-outline-secondary btn-icon px-4" onClick={() => setShowForm(false)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg> Cancelar</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="card">
        <div className="card-body p-0">
          <table className="table table-striped mb-0">
            <thead><tr><th style={{width:40}}>#</th><th>Nombre</th><th>Email</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead>
            <tbody>
              {usuarios.map((u, i) => (
                <tr key={u.id}>
                  <td className="text-muted text-center" style={{width:40}}>{i + 1}</td>
                  <td className="fw-semibold">{u.nombre}</td><td>{u.email}</td>
                  <td><span className="badge bg-secondary">{u.rol === 'ADMINISTRADOR' ? 'Administrador' : u.rol === 'JEFE_PROYECTO' ? 'Jefe Proyecto' : 'Usuario'}</span></td>
                  <td>{u.activo ? <span className="badge bg-success">Activo</span> : <span className="badge bg-danger">Inactivo</span>}</td>
                  <td>
                    <button className="btn btn-sm btn-outline-primary me-1 btn-icon" onClick={() => openEdit(u)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg> Editar</button>
                    <button className="btn btn-sm btn-outline-danger btn-icon" onClick={() => handleDelete(u.id, u.nombre)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg> Eliminar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="px-3 py-2 border-top small text-muted">Total: {usuarios.length} usuario(s)</div>
        </div>
      </div>

    </div>
  );
}
