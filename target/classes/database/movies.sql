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