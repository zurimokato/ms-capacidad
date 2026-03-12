-- =============================================================
-- Base de datos: db_capacidad
-- Microservicio: ms-capacidad
-- =============================================================

CREATE DATABASE IF NOT EXISTS db_capacidad;
USE db_capacidad;

CREATE TABLE IF NOT EXISTS capacidades (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS capacidad_tecnologias (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_capacidad   BIGINT NOT NULL,
    id_tecnologia  BIGINT NOT NULL,
    CONSTRAINT fk_cap_tech_capacidad FOREIGN KEY (id_capacidad) REFERENCES capacidades(id) ON DELETE CASCADE,
    CONSTRAINT uk_cap_tech UNIQUE (id_capacidad, id_tecnologia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos de prueba
INSERT INTO capacidades (nombre, descripcion) VALUES
    ('Desarrollador Backend', 'Capacidad para desarrollo de servicios del lado del servidor'),
    ('Desarrollador Frontend', 'Capacidad para desarrollo de interfaces de usuario'),
    ('DevOps', 'Capacidad para automatización de infraestructura y CI/CD');

-- Asumiendo que en db_tecnologia existen tecnologías con id 1-7
INSERT INTO capacidad_tecnologias (id_capacidad, id_tecnologia) VALUES
    (1, 1), (1, 2), (1, 6), (1, 7),  -- Backend: Java, Spring Boot, Mockito, JUnit
    (2, 3), (2, 4), (2, 5),           -- Frontend: Angular, Git, Node.js
    (3, 4), (3, 5), (3, 1);           -- DevOps: Git, Node.js, Java
