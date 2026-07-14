CREATE DATABASE IF NOT EXISTS sigepro;
USE sigepro;

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR','JEFE_PROYECTO','USUARIO') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS proyecto (
    id_proyecto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('PLANIFICADO','EN_CURSO','FINALIZADO','CANCELADO') DEFAULT 'PLANIFICADO',
    id_jefe_proyecto INT NOT NULL,
    presupuesto_total DECIMAL(12,2) DEFAULT 0,
    costo_real_total DECIMAL(12,2) DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_jefe_proyecto) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS tarea (
    id_tarea INT AUTO_INCREMENT PRIMARY KEY,
    id_proyecto INT NOT NULL,
    id_tarea_padre INT NULL,
    nombre VARCHAR(150) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    porcentaje_avance INT DEFAULT 0,
    presupuesto_estimado DECIMAL(12,2) DEFAULT 0,
    costo_ejecutado DECIMAL(12,2) DEFAULT 0,
    id_responsable INT NOT NULL,
    FOREIGN KEY (id_proyecto) REFERENCES proyecto(id_proyecto),
    FOREIGN KEY (id_tarea_padre) REFERENCES tarea(id_tarea),
    FOREIGN KEY (id_responsable) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS dependencia_tarea (
    id_dependencia INT AUTO_INCREMENT PRIMARY KEY,
    id_tarea_origen INT NOT NULL,
    id_tarea_destino INT NOT NULL,
    tipo ENUM('FIN_INICIO','INICIO_INICIO','FIN_FIN','INICIO_FIN') DEFAULT 'FIN_INICIO',
    FOREIGN KEY (id_tarea_origen) REFERENCES tarea(id_tarea),
    FOREIGN KEY (id_tarea_destino) REFERENCES tarea(id_tarea)
);

CREATE TABLE IF NOT EXISTS recurso_tarea (
    id_recurso_tarea INT AUTO_INCREMENT PRIMARY KEY,
    id_tarea INT NOT NULL,
    id_usuario INT NOT NULL,
    horas_estimadas DECIMAL(6,2) DEFAULT 0,
    horas_reales DECIMAL(6,2) DEFAULT 0,
    FOREIGN KEY (id_tarea) REFERENCES tarea(id_tarea),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion VARCHAR(100) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    id_entidad INT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);
