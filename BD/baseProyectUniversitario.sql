USE education_db;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS educational_package;
DROP TABLE IF EXISTS program;
DROP TABLE IF EXISTS user;

-- Tabla de usuarios
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(225) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') DEFAULT 'STUDENT'
);

-- Tabla de programas
CREATE TABLE program (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Tabla de paquetes educativos
CREATE TABLE educational_package (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    program_id INT,
    FOREIGN KEY (program_id) REFERENCES program(id)
);

-- Tabla de cursos
CREATE TABLE course (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    package_id INT,
    FOREIGN KEY (package_id) REFERENCES educational_package(id)
);

-- Tabla de matrículas
CREATE TABLE enrollment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    package_id INT,
    enrollment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'CANCELLED', 'COMPLETED') DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (package_id) REFERENCES educational_package(id)
);

-- Tabla de pagos
CREATE TABLE payment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50),
    status ENUM('PAID', 'PENDING', 'FAILED') DEFAULT 'PENDING',
    FOREIGN KEY (enrollment_id) REFERENCES enrollment(id)
);
