import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import esES from 'antd/locale/es_ES';
import App from './App';
import { AuthProvider } from './context/AuthContext';
import './styles/main.scss';

import {
  Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement,
  Title, Tooltip, Legend, PointElement, LineElement
} from 'chart.js';

ChartJS.register(
  CategoryScale, LinearScale, BarElement, ArcElement,
  Title, Tooltip, Legend, PointElement, LineElement
);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <ConfigProvider
          locale={esES}
          theme={{
            token: {
              colorPrimary: '#1E3A5F',       // Azul acero
              colorInfo: '#1E3A5F',
              colorSuccess: '#16A34A',      // Verde
              colorWarning: '#D97706',      // Ámbar
              colorError: '#DC2626',        // Rojo
              colorTextBase: '#0F172A',
              colorTextSecondary: '#64748B', // Gris pizarra
              fontFamily: 'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
              borderRadius: 8,
            },
            components: {
              Button: {
                borderRadius: 6,
                fontWeight: 500,
              },
            },
          }}
        >
          <App />
        </ConfigProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
