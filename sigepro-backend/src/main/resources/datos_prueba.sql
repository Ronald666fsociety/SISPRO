-- =====================================
-- SIGEPRO - Datos de Prueba
-- =====================================
-- NOTA: Las contrasenas estan hasheadas con BCrypt.
-- Todas las contrasenas de prueba son: "123456"
-- =====================================

USE sigepro;

-- Usuarios
INSERT INTO usuario (nombre, email, password, rol, activo) VALUES
('Admin Principal', 'admin@transandina.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMINISTRADOR', TRUE),
('Carlos Mendoza', 'cmendoza@transandina.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'JEFE_PROYECTO', TRUE),
('Ana Lopez', 'alopez@transandina.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USUARIO', TRUE),
('Pedro Garcia', 'pgarcia@transandina.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USUARIO', TRUE),
('Maria Torres', 'mtorres@transandina.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USUARIO', TRUE);

-- Proyectos
INSERT INTO proyecto (nombre, descripcion, fecha_inicio, fecha_fin, estado, id_jefe_proyecto, presupuesto_total, costo_real_total, activo) VALUES
('Implementacion ERP', 'Implementacion del sistema ERP corporativo', '2026-01-15', '2026-06-30', 'EN_CURSO', 2, 150000.00, 48000.00, TRUE),
('Migracion Cloud', 'Migracion de infraestructura a la nube', '2026-03-01', '2026-08-15', 'PLANIFICADO', 2, 200000.00, 0.00, TRUE),
('App Clientes', 'Desarrollo de aplicacion movil para clientes', '2026-02-01', '2026-05-15', 'EN_CURSO', 2, 85000.00, 35000.00, TRUE);

-- Tareas del Proyecto 1 (Implementacion ERP)
INSERT INTO tarea (id_proyecto, id_tarea_padre, nombre, fecha_inicio, fecha_fin, porcentaje_avance, presupuesto_estimado, costo_ejecutado, id_responsable) VALUES
(1, NULL, 'Analisis de Requerimientos', '2026-01-15', '2026-02-15', 100, 15000.00, 14500.00, 3),
(1, NULL, 'Diseno de Arquitectura', '2026-02-16', '2026-03-15', 80, 20000.00, 16000.00, 4),
(1, NULL, 'Desarrollo Modulos', '2026-03-16', '2026-05-15', 30, 70000.00, 12000.00, 3),
(1, NULL, 'Pruebas y QA', '2026-05-16', '2026-06-15', 0, 25000.00, 0.00, 5),
(1, NULL, 'Despliegue y Capacitacion', '2026-06-16', '2026-06-30', 0, 20000.00, 0.00, 5);

-- Subtareas de Desarrollo Modulos (id=5)
INSERT INTO tarea (id_proyecto, id_tarea_padre, nombre, fecha_inicio, fecha_fin, porcentaje_avance, presupuesto_estimado, costo_ejecutado, id_responsable) VALUES
(1, 5, 'Modulo Contabilidad', '2026-03-16', '2026-04-15', 50, 25000.00, 6000.00, 3),
(1, 5, 'Modulo RRHH', '2026-04-01', '2026-04-30', 20, 25000.00, 3000.00, 4),
(1, 5, 'Modulo Logistica', '2026-04-16', '2026-05-15', 10, 20000.00, 3000.00, 3);

-- Tareas del Proyecto 3 (App Clientes)
INSERT INTO tarea (id_proyecto, id_tarea_padre, nombre, fecha_inicio, fecha_fin, porcentaje_avance, presupuesto_estimado, costo_ejecutado, id_responsable) VALUES
(3, NULL, 'Diseno UX/UI', '2026-02-01', '2026-02-28', 100, 10000.00, 9500.00, 4),
(3, NULL, 'Desarrollo Frontend', '2026-03-01', '2026-04-15', 60, 35000.00, 18000.00, 3),
(3, NULL, 'Desarrollo Backend', '2026-03-01', '2026-04-30', 40, 25000.00, 7500.00, 4),
(3, NULL, 'Testing', '2026-05-01', '2026-05-15', 0, 15000.00, 0.00, 5);

-- Dependencias (Proyecto 1 - tipo FIN_INICIO)
INSERT INTO dependencia_tarea (id_tarea_origen, id_tarea_destino, tipo) VALUES
(1, 2, 'FIN_INICIO'),  -- Analisis -> Diseno
(2, 5, 'FIN_INICIO'),  -- Diseno -> Desarrollo
(5, 6, 'FIN_INICIO'),  -- Desarrollo -> Pruebas
(6, 7, 'FIN_INICIO');  -- Pruebas -> Despliegue

-- Dependencias (Proyecto 3)
INSERT INTO dependencia_tarea (id_tarea_origen, id_tarea_destino, tipo) VALUES
(12, 13, 'FIN_INICIO'), -- Diseno UX/UI -> Frontend
(12, 14, 'FIN_INICIO'), -- Diseno UX/UI -> Backend
(13, 15, 'FIN_INICIO'), -- Frontend -> Testing
(14, 15, 'FIN_INICIO'); -- Backend -> Testing

-- Asignacion de recursos
INSERT INTO recurso_tarea (id_tarea, id_usuario, horas_estimadas, horas_reales) VALUES
(1, 3, 120.00, 115.00),
(2, 4, 100.00, 85.00),
(5, 3, 200.00, 60.00),
(5, 4, 150.00, 40.00),
(6, 5, 80.00, 0.00),
(8, 3, 80.00, 25.00),
(9, 4, 80.00, 15.00),
(10, 3, 60.00, 10.00),
(12, 4, 60.00, 58.00),
(13, 3, 120.00, 70.00),
(14, 4, 100.00, 35.00);

-- Auditoria
INSERT INTO auditoria (id_usuario, accion, entidad, id_entidad, fecha) VALUES
(1, 'CREAR', 'Proyecto', 1, '2026-01-10 09:00:00'),
(1, 'CREAR', 'Proyecto', 2, '2026-02-20 10:30:00'),
(1, 'CREAR', 'Proyecto', 3, '2026-01-25 14:00:00'),
(2, 'ACTUALIZAR', 'Proyecto', 1, '2026-03-01 11:00:00'),
(2, 'CREAR', 'Usuario', 3, '2026-01-05 08:30:00'),
(2, 'CREAR', 'Usuario', 4, '2026-01-05 08:35:00'),
(2, 'CREAR', 'Usuario', 5, '2026-01-05 08:40:00');
