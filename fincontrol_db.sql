-- Crear base de datos
CREATE DATABASE IF NOT EXISTS fincontrol_db;
USE fincontrol_db;

-- Tabla categorias
CREATE TABLE categorias (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    tipo ENUM('Gasto','Ingreso') NOT NULL
);

-- Tabla transacciones
CREATE TABLE transacciones (
    id_transaccion INT PRIMARY KEY AUTO_INCREMENT,
    monto DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(200),
    fecha DATETIME NOT NULL,
    tipo ENUM('Gasto','Ingreso') NOT NULL,
    id_categoria INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON DELETE SET NULL
);

-- Datos de ejemplo para categorías
INSERT INTO categorias (nombre, tipo) VALUES 
('Salario', 'Ingreso'),
('Freelance', 'Ingreso'),
('Alimentación', 'Gasto'),
('Transporte', 'Gasto'),
('Entretenimiento', 'Gasto'),
('Servicios', 'Gasto');



 -- SCRIP PARA PRIVILEGIOS (CORRER LA APP)

-- Asegúrate de que el usuario 'root' pueda conectarse desde cualquier dirección IP ('%').

-- Reemplaza 'n0m3l0' con la contraseña correcta que tienes en tu archivo ConexionDB.java.

-- (La contraseña en el código era: private static final String PASSWORD = "n0m3l0";)

-- Si usas MySQL 8.0+:
-- CREATE USER 'root'@'%' IDENTIFIED BY 'n0m3l0';
-- GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
-- FLUSH PRIVILEGES;

-- Si usas MySQL 5.7 o anterior, y el usuario ya existe:
-- UPDATE mysql.user SET Host='%' WHERE User='root' AND Host='localhost';
-- FLUSH PRIVILEGES;