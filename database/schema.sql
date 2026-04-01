-- 1. Crear la base de datos
CREATE DATABASE IF NOT EXISTS biblioteca_db;
USE biblioteca_db;

-- 2. Crear la tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_usuario ENUM('LECTOR', 'ADMIN') NOT NULL
);

-- 3. Crear un administrador por defecto
INSERT INTO usuarios (nombre, apellidos, fecha_nacimiento, username, password, tipo_usuario) 
VALUES ('Admin', 'General', '1990-01-01', 'admin', 'BIBLIOTECA123', 'ADMIN'),
('Isaac', 'galeana', '2007-04-26', 'isaac', 'isaac123', 'LECTOR'),
('Kevin', 'Ramos', '2007-01-01', 'kevin', 'kevin123', 'LECTOR'),
('Fabian', 'Reyes', '1998-05-15', 'fabian', 'fabian123', 'LECTOR'),
('Tony', 'Anaya', '2009-01-01', 'tony', 'tony123', 'LECTOR');

INSERT INTO libros (isbn, titulo, autor, genero, sinopsis, ejemplares_totales, ejemplares_disponibles, ruta_archivo) VALUES
('978-0001', 'El Principito', 'Antoine de Saint-Exupéry', 'Ficción', 'Un piloto se pierde en el desierto...', 3, 3, 'demo\src\main\Libros\Principito.txt'),
('978-0002', 'Don Quijote de la Mancha', 'Miguel de Cervantes', 'Novela', 'En un lugar de la mancha...', 2, 2, 'demo\src\main\Libros\Quijote.txt'),
('978-0003', 'Libro de Prueba', 'Autor Desconocido', 'Varios', 'Libro para pruebas del sistema.', 5, 5, 'demo\src\main\Libros\Libro_Prueba.txt');

-- 1. Tabla de Libros
CREATE TABLE IF NOT EXISTS libros (
    isbn VARCHAR(20) PRIMARY KEY,      -- Usamos el ISBN como identificador único
    titulo VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    genero VARCHAR(50),
    sinopsis TEXT,
    ejemplares_totales INT DEFAULT 1,
    ejemplares_disponibles INT DEFAULT 1,
    ruta_archivo VARCHAR(255)         -- Para guardar la ruta de los archivos .pdf o .txt
);

-- 2. Tabla de Prestamos
CREATE TABLE IF NOT EXISTS prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,          -- Relación con la tabla usuarios
    isbn_libro VARCHAR(20) NOT NULL,  -- Relación con la tabla libros
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE,            -- Puede ser null hasta que se devuelva
    activo BOOLEAN DEFAULT TRUE,      -- Para saber si el lector aún tiene el libro
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (isbn_libro) REFERENCES libros(isbn) ON DELETE CASCADE
);

-- COMANDOS PARA VER DATOS 

USE biblioteca_db;

SELECT * FROM Usuarios;

SELECT * FROM Prestamos;

SELECT * FROM Libros;
