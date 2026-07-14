import { useState, useEffect } from 'react';

import api from '../api/axios';

export default function Auditoria() {
  const [logs, setLogs] = useState([]);
  const [filtroEntidad, setFiltroEntidad] = useState('');
  const [filtroId, setFiltroId] = useState('');


  const load = async () => {
    if (filtroEntidad && filtroId) {
      const r = await api.get(`/auditoria/${filtroEntidad}/${filtroId}`);
      setLogs(r.data);
    } else {
      const r = await api.get('/auditoria');
      setLogs(r.data);
    }
  };

  useEffect(() => { load(); }, []);

  const descargarReporteAuditoria = async () => {
    try {
      const r = await api.get('/exportar/pdf/auditoria', { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([r.data], { type: 'application/pdf' }));
      const a = document.createElement('a'); a.href = url; a.download = 'reporte_auditoria.pdf';
      document.body.appendChild(a); a.click(); document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (e) { alert('Error al descargar el reporte'); }
  };

  const handleFilter = (e) => {
    e.preventDefault();
    load();
  };

  return (
    <div className="fade-in">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2>Auditoria</h2>
          <p style={{ color: 'var(--gray-500)', fontSize: '0.875rem', margin: 0 }}>Registro de actividades del sistema</p>
        </div>
        <button className="btn btn-outline-danger btn-icon mb-3" onClick={descargarReporteAuditoria}>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg> Reporte PDF
        </button>
      </div>

      <div className="card mb-4">
        <div className="card-body">
          <form onSubmit={handleFilter} className="row g-2">
            <div className="col-md-4">
              <label className="form-label">Entidad</label>
              <select className="form-select" value={filtroEntidad} onChange={e => setFiltroEntidad(e.target.value)}>
                <option value="">Todas las entidades</option>
                <option value="Proyecto">Proyecto</option>
                <option value="Usuario">Usuario</option>
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">ID de entidad</label>
              <input type="number" className="form-control" placeholder="ID entidad" value={filtroId} onChange={e => setFiltroId(e.target.value)} />
            </div>
            <div className="col-md-3 d-flex align-items-end gap-1">
              <button type="submit" className="btn btn-primary">Filtrar</button>
              <button type="button" className="btn btn-secondary" onClick={() => { setFiltroEntidad(''); setFiltroId(''); load(); }}>Limpiar</button>
            </div>
          </form>
        </div>
      </div>

      <div className="card">
        <div className="card-body p-0">
          <table className="table mb-0">
            <thead><tr><th style={{width:40}}>#</th><th>Fecha</th><th>Usuario</th><th>Accion</th><th>Entidad</th><th>ID</th></tr></thead>
            <tbody>
              {logs.map((l, i) => (
                <tr key={l.id}>
                  <td className="text-muted text-center" style={{width:40}}>{i + 1}</td>
                  <td>{new Date(l.fecha).toLocaleString('es-PE').replace(',', '')}</td>
                  <td className="fw-semibold">{l.nombreUsuario}</td>
                  <td>
                    <span className={`badge bg-${l.accion === 'CREAR' ? 'success' : l.accion === 'ACTUALIZAR' ? 'warning' : 'danger'}`}>
                      {l.accion === 'CREAR' ? 'Creacion' : l.accion === 'ACTUALIZAR' ? 'Actualizacion' : 'Eliminacion'}
                    </span>
                  </td>
                  <td>{l.entidad}</td>
                  <td>{l.idEntidad}</td>
                </tr>
              ))}
              {logs.length === 0 && <tr><td colSpan={6} className="text-center py-4 text-muted">Sin registros de auditoria</td></tr>}
            </tbody>
          </table>
          <div className="px-3 py-2 border-top small text-muted">Total: {logs.length} registro(s)</div>
        </div>
      </div>

    </div>
  );
}
