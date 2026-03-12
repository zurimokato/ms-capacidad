CREATE TABLE IF NOT EXISTS capacidades (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS capacidad_tecnologias (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_capacidad   BIGINT NOT NULL,
    id_tecnologia  BIGINT NOT NULL,
    CONSTRAINT fk_cap_tech_capacidad FOREIGN KEY (id_capacidad) REFERENCES capacidades(id),
    CONSTRAINT uk_cap_tech UNIQUE (id_capacidad, id_tecnologia)
);
