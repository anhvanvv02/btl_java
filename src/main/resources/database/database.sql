-- Create database
CREATE DATABASE IF NOT EXISTS cinema_db;
USE cinema_db;

-- Create employees table
CREATE TABLE IF NOT EXISTS employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    position VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Create movies table
CREATE TABLE IF NOT EXISTS movies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    director VARCHAR(100) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    duration INT NOT NULL,
    release_date DATETIME NOT NULL,
    description TEXT,
    poster_url VARCHAR(500),
    actors TEXT NOT NULL
);



-- Create theaters table
CREATE TABLE IF NOT EXISTS theaters (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    screen_type VARCHAR(20) NOT NULL,
    status BOOLEAN DEFAULT true
);

-- Create tickets table
CREATE TABLE IF NOT EXISTS tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT NOT NULL,
    theater_id INT NOT NULL,
    show_time DATETIME NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    employee_id INT NOT NULL,
    purchase_date DATETIME NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (theater_id) REFERENCES theaters(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS movies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100),
    duration INT,
    image_path VARCHAR(255),
    director VARCHAR(100),
    release_date VARCHAR(20),
    language VARCHAR(50),
    rating VARCHAR(10)
); 

-- Drop existing table if exists
DROP TABLE IF EXISTS showtimes;

-- Create showtimes table
CREATE TABLE IF NOT EXISTS showtimes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT NOT NULL,
    theater_id INT NOT NULL,
    show_datetime DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status BOOLEAN DEFAULT true,
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (theater_id) REFERENCES theaters(id)
);

-- Add indexes for better performance
CREATE INDEX idx_movie_id ON showtimes(movie_id);
CREATE INDEX idx_theater_id ON showtimes(theater_id);
CREATE INDEX idx_show_datetime ON showtimes(show_datetime); 
-- Add poster_url column if it doesn't exist
ALTER TABLE movies ADD COLUMN IF NOT EXISTS poster_url VARCHAR(500);

-- Add actors column if it doesn't exist
ALTER TABLE movies ADD COLUMN IF NOT EXISTS actors TEXT NOT NULL DEFAULT '';

-- Modify release_date column to DATETIME if it exists
ALTER TABLE movies MODIFY COLUMN release_date DATETIME NOT NULL;

-- Modify actors column to TEXT NOT NULL if it exists
ALTER TABLE movies MODIFY COLUMN actors TEXT NOT NULL;

-- Drop movie_actors table if it exists
DROP TABLE IF EXISTS movie_actors;
-- Insert default admin account
INSERT INTO employees (name, phone, email, address, position, username, password)
VALUES ('Admin', '0123456789', 'admin@cinema.com', 'Cinema Address', 'Admin', 'admin', 'admin123'); 