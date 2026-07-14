import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Doughnut, Bar } from 'react-chartjs-2';
import api from '../api/axios';

export default function Dashboard() {
  const [proyectos, setProyectos] = useState([]);

  useEffect(() => {
    api.get('/proyectos').then(r => setProyectos(r.data)).catch(() => {});
  }, []);

  const contarEstado = (estado) => proyectos.filter(p => p.estado === estado).length;

  const badgeClass = (estado) => {
    const map = { PLANIFICADO: 'warning', EN_CURSO: 'info', FINALIZADO: 'success', CANCELADO: 'danger' };
    return `badge bg-${map[estado] || 'secondary'}`;
  };

  const estadoLabel = (estado) => {
    const map = { PLANIFICADO: 'Planificado', EN_CURSO: 'En Curso', FINALIZADO: 'Finalizado', CANCELADO: 'Cancelado' };
    return map[estado] || estado;
  };

  const statusData = {
    labels: ['Planificado', 'En Curso', 'Finalizado', 'Cancelado'],
    datasets: [{
      data: [contarEstado('PLANIFICADO'), contarEstado('EN_CURSO'), contarEstado('FINALIZADO'), contarEstado('CANCELADO')],
      backgroundColor: ['#f59e0b', '#3b82f6', '#10b981', '#ef4444'],
      borderWidth: 3,
      borderColor: '#fff',
    }]
  };

  const estadoColors = { PLANIFICADO: 'rgba(245,158,11,0.7)', EN_CURSO: 'rgba(59,130,246,0.7)', FINALIZADO: 'rgba(16,185,129,0.7)', CANCELADO: 'rgba(239,68,68,0.7)' };

  return (
    <div className="fade-in">
      <div className="page-header">
        <h2>Dashboard</h2>
        <p>Panel principal de control de proyectos</p>
      </div>

      <div className="row mb-4 g-3">
        <div className="col-md-3">
          <div className="stat-card stat-card-bg-primary">
            <h5 className="card-title">Total Proyectos</h5>
            <p className="display-6">{proyectos.length}</p>
          </div>
        </div>
        <div className="col-md-3">
          <div className="stat-card stat-card-bg-warning">
            <h5 className="card-title">Planificados</h5>
            <p className="display-6">{contarEstado('PLANIFICADO')}</p>
          </div>
        </div>
        <div className="col-md-3">
          <div className="stat-card stat-card-bg-info">
            <h5 className="card-title">En Curso</h5>
            <p className="display-6">{contarEstado('EN_CURSO')}</p>
          </div>
        </div>
        <div className="col-md-3">
          <div className="stat-card stat-card-bg-success">
            <h5 className="card-title">Finalizados</h5>
            <p className="display-6">{contarEstado('FINALIZADO')}</p>
          </div>
        </div>
      </div>

      <div className="row g-4 mb-4">
        <div className="col-md-5">
          <div className="card h-100">
            <div className="card-header">Distribucion de Proyectos</div>
            <div className="card-body d-flex align-items-center" style={{minHeight:'280px'}}>
              {proyectos.length > 0 ? (
                <Doughnut data={statusData} options={{
                  responsive: true, maintainAspectRatio: false,
                  plugins: {
                    legend: { position: 'bottom', labels: { padding: 16, usePointStyle: true, font: { size: 11, family: 'Inter' } } }
                  }
                }} />
              ) : (
                <p className="text-muted text-center w-100 mb-0">No hay proyectos</p>
              )}
            </div>
          </div>
        </div>
        <div className="col-md-7">
          <div className="card h-100">
            <div className="card-header">Proyectos por Estado</div>
            <div className="card-body" style={{minHeight:'280px'}}>
              {proyectos.length > 0 ? (
                <Bar data={{
                  labels: proyectos.slice(0, 8).map(p => p.nombre.length > 20 ? p.nombre.substring(0, 20) + '...' : p.nombre),
                  datasets: [{
                    label: 'Presupuesto (Bs)',
                    data: proyectos.slice(0, 8).map(p => p.presupuestoTotal || 0),
                    backgroundColor: proyectos.slice(0, 8).map(p => estadoColors[p.estado] || 'rgba(148,163,184,0.7)'),
                    borderRadius: 6,
                  }]
                }} options={{
                  responsive: true, maintainAspectRatio: false,
                  indexAxis: 'y',
                  plugins: { legend: { display: false } },
                  scales: { x: { beginAtZero: true, ticks: { callback: v => 'Bs ' + v.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2}) } } }
                }} />
              ) : (
                <p className="text-muted text-center w-100 mb-0">No hay proyectos</p>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-header d-flex justify-content-between align-items-center">
          <span>Proyectos Recientes</span>
          <Link to="/proyectos" className="btn btn-sm btn-primary btn-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg> Ver todos</Link>
        </div>
        <div className="card-body p-0">
          <table className="table table-striped mb-0">
            <thead><tr><th style={{width:40}}>#</th><th>Nombre</th><th>Estado</th><th>Inicio</th><th>Fin</th><th></th></tr></thead>
            <tbody>
              {proyectos.slice(0, 5).map((p, i) => (
                <tr key={p.id}>
                  <td className="text-muted text-center" style={{width:40}}>{i + 1}</td>
                  <td className="fw-semibold">{p.nombre}</td>
                  <td><span className={`badge bg-${p.estado === 'EN_CURSO' ? 'info' : p.estado === 'FINALIZADO' ? 'success' : p.estado === 'CANCELADO' ? 'danger' : 'warning'}`}>{estadoLabel(p.estado)}</span></td>
                  <td>{p.fechaInicio}</td>
                  <td>{p.fechaFin}</td>
                  <td><Link to={`/proyectos/${p.id}`} className="btn btn-sm btn-outline-primary btn-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg> Abrir</Link></td>
                </tr>
              ))}
              {proyectos.length === 0 && <tr><td colSpan={6} className="text-center py-4 text-muted">No hay proyectos registrados</td></tr>}
            </tbody>
          </table>
          <div className="px-3 py-2 border-top small text-muted">Total: {proyectos.length} proyecto(s)</div>
        </div>
      </div>
    </div>
  );
}
