import { useState, useEffect } from 'react';
import { Bar, Doughnut } from 'react-chartjs-2';
import api from '../api/axios';

export default function Reportes() {
  const [proyectos, setProyectos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [selProyecto, setSelProyecto] = useState('');
  const [selUsuario, setSelUsuario] = useState('');
  const [presupuesto, setPresupuesto] = useState(null);
  const [semaforo, setSemaforo] = useState(null);
  const [cargaTrabajo, setCargaTrabajo] = useState(null);

  useEffect(() => {
    api.get('/proyectos').then(r => setProyectos(r.data));
    api.get('/usuarios').then(r => setUsuarios(r.data));
  }, []);

  const consultarPresupuesto = async () => {
    if (!selProyecto) return;
    const r = await api.get(`/reportes/presupuesto/${selProyecto}`);
    setPresupuesto(r.data);
  };

  const consultarSemaforo = async () => {
    if (!selProyecto) return;
    const r = await api.get(`/reportes/semaforo/${selProyecto}`);
    setSemaforo(r.data);
  };

  const consultarCarga = async () => {
    if (!selUsuario) return;
    const r = await api.get(`/reportes/carga-trabajo/${selUsuario}`);
    setCargaTrabajo(r.data);
  };

  const presupuestoChart = presupuesto ? {
    labels: ['Presupuesto', 'Costo Real'],
    datasets: [{
      label: 'Bs',
      data: [presupuesto.presupuestoTotalEstimado, presupuesto.costoTotalEjecutado],
      backgroundColor: ['rgba(59, 130, 246, 0.85)', 'rgba(239, 68, 68, 0.85)'],
      borderColor: ['#3b82f6', '#ef4444'],
      borderWidth: 2,
      borderRadius: 6,
    }]
  } : null;

  const semaforoColors = { VERDE: '#10b981', AMARILLO: '#f59e0b', ROJO: '#ef4444' };

  return (
    <div className="fade-in">
      <div className="page-header">
        <h2>Reportes</h2>
        <p>Indicadores y analisis de proyectos</p>
      </div>

      <div className="row g-4">
        {/* Presupuesto vs Costo */}
        <div className="col-lg-6">
          <div className="card h-100">
            <div className="card-header">Presupuesto vs Costo Real</div>
            <div className="card-body">
              <div className="input-group mb-3">
                <select className="form-select" value={selProyecto} onChange={e => { setSelProyecto(e.target.value); setPresupuesto(null); }}>
                  <option value="">Seleccionar proyecto</option>
                  {proyectos.map(p => <option key={p.id} value={p.id}>{p.nombre}</option>)}
                </select>
                <button className="btn btn-primary btn-icon" onClick={consultarPresupuesto}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg> Consultar</button>
              </div>
              {presupuesto ? (
                <div>
                  <div className="d-flex justify-content-around text-center mb-3">
                    <div>
                      <div style={{fontSize:'0.75rem',color:'var(--gray-500)'}}>Presupuesto</div>
                      <div className="fw-bold fs-5" style={{color:'var(--primary)'}}>Bs {presupuesto.presupuestoTotalEstimado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</div>
                    </div>
                    <div>
                      <div style={{fontSize:'0.75rem',color:'var(--gray-500)'}}>Costo Real</div>
                      <div className="fw-bold fs-5" style={{color:'var(--danger)'}}>Bs {presupuesto.costoTotalEjecutado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</div>
                    </div>
                    <div>
                      <div style={{fontSize:'0.75rem',color:'var(--gray-500)'}}>Diferencia</div>
                      <div className={`fw-bold fs-5 ${presupuesto.diferencia >= 0 ? 'text-success' : 'text-danger'}`}>
                        Bs {presupuesto.diferencia?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}
                      </div>
                    </div>
                  </div>
                  {presupuestoChart && (
                    <div style={{height:'200px'}}>
                      <Bar data={presupuestoChart} options={{
                        responsive: true, maintainAspectRatio: false,
                        plugins: { legend: { display: false } },
                        scales: { y: { beginAtZero: true, ticks: { callback: v => 'Bs ' + v.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2}) } } }
                      }} />
                    </div>
                  )}
                  <div className="text-center mt-3">
                    <span className={`badge fs-6 ${presupuesto.estado === 'DENTRO_PRESUPUESTO' ? 'bg-success' : 'bg-danger'}`}>
                      {presupuesto.estado === 'DENTRO_PRESUPUESTO' ? 'Dentro del presupuesto' : 'Sobre presupuesto'}
                    </span>
                  </div>
                </div>
              ) : (
                <p className="text-muted text-center py-4 mb-0">Seleccione un proyecto y consulte</p>
              )}
            </div>
          </div>
        </div>

        {/* Semaforo de Estado */}
        <div className="col-lg-6">
          <div className="card h-100">
            <div className="card-header">Semaforo de Estado</div>
            <div className="card-body">
              <div className="input-group mb-3">
                <select className="form-select" value={selProyecto} onChange={e => { setSelProyecto(e.target.value); setSemaforo(null); }}>
                  <option value="">Seleccionar proyecto</option>
                  {proyectos.map(p => <option key={p.id} value={p.id}>{p.nombre}</option>)}
                </select>
                <button className="btn btn-primary btn-icon" onClick={consultarSemaforo}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg> Consultar</button>
              </div>
              {semaforo ? (
                <div className="text-center">
                  <div className="d-flex align-items-center justify-content-center gap-3 mb-3">
                    <div style={{
                      width: 80, height: 80, borderRadius: '50%',
                      background: semaforoColors[semaforo.color] || '#94a3b8',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      boxShadow: `0 0 30px ${semaforoColors[semaforo.color]}40`
                    }}>
                      <span className="fw-bold fs-1 text-white">
                        {semaforo.color === 'VERDE' ? '✓' : semaforo.color === 'AMARILLO' ? '!' : '✕'}
                      </span>
                    </div>
                    <div className="text-start">
                      <div className={`badge fs-6 px-3 py-2 mb-1 bg-${semaforo.color === 'VERDE' ? 'success' : semaforo.color === 'AMARILLO' ? 'warning' : 'danger'}`}>
                        {semaforo.color}
                      </div>
                      <div style={{fontSize:'0.8rem',color:'var(--gray-500)'}}>
                        {semaforo.avanceReal?.toFixed(1)}% avance real
                      </div>
                    </div>
                  </div>
                  <div className="row g-2 mb-3">
                    <div className="col-6">
                      <div className="p-3 rounded" style={{background:'var(--gray-50)'}}>
                        <div style={{fontSize:'0.75rem',color:'var(--gray-500)'}}>Avance Real</div>
                        <div className="fw-bold fs-5">{semaforo.avanceReal?.toFixed(1)}%</div>
                      </div>
                    </div>
                    <div className="col-6">
                      <div className="p-3 rounded" style={{background:'var(--gray-50)'}}>
                        <div style={{fontSize:'0.75rem',color:'var(--gray-500)'}}>Avance Planif.</div>
                        <div className="fw-bold fs-5">{semaforo.avancePlanificado?.toFixed(1)}%</div>
                      </div>
                    </div>
                    <div className="col-6">
                      <div className="p-3 rounded" style={{background:'var(--gray-50)'}}>
                        <div style={{fontSize:'0.75rem',color:'var(--gray-500)'}}>Retraso</div>
                        <div className="fw-bold fs-5">{semaforo.retrasoPorcentaje?.toFixed(1)}%</div>
                      </div>
                    </div>
                    <div className="col-6">
                      <div className="p-3 rounded" style={{background:'var(--gray-50)'}}>
                        <div style={{fontSize:'0.75rem',color:'var(--gray-500)'}}>Sobrecosto</div>
                        <div className="fw-bold fs-5">{semaforo.sobreCostoPorcentaje?.toFixed(1)}%</div>
                      </div>
                    </div>
                  </div>
                  <div style={{height:'180px'}}>
                    <Doughnut data={{
                      labels: ['Avance Real', 'Restante'],
                      datasets: [{
                        data: [semaforo.avanceReal || 0, 100 - (semaforo.avanceReal || 0)],
                        backgroundColor: [semaforoColors[semaforo.color] || '#94a3b8', '#e2e8f0'],
                        borderWidth: 0,
                      }]
                    }} options={{
                      responsive: true, maintainAspectRatio: false,
                      cutout: '70%',
                      plugins: {
                        legend: { display: false },
                      },
                    }} />
                  </div>
                </div>
              ) : (
                <p className="text-muted text-center py-4 mb-0">Seleccione un proyecto y consulte</p>
              )}
            </div>
          </div>
        </div>

        {/* Carga de Trabajo */}
        <div className="col-lg-6">
          <div className="card h-100">
            <div className="card-header">Carga de Trabajo por Recurso</div>
            <div className="card-body">
              <div className="input-group mb-3">
                <select className="form-select" value={selUsuario} onChange={e => { setSelUsuario(e.target.value); setCargaTrabajo(null); }}>
                  <option value="">Seleccionar usuario</option>
                  {usuarios.map(u => <option key={u.id} value={u.id}>{u.nombre}</option>)}
                </select>
                <button className="btn btn-primary btn-icon" onClick={consultarCarga}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg> Consultar</button>
              </div>
              {cargaTrabajo ? (
                <div>
                  <h6 className="fw-bold mb-3">{cargaTrabajo.nombreUsuario}</h6>
                  <div className="row g-2 mb-3">
                    <div className="col-4">
                      <div className="p-3 rounded text-center text-white" style={{background:'var(--primary-gradient)'}}>
                        <div style={{fontSize:'0.7rem',opacity:0.8}}>Hrs Est.</div>
                        <div className="fw-bold fs-5">{cargaTrabajo.totalHorasEstimadas?.toFixed(1)}</div>
                      </div>
                    </div>
                    <div className="col-4">
                      <div className="p-3 rounded text-center text-white" style={{background:'linear-gradient(135deg,#d97706,#f59e0b)'}}>
                        <div style={{fontSize:'0.7rem',opacity:0.8}}>Hrs Reales</div>
                        <div className="fw-bold fs-5">{cargaTrabajo.totalHorasReales?.toFixed(1)}</div>
                      </div>
                    </div>
                    <div className="col-4">
                      <div className="p-3 rounded text-center text-white" style={{background:'linear-gradient(135deg,#059669,#10b981)'}}>
                        <div style={{fontSize:'0.7rem',opacity:0.8}}>Tareas</div>
                        <div className="fw-bold fs-5">{cargaTrabajo.cantidadTareas}</div>
                      </div>
                    </div>
                  </div>
                  <div style={{height:'200px'}}>
                    <Bar data={{
                      labels: ['Horas Est.', 'Horas Reales'],
                      datasets: [{
                        label: 'Horas',
                        data: [cargaTrabajo.totalHorasEstimadas, cargaTrabajo.totalHorasReales],
                        backgroundColor: ['rgba(59, 130, 246, 0.85)', 'rgba(245, 158, 11, 0.85)'],
                        borderColor: ['#3b82f6', '#f59e0b'],
                        borderWidth: 2,
                        borderRadius: 6,
                      }]
                    }} options={{
                      responsive: true, maintainAspectRatio: false,
                      plugins: { legend: { display: false } },
                      scales: { y: { beginAtZero: true, ticks: { callback: v => v + ' hrs' } } }
                    }} />
                  </div>
                </div>
              ) : (
                <p className="text-muted text-center py-4 mb-0">Seleccione un usuario y consulte</p>
              )}
            </div>
          </div>
        </div>

        {/* Distribucion de Proyectos */}
        <div className="col-lg-6">
          <div className="card h-100">
            <div className="card-header">Distribucion de Proyectos</div>
            <div className="card-body">
              {proyectos.length > 0 ? (
                <div>
                  <div className="row g-2 mb-3">
                    {['PLANIFICADO', 'EN_CURSO', 'FINALIZADO', 'CANCELADO'].map(est => {
                      const count = proyectos.filter(p => p.estado === est).length;
                      const labels = { PLANIFICADO: 'Planificado', EN_CURSO: 'En Curso', FINALIZADO: 'Finalizado', CANCELADO: 'Cancelado' };
                      const colors = { PLANIFICADO: '#f59e0b', EN_CURSO: '#3b82f6', FINALIZADO: '#10b981', CANCELADO: '#ef4444' };
                      return (
                        <div className="col-3" key={est}>
                          <div className="p-3 rounded text-center text-white" style={{background: colors[est]}}>
                            <div style={{fontSize:'0.65rem',opacity:0.8,textTransform:'uppercase',letterSpacing:'0.5px'}}>{labels[est]}</div>
                            <div className="fw-bold fs-4">{count}</div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                  <div style={{height:'220px'}}>
                    <Doughnut data={{
                      labels: ['Planificado', 'En Curso', 'Finalizado', 'Cancelado'],
                      datasets: [{
                        data: [
                          proyectos.filter(p => p.estado === 'PLANIFICADO').length,
                          proyectos.filter(p => p.estado === 'EN_CURSO').length,
                          proyectos.filter(p => p.estado === 'FINALIZADO').length,
                          proyectos.filter(p => p.estado === 'CANCELADO').length,
                        ],
                        backgroundColor: ['#f59e0b', '#3b82f6', '#10b981', '#ef4444'],
                        borderWidth: 3,
                        borderColor: '#fff',
                      }]
                    }} options={{
                      responsive: true, maintainAspectRatio: false,
                      plugins: {
                        legend: { position: 'bottom', labels: { padding: 16, usePointStyle: true, font: { size: 11, family: 'Inter' } } }
                      }
                    }} />
                  </div>
                </div>
              ) : (
                <p className="text-muted text-center py-4 mb-0">No hay proyectos registrados</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
