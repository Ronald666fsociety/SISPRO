import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Form, Input, Select, Modal } from 'antd';
import { confirmarEliminacion, notificar } from '../utils/feedback';
import ProyectoCard from '../components/ProyectoCard';

export default function Proyectos() {
  const [proyectos, setProyectos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingProyecto, setEditingProyecto] = useState(null);
  const [formRef] = Form.useForm();

  const { tieneRol } = useAuth();
  const puedeEditar = tieneRol(['ADMINISTRADOR', 'JEFE_PROYECTO']);

  const load = () => api.get('/proyectos').then(r => setProyectos(r.data));
  useEffect(() => { load(); api.get('/usuarios').then(r => setUsuarios(r.data)).catch(() => {}); }, []);

  const descargarReporteProyectos = async () => {
    try {
      const r = await api.get('/exportar/pdf/proyectos', { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([r.data], { type: 'application/pdf' }));
      const a = document.createElement('a'); a.href = url; a.download = 'reporte_proyectos.pdf';
      document.body.appendChild(a); a.click(); document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (e) { alert('Error al descargar el reporte'); }
  };

  const openCreateModal = () => {
    setEditingProyecto(null);
    setShowForm(true);
    setTimeout(() => {
      formRef.resetFields();
    }, 50);
  };

  const openEditModal = (p) => {
    setEditingProyecto(p);
    setShowForm(true);
    setTimeout(() => {
      formRef.setFieldsValue({
        nombre: p.nombre,
        descripcion: p.descripcion,
        fechaInicio: p.fechaInicio,
        fechaFin: p.fechaFin,
        idJefeProyecto: p.idJefeProyecto,
        estado: p.estado,
      });
    }, 50);
  };

  const handleSave = async (values) => {
    try {
      if (editingProyecto) {
        await api.put(`/proyectos/${editingProyecto.id}`, {
          ...values,
          idJefeProyecto: parseInt(values.idJefeProyecto),
        });
        notificar.actualizado('Proyecto');
      } else {
        await api.post('/proyectos', {
          ...values,
          estado: 'PLANIFICADO',
          idJefeProyecto: parseInt(values.idJefeProyecto),
        });
        notificar.creado('Proyecto');
      }
      setShowForm(false);
      setEditingProyecto(null);
      formRef.resetFields();
      load();
    } catch (err) {
      notificar.error(err.response?.data?.mensaje || 'Error al guardar el proyecto');
    }
  };

  const handleDelete = (proyectoId, nombre) => {
    confirmarEliminacion({
      tipo: 'Proyecto',
      itemNombre: nombre,
      alConfirmar: async () => {
        try {
          await api.delete(`/proyectos/${proyectoId}`);
          notificar.eliminado('Proyecto');
          load();
        } catch (err) {
          notificar.error(err.response?.data?.mensaje || 'Error al eliminar el proyecto');
        }
      }
    });
  };

  return (
    <div className="fade-in">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2>Proyectos</h2>
          <p style={{ color: 'var(--gray-500)', fontSize: '0.875rem', margin: 0 }}>Gestion de proyectos de TransAndina S.A.</p>
        </div>
        <div>
          {puedeEditar && (
            <button className="btn btn-primary btn-icon" onClick={openCreateModal}>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              Nuevo Proyecto
            </button>
          )}
          <button className="btn btn-outline-danger btn-icon ms-2" onClick={descargarReporteProyectos}>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg> Reporte PDF
          </button>
        </div>
      </div>

      <Modal
        title={<span style={{ fontWeight: 600, color: '#1E3A5F', fontSize: '16px' }}>{editingProyecto ? 'Editar Proyecto' : 'Nuevo Proyecto'}</span>}
        open={showForm}
        onCancel={() => { setShowForm(false); setEditingProyecto(null); }}
        footer={null}
        width={720}
        destroyOnClose
      >
        <Form
          form={formRef}
          layout="vertical"
          onFinish={handleSave}
          autoComplete="off"
          style={{ paddingTop: '12px' }}
        >
          <div className="row">
            <div className="col-md-6">
              <Form.Item
                name="nombre"
                label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Nombre del proyecto</span>}
                rules={[{ required: true, message: 'El nombre es obligatorio' }]}
              >
                <Input placeholder="Ej. Implementación ERP" size="large" />
              </Form.Item>
            </div>
            <div className="col-md-6">
              <Form.Item
                name="descripcion"
                label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Descripción</span>}
              >
                <Input placeholder="Breve descripción del proyecto" size="large" />
              </Form.Item>
            </div>
          </div>

          <div className="row">
            <div className="col-md-3">
              <Form.Item
                name="fechaInicio"
                label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Fecha de inicio</span>}
                rules={[{ required: true, message: 'La fecha de inicio es obligatoria' }]}
              >
                <Input type="date" size="large" />
              </Form.Item>
            </div>
            <div className="col-md-3">
              <Form.Item
                name="fechaFin"
                label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Fecha de fin</span>}
                rules={[{ required: true, message: 'La fecha de fin es obligatoria' }]}
              >
                <Input type="date" size="large" />
              </Form.Item>
            </div>
            <div className={editingProyecto ? "col-md-3" : "col-md-6"}>
              <Form.Item
                name="idJefeProyecto"
                label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Jefe de proyecto</span>}
                rules={[{ required: true, message: 'Debe seleccionar un jefe de proyecto' }]}
              >
                <Select placeholder="Seleccionar jefe..." size="large">
                  {usuarios.map(u => (
                    <Select.Option key={u.id} value={u.id}>
                      {u.nombre}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </div>
            {editingProyecto && (
              <div className="col-md-3">
                <Form.Item
                  name="estado"
                  label={<span style={{ fontWeight: 600, fontSize: '13px', color: '#475569' }}>Estado</span>}
                  rules={[{ required: true, message: 'El estado es obligatorio' }]}
                >
                  <Select size="large">
                    <Select.Option value="PLANIFICADO">Planificado</Select.Option>
                    <Select.Option value="EN_CURSO">En Curso</Select.Option>
                    <Select.Option value="FINALIZADO">Finalizado</Select.Option>
                    <Select.Option value="CANCELADO">Cancelado</Select.Option>
                  </Select>
                </Form.Item>
              </div>
            )}
          </div>

          <div style={{ marginTop: '20px', display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
            <button type="button" className="btn btn-outline-secondary" onClick={() => { setShowForm(false); setEditingProyecto(null); }}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary">
              {editingProyecto ? 'Guardar Cambios' : 'Crear Proyecto'}
            </button>
          </div>
        </Form>
      </Modal>

      <div className="row g-4">
        {proyectos.map(p => (
          <div className="col-md-4" key={p.id}>
            <ProyectoCard
              proyecto={p}
              onEdit={() => openEditModal(p)}
              onDelete={() => handleDelete(p.id, p.nombre)}
              puedeEditar={puedeEditar}
            />
          </div>
        ))}
        {proyectos.length === 0 && (
          <div className="col-12 text-center py-5">
            <h5 className="text-muted">No hay proyectos registrados</h5>
          </div>
        )}
      </div>

    </div>
  );
}
