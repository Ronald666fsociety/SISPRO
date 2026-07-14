import React, { useState } from 'react';
import { Layout, Menu, Button, Avatar, Dropdown, Space, Badge, Typography } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  ProjectOutlined,
  BarChartOutlined,
  UserOutlined,
  AuditOutlined,
  LogoutOutlined,
  DownOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

export default function DashboardLayout({ children }) {
  const [collapsed, setCollapsed] = useState(false);
  const { usuario, logout, tieneRol } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  if (!usuario) return children;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const rolLabels = {
    ADMINISTRADOR: 'Administrador',
    JEFE_PROYECTO: 'Jefe de Proyecto',
    USUARIO: 'Usuario',
  };

  // Definir items del menú según el rol
  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: 'Dashboard',
      onClick: () => navigate('/'),
    },
    {
      key: '/proyectos',
      icon: <ProjectOutlined />,
      label: 'Proyectos',
      onClick: () => navigate('/proyectos'),
    },
    {
      key: '/reportes',
      icon: <BarChartOutlined />,
      label: 'Reportes',
      onClick: () => navigate('/reportes'),
    },
  ];

  if (tieneRol(['ADMINISTRADOR'])) {
    menuItems.push(
      {
        key: '/usuarios',
        icon: <UserOutlined />,
        label: 'Usuarios',
        onClick: () => navigate('/usuarios'),
      },
      {
        key: '/auditoria',
        icon: <AuditOutlined />,
        label: 'Auditoría',
        onClick: () => navigate('/auditoria'),
      }
    );
  }

  // Encontrar la clave activa del menú
  const getSelectedKey = () => {
    if (location.pathname === '/') return ['/'];
    const match = menuItems.find(item => item.key !== '/' && location.pathname.startsWith(item.key));
    return match ? [match.key] : [location.pathname];
  };

  const userMenu = {
    items: [
      {
        key: 'profile',
        label: (
          <div style={{ padding: '4px 8px' }}>
            <div style={{ fontWeight: 600 }}>{usuario.nombre}</div>
            <div style={{ fontSize: '12px', color: '#64748B' }}>{usuario.email}</div>
          </div>
        ),
      },
      {
        type: 'divider',
      },
      {
        key: 'logout',
        danger: true,
        icon: <LogoutOutlined />,
        label: 'Cerrar Sesión',
        onClick: handleLogout,
      },
    ],
  };

  return (
    <Layout style={{ minHeight: '100vh', background: '#E5EBF2' }}>
      {/* Sidebar */}
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        theme="light"
        width={260}
        style={{
          borderRight: '1px solid #E2E8F0',
          position: 'fixed',
          height: '100vh',
          left: 0,
          top: 0,
          bottom: 0,
          zIndex: 100,
        }}
      >
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: collapsed ? 'center' : 'flex-start',
            padding: '0 24px',
            borderBottom: '1px solid #E2E8F0',
            background: '#fff',
          }}
          className="d-flex align-items-center"
        >
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              cursor: 'pointer',
            }}
            onClick={() => navigate('/')}
          >
            <div
              style={{
                width: 32,
                height: 32,
                borderRadius: 8,
                background: 'linear-gradient(135deg, #1E3A5F 0%, #2A4D7C 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#fff',
                fontWeight: 'bold',
                fontSize: 16,
              }}
            >
              T
            </div>
            {!collapsed && (
              <span
                style={{
                  fontWeight: 700,
                  fontSize: 18,
                  color: '#1E3A5F',
                  letterSpacing: '-0.5px',
                }}
              >
                SIGEPRO
              </span>
            )}
          </div>
        </div>

        <Menu
          mode="inline"
          selectedKeys={getSelectedKey()}
          items={menuItems}
          style={{
            borderRight: 0,
            padding: '16px 8px',
            background: 'transparent',
          }}
          className="custom-sidebar-menu"
        />

        {!collapsed && (
          <div
            style={{
              position: 'absolute',
              bottom: 24,
              left: 24,
              right: 24,
              padding: '12px',
              borderRadius: 8,
              background: '#F1F5F9',
              border: '1px solid #E2E8F0',
            }}
          >
            <div style={{ fontSize: '11px', textTransform: 'uppercase', color: '#64748B', fontWeight: 600, letterSpacing: '0.5px' }}>
              Empresa
            </div>
            <div style={{ fontSize: '13px', fontWeight: 600, color: '#1E3A5F', marginTop: '2px' }}>
              TransAndina S.A.
            </div>
          </div>
        )}
      </Sider>

      {/* Main Layout Area */}
      <Layout
        style={{
          marginLeft: collapsed ? 80 : 260,
          transition: 'margin-left 0.2s',
          minHeight: '100vh',
          background: 'transparent',
        }}
      >
        {/* Header */}
        <Header
          style={{
            padding: '0 24px',
            background: '#fff',
            borderBottom: '1px solid #E2E8F0',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            position: 'sticky',
            top: 0,
            zIndex: 99,
            height: 64,
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', flex: 1 }}>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{
                fontSize: '16px',
                width: 40,
                height: 40,
                color: '#64748B',
              }}
            />
            <Text
              style={{
                marginLeft: 16,
                fontSize: 16,
                fontWeight: 600,
                color: '#1E3A5F',
              }}
            >
              Sistema de Gestión de Proyectos
            </Text>
          </div>

          <Space size={16}>
            <Dropdown menu={userMenu} trigger={['click']}>
              <a onClick={e => e.preventDefault()} style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Avatar
                  style={{
                    backgroundColor: '#1E3A5F',
                    verticalAlign: 'middle',
                  }}
                  size="medium"
                >
                  {usuario.nombre ? usuario.nombre.charAt(0).toUpperCase() : 'U'}
                </Avatar>
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', lineHeight: 1.2 }}>
                  <span style={{ fontWeight: 600, color: '#1E3A5F', fontSize: '13px' }}>{usuario.nombre}</span>
                  <span style={{ fontSize: '11px', color: '#64748B' }}>
                    {rolLabels[usuario.rol] || usuario.rol}
                  </span>
                </div>
                <DownOutlined style={{ fontSize: 10, color: '#64748B', marginLeft: 4 }} />
              </a>
            </Dropdown>
          </Space>
        </Header>

        {/* Content */}
        <Content
          style={{
            padding: '24px',
            margin: 0,
            minHeight: 280,
            background: 'transparent',
          }}
        >
          {children}
        </Content>
      </Layout>
    </Layout>
  );
}
