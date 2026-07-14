import { useState, useEffect, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Bar } from 'react-chartjs-2';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Form, Input, Select, Progress } from 'antd';
import { confirmarEliminacion, notificar } from '../utils/feedback';

export default function ProyectoDetalle() {
  const { id } = useParams();
  const { usuario, tieneRol } = useAuth();
  const puedeEditar = tieneRol(['ADMINISTRADOR', 'JEFE_PROYECTO']);
  const ganttRef = useRef(null);

  const [proyecto, setProyecto] = useState(null);
  const [tareas, setTareas] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [deps, setDeps] = useState([]);
  const [asignaciones, setAsignaciones] = useState([]);
  const [activeTab, setActiveTab] = useState('tareas');

  const [showTareaForm, setShowTareaForm] = useState(false);
  const [editTareaId, setEditTareaId] = useState(null);
  const [formRef] = Form.useForm();

  const [showDepForm, setShowDepForm] = useState(false);
  const [depForm, setDepForm] = useState({ idTareaOrigen: '', idTareaDestino: '', tipo: 'FIN_INICIO' });

  const [showAsigForm, setShowAsigForm] = useState(false);
  const [asigForm, setAsigForm] = useState({ idTarea: '', idUsuario: '', horasEstimadas: 0, horasReales: 0 });

  useEffect(() => {
    api.get(`/proyectos/${id}`).then(r => setProyecto(r.data));
    api.get(`/tareas/proyecto/${id}`).then(r => setTareas(r.data));
    api.get('/usuarios').then(r => setUsuarios(r.data)).catch(() => {});
    api.get(`/dependencias/proyecto/${id}`).then(r => setDeps(r.data)).catch(() => {});
    api.get(`/asignaciones/proyecto/${id}`).then(r => setAsignaciones(r.data)).catch(() => {});
  }, [id]);

  const reloadTareas = async () => {
    const r = await api.get(`/tareas/proyecto/${id}`);
    setTareas(r.data);
    api.get(`/proyectos/${id}`).then(r => setProyecto(r.data));
    api.get(`/dependencias/proyecto/${id}`).then(r => setDeps(r.data)).catch(() => {});
    api.get(`/asignaciones/proyecto/${id}`).then(r => setAsignaciones(r.data)).catch(() => {});
  };

  const openCreateTarea = () => {
    setEditTareaId(null);
    setShowTareaForm(true);
    setTimeout(() => {
      formRef.resetFields();
    }, 50);
  };

  const openEditTarea = (t) => {
    setEditTareaId(t.id);
    setShowTareaForm(true);
    setTimeout(() => {
      formRef.setFieldsValue({
        nombre: t.nombre,
        fechaInicio: t.fechaInicio,
        fechaFin: t.fechaFin,
        porcentajeAvance: t.porcentajeAvance,
        presupuestoEstimado: t.presupuestoEstimado || 0,
        costoEjecutado: t.costoEjecutado || 0,
        idResponsable: t.idResponsable,
        idTareaPadre: t.idTareaPadre || undefined,
      });
    }, 50);
  };

  const handleSaveTarea = async (values) => {
    const data = {
      ...values,
      idProyecto: parseInt(id),
      porcentajeAvance: parseInt(values.porcentajeAvance || 0),
      presupuestoEstimado: parseFloat(values.presupuestoEstimado) || 0,
      costoEjecutado: parseFloat(values.costoEjecutado) || 0,
      idResponsable: parseInt(values.idResponsable),
      idTareaPadre: values.idTareaPadre ? parseInt(values.idTareaPadre) : null
    };
    try {
      if (editTareaId) {
        await api.put(`/tareas/${editTareaId}`, data);
        notificar.actualizado('Tarea');
      } else {
        await api.post('/tareas', data);
        notificar.creado('Tarea');
      }
      setShowTareaForm(false);
      reloadTareas();
    } catch (err) {
      notificar.error(err.response?.data?.mensaje || 'Error al guardar la tarea');
    }
  };

  const handleDeleteTarea = (tid, nombre) => {
    confirmarEliminacion({
      tipo: 'Tarea',
      itemNombre: nombre,
      alConfirmar: async () => {
        try {
          await api.delete(`/tareas/${tid}`);
          notificar.eliminado('Tarea');
          reloadTareas();
        } catch (err) {
          notificar.error(err.response?.data?.mensaje || 'Error al eliminar la tarea');
        }
      }
    });
  };

  const handleCreateDep = async (e) => {
    e.preventDefault();
    try {
      await api.post('/dependencias', { ...depForm, idTareaOrigen: parseInt(depForm.idTareaOrigen), idTareaDestino: parseInt(depForm.idTareaDestino) });
      notificar.creado('Dependencia');
      setShowDepForm(false);
      setDepForm({ idTareaOrigen: '', idTareaDestino: '', tipo: 'FIN_INICIO' });
      reloadTareas();
    } catch (err) {
      notificar.error(err.response?.data?.mensaje || 'Error al crear la dependencia');
    }
  };

  const handleDeleteDep = (did, desc) => {
    confirmarEliminacion({
      tipo: 'Dependencia',
      itemNombre: desc,
      alConfirmar: async () => {
        try {
          await api.delete(`/dependencias/${did}`);
          notificar.eliminado('Dependencia');
          reloadTareas();
        } catch (err) {
          notificar.error(err.response?.data?.mensaje || 'Error al eliminar la dependencia');
        }
      }
    });
  };

  const handleCreateAsig = async (e) => {
    e.preventDefault();
    try {
      await api.post('/asignaciones', { ...asigForm, idTarea: parseInt(asigForm.idTarea), idUsuario: parseInt(asigForm.idUsuario), horasEstimadas: parseFloat(asigForm.horasEstimadas) || 0, horasReales: parseFloat(asigForm.horasReales) || 0 });
      notificar.creado('Asignación');
      setShowAsigForm(false);
      setAsigForm({ idTarea: '', idUsuario: '', horasEstimadas: 0, horasReales: 0 });
      reloadTareas();
    } catch (err) {
      notificar.error(err.response?.data?.mensaje || 'Error al crear la asignación');
    }
  };

  const handleDeleteAsig = (aid, desc) => {
    confirmarEliminacion({
      tipo: 'Asignación',
      itemNombre: desc,
      alConfirmar: async () => {
        try {
          await api.delete(`/asignaciones/${aid}`);
          notificar.eliminado('Asignación');
          reloadTareas();
        } catch (err) {
          notificar.error(err.response?.data?.mensaje || 'Error al eliminar la asignación');
        }
      }
    });
  };

  const exportPdf = async () => {
    try {
      const r = await api.get(`/exportar/pdf/${id}`, { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([r.data], { type: 'application/pdf' }));
      const a = document.createElement('a'); a.href = url; a.download = `plan_proyecto_${id}.pdf`;
      document.body.appendChild(a); a.click(); document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (e) { alert('Error al descargar el PDF'); }
  };
  const exportExcel = async () => {
    try {
      const r = await api.get(`/exportar/excel/${id}`, { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([r.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }));
      const a = document.createElement('a'); a.href = url; a.download = `plan_proyecto_${id}.xlsx`;
      document.body.appendChild(a); a.click(); document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (e) { alert('Error al descargar el Excel'); }
  };

  if (!proyecto) return <div className="text-center py-5"><div className="spinner-border text-primary"></div><p className="mt-2 text-muted">Cargando proyecto...</p></div>;

  const tareasRaiz = tareas.filter(t => !t.idTareaPadre);
  const subtareas = tareas.filter(t => t.idTareaPadre);

  const badgeClass = (estado) => {
    const map = { PLANIFICADO: 'warning', EN_CURSO: 'info', FINALIZADO: 'success', CANCELADO: 'danger' };
    return `badge bg-${map[estado] || 'secondary'}`;
  };

  return (
    <div className="fade-in">
      <Link to="/proyectos" className="btn btn-sm btn-outline-secondary mb-3 btn-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg> Volver a proyectos</Link>
      <div className="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h2 className="mb-1">{proyecto.nombre}</h2>
          <p style={{ color: 'var(--gray-500)', fontSize: '0.9rem', margin: 0 }}>
            Jefe: <strong>{proyecto.nombreJefeProyecto}</strong> &middot; Presupuesto: <strong>Bs {proyecto.presupuestoTotal?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</strong>
          </p>
        </div>
        <div className="d-flex gap-2 align-items-center">
          <span className={badgeClass(proyecto.estado)}>{proyecto.estado === 'EN_CURSO' ? 'En Curso' : proyecto.estado === 'FINALIZADO' ? 'Finalizado' : proyecto.estado === 'CANCELADO' ? 'Cancelado' : 'Planificado'}</span>
          <button className="btn btn-sm btn-outline-success btn-icon" onClick={exportPdf}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg> PDF</button>
          <button className="btn btn-sm btn-outline-success btn-icon" onClick={exportExcel}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="8" y1="16" x2="12" y2="16"/><line x1="8" y1="12" x2="14" y2="12"/></svg> Excel</button>
        </div>
      </div>

      <ul className="nav nav-tabs mb-3">
        <li className="nav-item"><button className={`nav-link ${activeTab === 'tareas' ? 'active' : ''}`} onClick={() => setActiveTab('tareas')}>Tareas</button></li>
        <li className="nav-item"><button className={`nav-link ${activeTab === 'gantt' ? 'active' : ''}`} onClick={() => setActiveTab('gantt')}>Gantt</button></li>
        <li className="nav-item"><button className={`nav-link ${activeTab === 'deps' ? 'active' : ''}`} onClick={() => setActiveTab('deps')}>Dependencias</button></li>
        <li className="nav-item"><button className={`nav-link ${activeTab === 'recursos' ? 'active' : ''}`} onClick={() => setActiveTab('recursos')}>Recursos</button></li>
        <li className="nav-item"><button className={`nav-link ${activeTab === 'presupuesto' ? 'active' : ''}`} onClick={() => setActiveTab('presupuesto')}>Presupuesto</button></li>
      </ul>

      {/* TAB TAREAS */}
      {activeTab === 'tareas' && (
        <div>
          <div className="d-flex gap-2 mb-2">
            {puedeEditar && <button className="btn btn-primary btn-sm btn-icon" onClick={openCreateTarea}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg> Nueva Tarea</button>}
          </div>
          {showTareaForm && (
            <div className="card mb-4" style={{ borderRadius: 12, border: '1px solid #E2E8F0', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
              <div className="card-body" style={{ padding: '24px' }}>
                <h5 className="mb-4" style={{ fontWeight: 600, color: '#1E3A5F' }}>{editTareaId ? (puedeEditar ? 'Editar Tarea' : 'Reportar Avance de Tarea') : 'Nueva Tarea'}</h5>
                <Form
                  form={formRef}
                  layout="vertical"
                  onFinish={handleSaveTarea}
                  autoComplete="off"
                >
                  <div className="row">
                    <div className="col-md-6">
                      <Form.Item
                        name="nombre"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Nombre de la tarea</span>}
                        rules={[{ required: true, message: 'El nombre es obligatorio' }]}
                      >
                        <Input placeholder="Ej. Diseñar modelo de datos" size="large" disabled={!puedeEditar} />
                      </Form.Item>
                    </div>
                    <div className="col-md-3">
                      <Form.Item
                        name="fechaInicio"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Fecha de inicio</span>}
                        rules={[{ required: true, message: 'La fecha es obligatoria' }]}
                      >
                        <Input type="date" size="large" disabled={!puedeEditar} />
                      </Form.Item>
                    </div>
                    <div className="col-md-3">
                      <Form.Item
                        name="fechaFin"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Fecha de fin</span>}
                        rules={[{ required: true, message: 'La fecha es obligatoria' }]}
                      >
                        <Input type="date" size="large" disabled={!puedeEditar} />
                      </Form.Item>
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-2">
                      <Form.Item
                        name="porcentajeAvance"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>% Avance</span>}
                        rules={[
                          { required: true, message: 'Requerido' }
                        ]}
                      >
                        <Input type="number" placeholder="0" size="large" min={0} max={100} />
                      </Form.Item>
                    </div>
                    <div className="col-md-5">
                      <Form.Item
                        name="idResponsable"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Responsable</span>}
                        rules={[{ required: true, message: 'Debe asignar un responsable' }]}
                      >
                        <Select placeholder="Seleccionar..." size="large" disabled={!puedeEditar}>
                          {usuarios.map(u => (
                            <Select.Option key={u.id} value={u.id}>
                              {u.nombre}
                            </Select.Option>
                          ))}
                        </Select>
                      </Form.Item>
                    </div>
                    <div className="col-md-5">
                      <Form.Item
                        name="idTareaPadre"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Tarea Padre (Opcional)</span>}
                      >
                        <Select placeholder="Ninguna" size="large" allowClear disabled={!puedeEditar}>
                          {tareasRaiz.filter(t => t.id !== editTareaId).map(t => (
                            <Select.Option key={t.id} value={t.id}>
                              {t.nombre}
                            </Select.Option>
                          ))}
                        </Select>
                      </Form.Item>
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-6">
                      <Form.Item
                        name="presupuestoEstimado"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Presupuesto Bs</span>}
                      >
                        <Input type="number" placeholder="0.00" step="0.01" size="large" disabled={!puedeEditar} />
                      </Form.Item>
                    </div>
                    <div className="col-md-6">
                      <Form.Item
                        name="costoEjecutado"
                        label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Costo Real Bs</span>}
                      >
                        <Input type="number" placeholder="0.00" step="0.01" size="large" />
                      </Form.Item>
                    </div>
                  </div>

                  <div style={{ marginTop: '16px', display: 'flex', gap: '12px' }}>
                    <button type="submit" className="btn btn-primary">
                      Guardar Tarea
                    </button>
                    <button type="button" className="btn btn-outline-secondary" onClick={() => setShowTareaForm(false)}>
                      Cancelar
                    </button>
                  </div>
                </Form>
              </div>
            </div>
          )}
          <div className="card"><div className="card-body p-0">
            <table className="table table-sm table-striped mb-0">
              <thead><tr><th style={{width:36}}>#</th><th>Nombre</th><th>Inicio</th><th>Fin</th><th style={{width:120}}>%</th><th>Responsable</th><th>Presupuesto</th><th>Costo</th><th>Acciones</th></tr></thead>
              <tbody>
                {(tareasRaiz).map((t, i) => (
                  <tr key={t.id} className="table-primary">
                    <td className="text-muted text-center" style={{width:36}}>{i + 1}</td>
                    <td><strong>{t.nombre}</strong></td>
                    <td>{t.fechaInicio}</td><td>{t.fechaFin}</td>
                    <td><Progress percent={t.porcentajeAvance} size="small" strokeColor="#1E3A5F" /></td>
                    <td>{t.nombreResponsable}</td>
                    <td>Bs {t.presupuestoEstimado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
                    <td>Bs {t.costoEjecutado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
                    <td>
                      {puedeEditar ? (
                        <>
                          <button className="btn btn-sm btn-warning me-1 btn-icon" onClick={() => openEditTarea(t)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg> Editar</button>
                          <button className="btn btn-sm btn-danger btn-icon" onClick={() => handleDeleteTarea(t.id, t.nombre)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg> Eliminar</button>
                        </>
                      ) : (
                        t.idResponsable === usuario?.id && (
                          <button className="btn btn-sm btn-outline-warning btn-icon" onClick={() => openEditTarea(t)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg> Reportar Avance</button>
                        )
                      )}
                    </td>
                  </tr>
                ))}
                {subtareas.map((t, j) => (
                  <tr key={t.id}>
                    <td className="text-muted text-center">{tareasRaiz.length + j + 1}</td>
                    <td className="ps-4">&rarr; {t.nombre}</td>
                    <td>{t.fechaInicio}</td><td>{t.fechaFin}</td>
                    <td><Progress percent={t.porcentajeAvance} size="small" strokeColor="#1E3A5F" /></td>
                    <td>{t.nombreResponsable}</td>
                    <td>Bs {t.presupuestoEstimado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
                    <td>Bs {t.costoEjecutado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
                    <td>
                      {puedeEditar ? (
                        <>
                          <button className="btn btn-sm btn-warning me-1 btn-icon" onClick={() => openEditTarea(t)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg> Editar</button>
                          <button className="btn btn-sm btn-danger btn-icon" onClick={() => handleDeleteTarea(t.id, t.nombre)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg> Eliminar</button>
                        </>
                      ) : (
                        t.idResponsable === usuario?.id && (
                          <button className="btn btn-sm btn-outline-warning btn-icon" onClick={() => openEditTarea(t)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg> Reportar Avance</button>
                        )
                      )}
                    </td>
                  </tr>
                ))}
                {tareas.length === 0 && <tr><td colSpan={9} className="text-center py-3">No hay tareas</td></tr>}
              </tbody>
            </table>
            <div className="px-3 py-2 border-top small text-muted">Total: {tareas.length} tarea(s)</div>
          </div></div>
        </div>
      )}

      {/* TAB GANTT */}
      {activeTab === 'gantt' && (
        <div>
          <div className="gantt-wrapper">
            <div className="card-header">Diagrama de Gantt</div>
            <div className="card-body">
              <GanttView id={id} />
            </div>
          </div>
        </div>
      )}

      {/* TAB DEPENDENCIAS */}
      {activeTab === 'deps' && (
        <div>
          {puedeEditar && <button className="btn btn-primary btn-sm mb-2 btn-icon" onClick={() => setShowDepForm(!showDepForm)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg> Nueva Dependencia</button>}
          {showDepForm && (
            <div className="card mb-3"><div className="card-body">
              <form onSubmit={handleCreateDep} className="row">
                <div className="col-md-3">
                  <select className="form-select" value={depForm.idTareaOrigen} onChange={e => setDepForm({...depForm, idTareaOrigen: e.target.value})} required>
                    <option value="">Tarea origen (predecesora)</option>
                    {tareas.map(t => <option key={t.id} value={t.id}>{t.nombre}</option>)}
                  </select>
                </div>
                <div className="col-md-3">
                  <select className="form-select" value={depForm.idTareaDestino} onChange={e => setDepForm({...depForm, idTareaDestino: e.target.value})} required>
                    <option value="">Tarea destino (sucesora)</option>
                    {tareas.map(t => <option key={t.id} value={t.id}>{t.nombre}</option>)}
                  </select>
                </div>
                <div className="col-md-2">
                  <select className="form-select" value={depForm.tipo} onChange={e => setDepForm({...depForm, tipo: e.target.value})}>
                    <option value="FIN_INICIO">Fin {">"} Inicio</option>
                    <option value="INICIO_INICIO">Inicio {">"} Inicio</option>
                    <option value="FIN_FIN">Fin {">"} Fin</option>
                    <option value="INICIO_FIN">Inicio {">"} Fin</option>
                  </select>
                </div>
                <div className="col-md-2">
                  <button type="submit" className="btn btn-success btn-sm btn-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg> Crear</button>
                  <button type="button" className="btn btn-secondary btn-sm ms-1 btn-icon" onClick={() => setShowDepForm(false)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg> Cancelar</button>
                </div>
              </form>
            </div></div>
          )}
          <div className="card"><div className="card-body p-0">
            <table className="table table-sm table-striped mb-0">
              <thead><tr><th style={{width:36}}>#</th><th>Predecesora</th><th>Sucesora</th><th>Tipo</th><th>Acciones</th></tr></thead>
              <tbody>
                {deps.map((d, i) => (
                  <tr key={d.id}>
                    <td className="text-muted text-center" style={{width:36}}>{i + 1}</td>
                    <td>{d.nombreTareaOrigen}</td><td>{d.nombreTareaDestino}</td>
                    <td><span className="badge bg-secondary">{d.tipo}</span></td>
                    <td>{puedeEditar && <button className="btn btn-sm btn-danger btn-icon" onClick={() => handleDeleteDep(d.id, `Relación ${d.nombreTareaOrigen} → ${d.nombreTareaDestino}`)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg> Eliminar</button>}</td>
                  </tr>
                ))}
                {deps.length === 0 && <tr><td colSpan={5} className="text-center py-3">Sin dependencias</td></tr>}
              </tbody>
            </table>
            <div className="px-3 py-2 border-top small text-muted">Total: {deps.length} dependencia(s)</div>
          </div></div>
        </div>
      )}

      {/* TAB RECURSOS */}
      {activeTab === 'recursos' && (
        <div>
          {puedeEditar && <button className="btn btn-primary btn-sm mb-2 btn-icon" onClick={() => setShowAsigForm(!showAsigForm)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg> Asignar Recurso</button>}
          {showAsigForm && (
            <div className="card mb-3"><div className="card-body">
              <form onSubmit={handleCreateAsig} className="row">
                <div className="col-md-3">
                  <select className="form-select" value={asigForm.idTarea} onChange={e => setAsigForm({...asigForm, idTarea: e.target.value})} required>
                    <option value="">Tarea</option>
                    {tareas.map(t => <option key={t.id} value={t.id}>{t.nombre}</option>)}
                  </select>
                </div>
                <div className="col-md-3">
                  <select className="form-select" value={asigForm.idUsuario} onChange={e => setAsigForm({...asigForm, idUsuario: e.target.value})} required>
                    <option value="">Usuario</option>
                    {usuarios.map(u => <option key={u.id} value={u.id}>{u.nombre}</option>)}
                  </select>
                </div>
                <div className="col-md-2"><input type="number" className="form-control" placeholder="Hrs est." step="0.5" value={asigForm.horasEstimadas} onChange={e => setAsigForm({...asigForm, horasEstimadas: e.target.value})} /></div>
                <div className="col-md-2"><input type="number" className="form-control" placeholder="Hrs reales" step="0.5" value={asigForm.horasReales} onChange={e => setAsigForm({...asigForm, horasReales: e.target.value})} /></div>
                <div className="col-md-2">
                  <button type="submit" className="btn btn-success btn-sm btn-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg> Asignar</button>
                  <button type="button" className="btn btn-secondary btn-sm ms-1 btn-icon" onClick={() => setShowAsigForm(false)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg> Cancelar</button>
                </div>
              </form>
            </div></div>
          )}
          <div className="card"><div className="card-body p-0">
            <table className="table table-sm table-striped mb-0">
              <thead><tr><th style={{width:36}}>#</th><th>Tarea</th><th>Usuario</th><th>Hrs Est.</th><th>Hrs Reales</th><th>Acciones</th></tr></thead>
              <tbody>
                {asignaciones.map((a, i) => (
                  <tr key={a.id}>
                    <td className="text-muted text-center" style={{width:36}}>{i + 1}</td>
                    <td>{a.nombreTarea}</td><td>{a.nombreUsuario}</td>
                    <td>{a.horasEstimadas}</td><td>{a.horasReales}</td>
                    <td>{puedeEditar && <button className="btn btn-sm btn-danger btn-icon" onClick={() => handleDeleteAsig(a.id, `Asignación de ${a.nombreUsuario} en ${a.nombreTarea}`)}><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg> Eliminar</button>}</td>
                  </tr>
                ))}
                {asignaciones.length === 0 && <tr><td colSpan={6} className="text-center py-3">Sin asignaciones</td></tr>}
              </tbody>
            </table>
            <div className="px-3 py-2 border-top small text-muted">Total: {asignaciones.length} asignacion(es)</div>
          </div></div>
        </div>
      )}

      {/* TAB PRESUPUESTO */}
      {activeTab === 'presupuesto' && (
        <div>
          <div className="row mb-4">
            <div className="col-md-4 mb-3">
              <div className="stat-card stat-card-bg-primary">
                <h5 className="card-title">Presupuesto Total</h5>
                <p className="display-6" style={{fontSize:'1.8rem'}}>Bs {proyecto.presupuestoTotal?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</p>
              </div>
            </div>
            <div className="col-md-4 mb-3">
              <div className="stat-card stat-card-bg-danger">
                <h5 className="card-title">Costo Real</h5>
                <p className="display-6" style={{fontSize:'1.8rem'}}>Bs {proyecto.costoRealTotal?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</p>
              </div>
            </div>
            <div className="col-md-4 mb-3">
              <div className={`stat-card ${proyecto.presupuestoTotal >= proyecto.costoRealTotal ? 'stat-card-bg-success' : 'stat-card-bg-warning'}`}>
                <h5 className="card-title">Diferencia</h5>
                <p className="display-6" style={{fontSize:'1.8rem'}}>Bs {(proyecto.presupuestoTotal - proyecto.costoRealTotal)?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</p>
              </div>
            </div>
          </div>
          <div className="row g-4 mb-4">
            <div className="col-md-6">
              <div className="card h-100">
                <div className="card-header">Presupuesto vs Costo por Tarea</div>
                <div className="card-body" style={{minHeight:'260px'}}>
                  {tareas.filter(t => !t.idTareaPadre).length > 0 ? (
                    <Bar data={{
                      labels: tareas.filter(t => !t.idTareaPadre).map(t => t.nombre.length > 18 ? t.nombre.substring(0,18)+'..' : t.nombre),
                      datasets: [
                        { label: 'Presupuesto', data: tareas.filter(t => !t.idTareaPadre).map(t => t.presupuestoEstimado || 0),
                          backgroundColor: 'rgba(59,130,246,0.8)', borderRadius: 4 },
                        { label: 'Costo Real', data: tareas.filter(t => !t.idTareaPadre).map(t => t.costoEjecutado || 0),
                          backgroundColor: 'rgba(239,68,68,0.8)', borderRadius: 4 },
                      ]
                    }} options={{
                      responsive: true, maintainAspectRatio: false,
                      plugins: { legend: { position: 'bottom', labels: { usePointStyle: true, font: { size: 10, family: 'Inter' } } } },
                      scales: { y: { beginAtZero: true, ticks: { callback: v => 'Bs ' + v.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2}) } } }
                    }} />
                  ) : (
                    <p className="text-muted text-center py-4 mb-0">Sin datos</p>
                  )}
                </div>
              </div>
            </div>
            <div className="col-md-6">
              <div className="card h-100">
                <div className="card-header">Resumen General</div>
                <div className="card-body d-flex align-items-center" style={{minHeight:'260px'}}>
                  {proyecto.presupuestoTotal > 0 ? (
                    <Bar data={{
                      labels: ['General'],
                      datasets: [
                        { label: 'Presupuesto', data: [proyecto.presupuestoTotal],
                          backgroundColor: 'rgba(59,130,246,0.8)', borderRadius: 6 },
                        { label: 'Costo Real', data: [proyecto.costoRealTotal],
                          backgroundColor: 'rgba(239,68,68,0.8)', borderRadius: 6 },
                      ]
                    }} options={{
                      responsive: true, maintainAspectRatio: false,
                      indexAxis: 'y',
                      plugins: { legend: { position: 'bottom', labels: { usePointStyle: true, font: { size: 11, family: 'Inter' } } } },
                      scales: { x: { beginAtZero: true, ticks: { callback: v => 'Bs ' + v.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2}) } } }
                    }} />
                  ) : (
                    <p className="text-muted text-center py-4 mb-0">Sin datos</p>
                  )}
                </div>
              </div>
            </div>
          </div>
          <div className="card">
            <div className="card-header">Detalle por Tarea</div>
            <div className="card-body p-0">
              <table className="table table-striped mb-0">
                <thead><tr><th style={{width:36}}>#</th><th>Tarea</th><th>Presupuesto</th><th>Costo</th><th>Diferencia</th></tr></thead>
                <tbody>
                  {tareas.length === 0 && <tr><td colSpan={5} className="text-center py-3 text-muted">Sin tareas</td></tr>}
                  {tareas.map((t, i) => (
                    <tr key={t.id}>
                      <td className="text-muted text-center" style={{width:36}}>{i + 1}</td>
                      <td className={t.idTareaPadre ? 'ps-4 small' : 'fw-semibold'}>{t.nombre}</td>
                      <td>Bs {t.presupuestoEstimado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
                      <td>Bs {t.costoEjecutado?.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
                      <td className={t.presupuestoEstimado >= t.costoEjecutado ? 'text-success fw-semibold' : 'text-danger fw-semibold'}>
                        Bs {((t.presupuestoEstimado || 0) - (t.costoEjecutado || 0)).toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
            <div className="px-3 py-2 border-top small text-muted">Total: {tareas.length} tarea(s)</div>
          </div>
        </div>
        </div>
      )}
    </div>
  );
}

function GanttView({ id }) {
  const [ganttData, setGanttData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const containerRef = useRef(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    setGanttData(null);
    api.get(`/gantt/proyecto/${id}`)
      .then(r => { setGanttData(r.data); setLoading(false); })
      .catch(() => { setError('No hay datos de Gantt para este proyecto'); setLoading(false); });
  }, [id]);

  useEffect(() => {
    if (!ganttData || !containerRef.current) return;

    containerRef.current.innerHTML = '';

    const tasks = ganttData.tareas.map(t => ({
      id: t.id,
      name: t.name,
      start: t.start,
      end: t.end,
      progress: t.progress / 100,
      dependencies: t.dependencies
    }));

    if (tasks.length === 0) {
      setError('No hay tareas para mostrar en el diagrama Gantt');
      return;
    }

    let cancelled = false;

    import('frappe-gantt').then(mod => {
      if (cancelled || !containerRef.current) return;
      try {
        const wrapper = document.createElement('div');
        wrapper.style.width = '100%';
        wrapper.style.overflowX = 'auto';
        wrapper.style.overflowY = 'hidden';
        containerRef.current.appendChild(wrapper);
                        new (mod.default || mod.Gantt)(wrapper, tasks, {
          view_mode: 'Day',
          date_format: 'YYYY-MM-DD'
        });
      } catch (e) {
        console.error('Gantt render error:', e);
        if (!cancelled) setError('Error al renderizar el diagrama Gantt: ' + e.message);
      }
    });

    return () => { cancelled = true; };
  }, [ganttData]);

  if (loading) return <div className="text-center py-4"><div className="spinner-border spinner-border-sm text-primary me-2"></div><span className="text-muted">Cargando diagrama Gantt...</span></div>;
  if (error) return <div className="alert alert-info py-2 mb-0">{error}</div>;
  if (!ganttData || !ganttData.tareas || ganttData.tareas.length === 0) return <p className="text-muted text-center py-3 mb-0">No hay tareas para mostrar</p>;

  return <div ref={containerRef} className="gantt-container" style={{ minHeight: '500px' }}></div>;
}
