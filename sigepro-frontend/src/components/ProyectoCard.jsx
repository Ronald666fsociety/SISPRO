import React, { useState, useEffect } from 'react';
import { Card, Progress, Space, Badge, Typography, Skeleton } from 'antd';
import { CalendarOutlined, UserOutlined, WalletOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import api from '../api/axios';

const { Text, Title, Paragraph } = Typography;

export default function ProyectoCard({ proyecto, onEdit, onDelete, puedeEditar }) {
  const [loading, setLoading] = useState(true);
  const [semaforo, setSemaforo] = useState(null);

  useEffect(() => {
    let active = true;
    api.get(`/reportes/semaforo/${proyecto.id}`)
      .then(res => {
        if (active) {
          setSemaforo(res.data);
          setLoading(false);
        }
      })
      .catch(() => {
        if (active) {
          setLoading(false);
        }
      });
    return () => { active = false; };
  }, [proyecto.id]);

  // Colores del semáforo
  const semaforoColores = {
    VERDE: '#16A34A',
    AMARILLO: '#D97706',
    ROJO: '#DC2626',
  };

  const semaforoEtiquetas = {
    VERDE: 'En regla',
    AMARILLO: 'En riesgo',
    ROJO: 'Crítico',
  };

  const estadoColores = {
    PLANIFICADO: 'warning',
    EN_CURSO: 'processing',
    FINALIZADO: 'success',
    CANCELADO: 'error',
  };

  const estadoLabel = {
    PLANIFICADO: 'Planificado',
    EN_CURSO: 'En Curso',
    FINALIZADO: 'Finalizado',
    CANCELADO: 'Cancelado',
  };

  const avance = semaforo ? Math.round(semaforo.avanceReal || 0) : 0;
  const semaforoColor = semaforo ? semaforoColores[semaforo.color] || '#64748B' : '#64748B';
  const semaforoText = semaforo ? semaforoEtiquetas[semaforo.color] || 'Sin estado' : 'Cargando...';

  return (
    <Card
      hoverable
      style={{
        borderRadius: 12,
        border: '1px solid #E2E8F0',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
      }}
      bodyStyle={{
        padding: '20px',
        display: 'flex',
        flexDirection: 'column',
        flex: 1,
      }}
    >
      {loading ? (
        <Skeleton active paragraph={{ rows: 4 }} />
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%', flex: 1 }}>
          {/* Header de la tarjeta */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
            <Badge status={estadoColores[proyecto.estado]} text={estadoLabel[proyecto.estado]} style={{ fontWeight: 500 }} />
            
            {/* Semáforo */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span
                style={{
                  width: '8px',
                  height: '8px',
                  borderRadius: '50%',
                  backgroundColor: semaforoColor,
                  display: 'inline-block',
                }}
              />
              <span style={{ fontSize: '12px', color: '#64748B', fontWeight: 500 }}>
                {semaforoText}
              </span>
            </div>
          </div>

          {/* Título y Descripción */}
          <div style={{ flex: 1, marginBottom: 16 }}>
            <Title level={5} style={{ margin: '0 0 8px 0', fontWeight: 600, color: '#1E3A5F' }}>
              {proyecto.nombre}
            </Title>
            <Paragraph
              ellipsis={{ rows: 2 }}
              style={{ fontSize: '13px', color: '#64748B', margin: 0, lineHeight: 1.5 }}
            >
              {proyecto.descripcion || 'Sin descripción.'}
            </Paragraph>
          </div>

          {/* Barra de Progreso */}
          <div style={{ marginBottom: 20 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
              <Text style={{ fontSize: '12px', fontWeight: 600, color: '#64748B' }}>Progreso</Text>
              <Text style={{ fontSize: '12px', fontWeight: 600, color: '#1E3A5F' }} className="tabular-nums">
                {avance}%
              </Text>
            </div>
            <Progress
              percent={avance}
              showInfo={false}
              strokeColor="#1E3A5F"
              trailColor="#E2E8F0"
              strokeWidth={6}
              style={{ margin: 0 }}
            />
          </div>

          {/* Meta e Info */}
          <div
            style={{
              paddingTop: 12,
              borderTop: '1px solid #F1F5F9',
              display: 'flex',
              flexDirection: 'column',
              gap: '8px',
              fontSize: '13px',
              color: '#64748B',
              marginBottom: 16,
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <UserOutlined style={{ color: '#94A3B8' }} />
              <span>
                <strong>Jefe:</strong> {proyecto.nombreJefeProyecto || 'No asignado'}
              </span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <WalletOutlined style={{ color: '#94A3B8' }} />
              <span>
                <strong>Presupuesto:</strong> Bs <span className="tabular-nums">{proyecto.presupuestoTotal?.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CalendarOutlined style={{ color: '#94A3B8' }} />
              <span>
                {proyecto.fechaInicio} &rarr; {proyecto.fechaFin}
              </span>
            </div>
          </div>

          {/* Botón de Gestión y Acciones */}
          <div style={{ marginTop: 'auto', display: 'flex', gap: '8px' }}>
            <Link
              to={`/proyectos/${proyecto.id}`}
              className="btn btn-outline-secondary btn-sm"
              style={{
                flex: 1,
                textAlign: 'center',
                fontWeight: 500,
                borderRadius: '6px',
                borderColor: '#CBD5E1',
                color: '#1E3A5F',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              Gestionar Proyecto
            </Link>
            {puedeEditar && (
              <>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-warning"
                  onClick={onEdit}
                  style={{ borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '0 12px' }}
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                </button>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-danger"
                  onClick={onDelete}
                  style={{ borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '0 12px' }}
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                </button>
              </>
            )}
          </div>
        </div>
      )}
    </Card>
  );
}
