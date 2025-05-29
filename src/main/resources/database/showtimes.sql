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