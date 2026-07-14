import React from 'react';
import { Modal, message } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';

/**
 * Muestra un modal de confirmación Ant Design para acciones destructivas.
 * Cumple con los lineamientos:
 * - Título que nombra la acción ("¿Eliminar [Tipo]?")
 * - Cuerpo que explica la consecuencia irreversible.
 * - Botón de confirmación que repite el verbo exacto ("Eliminar").
 */
export const confirmarEliminacion = ({ titulo, itemNombre, tipo, alConfirmar }) => {
  Modal.confirm({
    title: titulo || `¿Eliminar ${tipo}?`,
    icon: <ExclamationCircleOutlined style={{ color: '#DC2626' }} />,
    content: (
      <div style={{ marginTop: '8px' }}>
        <p style={{ margin: 0, color: '#475569' }}>
          Esta acción es permanente e irreversible. Se eliminará el {tipo.toLowerCase()} <strong>"{itemNombre}"</strong> junto con todos sus datos asociados.
        </p>
      </div>
    ),
    okText: 'Eliminar',
    okType: 'danger',
    okButtonProps: {
      style: {
        backgroundColor: '#DC2626',
        borderColor: '#DC2626',
        borderRadius: '6px',
        fontWeight: 500,
      },
    },
    cancelText: 'Cancelar',
    cancelButtonProps: {
      style: {
        borderRadius: '6px',
      },
    },
    onOk() {
      return alConfirmar();
    },
  });
};

/**
 * Toasts cortos y directos sin la palabra "exitosamente"
 */
export const notificar = {
  creado: (tipo) => {
    message.success({
      content: `${tipo} creado`,
      style: { marginTop: '10vh' },
    });
  },
  actualizado: (tipo) => {
    message.success({
      content: `${tipo} actualizado`,
      style: { marginTop: '10vh' },
    });
  },
  eliminado: (tipo) => {
    message.success({
      content: `${tipo} eliminado`,
      style: { marginTop: '10vh' },
    });
  },
  error: (msg) => {
    message.error({
      content: msg || 'Error en la operación',
      style: { marginTop: '10vh' },
    });
  },
};
