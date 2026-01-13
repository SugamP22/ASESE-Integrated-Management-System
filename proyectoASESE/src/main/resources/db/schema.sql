CREATE DATABASE IF NOT EXISTS asese;

USE asese;

-- TABLA USUARIOS
CREATE TABLE IF NOT EXISTS users (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'USER') NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    email_token VARCHAR(20) NOT NULL,
    passw VARCHAR(255) NOT NULL
);

-- TABLA PROYECTOS
CREATE TABLE IF NOT EXISTS projects (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL
);

-- TABLA CONTRATISTAS
CREATE TABLE IF NOT EXISTS contractors (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    addres VARCHAR(255)
);

-- TABLA ETAPAS
CREATE TABLE IF NOT EXISTS stages (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    id_project INT(5) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    descripcion TEXT,
    initial_date DATE,
    final_date DATE,
    permissionCount INT(5) DEFAULT 0,
    initial_delivery_count INT(5) DEFAULT 0,
    final_delivery_count INT(5) DEFAULT 0,
    CONSTRAINT fk_stages_projects FOREIGN KEY (id_project) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA ENTREGAS
CREATE TABLE IF NOT EXISTS deliveries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    material VARCHAR(150),
    descripcion TEXT,
    id_stage INT(5) NOT NULL,
    tipo_entrega ENUM('INITIAL', 'FINAL') NOT NULL,
    CONSTRAINT fk_deliveries_stages FOREIGN KEY (id_stage) REFERENCES stages (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA EQUIPOS
CREATE TABLE IF NOT EXISTS crew (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    job_type VARCHAR(150),
    id_contractor INT(5) NOT NULL,
    id_project INT(5) NOT NULL,
    CONSTRAINT fk_equipos_contractors FOREIGN KEY (id_contractor) REFERENCES contractors (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_equipos_projects FOREIGN KEY (id_project) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA ACCESOS A PROYECTOS
CREATE TABLE IF NOT EXISTS project_access (
    id_project INT(5) NOT NULL,
    id_user INT(5) NOT NULL,
    PRIMARY KEY (id_project, id_user),
    CONSTRAINT fk_project_access_projects FOREIGN KEY (id_project) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_project_access_users FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA PERMISOS
CREATE TABLE IF NOT EXISTS permissions (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    id_stage INT(5) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    descripcion TEXT,
    CONSTRAINT fk_permissions_stages FOREIGN KEY (id_stage) REFERENCES stages (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA LOGS
CREATE TABLE IF NOT EXISTS logs (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    date_log DATE NOT NULL,
    description TEXT,
    id_user INT(5) NOT NULL,
    CONSTRAINT fk_logs_users FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA TRANSCURSOS
CREATE TABLE IF NOT EXISTS courses (
    id_project INT(5) NOT NULL,
    id_stage INT(5) NOT NULL,
    PRIMARY KEY (id_project, id_stage),
    CONSTRAINT fk_courses_projects FOREIGN KEY (id_project) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_courses_stages FOREIGN KEY (id_stage) REFERENCES stages (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA WHITELIST
CREATE TABLE IF NOT EXISTS whitelist (
    id INT(5) AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) UNIQUE);
