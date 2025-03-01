-- Bảng movies
CREATE TABLE movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_name VARCHAR(100) NOT NULL UNIQUE,
    duration INT,
    genre VARCHAR(50)
);

-- Bảng showtimes
CREATE TABLE showtimes (
    showtime_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT,
    showtime DATETIME NOT NULL,
    room VARCHAR(10),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

-- Bảng seats
CREATE TABLE seats (
    seat_id INT PRIMARY KEY AUTO_INCREMENT,
    showtime_id INT,
    seat_number VARCHAR(5) NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id)
);

-- Bảng tickets
CREATE TABLE tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    showtime_id INT,
    seat_number VARCHAR(5),
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id)
);


-- Thêm phim
INSERT INTO movies (movie_name, duration, genre) VALUES
('Avengers', 120, 'Action'),
('Titanic', 180, 'Romance'),
('Harry Potter', 150, 'Fantasy'),
('The Shawshank Redemption', 142, 'Drama'),
('Inception', 148, 'Sci-Fi'),
('The Dark Knight', 152, 'Action'),
('Pulp Fiction', 154, 'Crime');

-- Thêm lịch chiếu
INSERT INTO showtimes (movie_id, showtime, room) VALUES
(1, '2025-02-23 19:00:00', 'Room1'), -- Avengers
(2, '2025-02-23 20:00:00', 'Room2'), -- Titanic
(3, '2025-02-24 15:00:00', 'Room1'), -- Harry Potter
(4, '2025-02-24 18:00:00', 'Room1'), -- The Shawshank Redemption
(5, '2025-02-24 19:00:00', 'Room2'), -- Inception
(6, '2025-02-24 20:00:00', 'Room1'), -- The Dark Knight
(7, '2025-02-24 21:00:00', 'Room2'); -- Pulp Fiction

-- Thêm ghế cho tất cả showtime_id
INSERT INTO seats (showtime_id, seat_number, is_booked)
VALUES 
(1, 'A1', FALSE), (1, 'A2', FALSE), (1, 'A3', FALSE), (1, 'A4', FALSE), (1, 'A5', FALSE), (1, 'A6', FALSE), (1, 'A7', FALSE), (1, 'A8', FALSE),
(1, 'B1', FALSE), (1, 'B2', FALSE), (1, 'B3', FALSE), (1, 'B4', TRUE), (1, 'B5', FALSE), (1, 'B6', FALSE), (1, 'B7', FALSE), (1, 'B8', FALSE),
(1, 'C1', FALSE), (1, 'C2', FALSE), (1, 'C3', FALSE), (1, 'C4', FALSE), (1, 'C5', FALSE), (1, 'C6', FALSE), (1, 'C7', FALSE), (1, 'C8', FALSE),
(1, 'D1', FALSE), (1, 'D2', FALSE), (1, 'D3', FALSE), (1, 'D4', FALSE), (1, 'D5', TRUE), (1, 'D6', FALSE), (1, 'D7', FALSE), (1, 'D8', FALSE),
(1, 'E1', FALSE), (1, 'E2', FALSE), (1, 'E3', FALSE), (1, 'E4', TRUE), (1, 'E5', FALSE), (1, 'E6', TRUE), (1, 'E7', FALSE), (1, 'E8', FALSE),
(1, 'F1', FALSE), (1, 'F2', FALSE), (1, 'F3', FALSE), (1, 'F4', FALSE), (1, 'F5', FALSE), (1, 'F6', FALSE), (1, 'F7', FALSE), (1, 'F8', FALSE);

-- Sao chép ghế cho các showtime_id khác với is_booked = FALSE
INSERT INTO seats (showtime_id, seat_number, is_booked)
SELECT 2, seat_number, FALSE FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked)
SELECT 3, seat_number, FALSE FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked)
SELECT 4, seat_number, FALSE FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked)
SELECT 5, seat_number, FALSE FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked)
SELECT 6, seat_number, FALSE FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked)
SELECT 7, seat_number, FALSE FROM seats WHERE showtime_id = 1;

-- Thêm vé mẫu cho Avengers (showtime_id = 1)
INSERT INTO tickets (showtime_id, seat_number) VALUES
(1, 'B4'),
(1, 'D5'),
(1, 'E6'),
(1, 'E4');

-- Kiểm tra dữ liệu
SELECT * FROM movies;
SELECT * FROM showtimes;
SELECT * FROM seats WHERE showtime_id = 1 AND is_booked = TRUE;
SELECT * FROM tickets;

ALTER TABLE movies 
ADD COLUMN age_rating VARCHAR(10), -- Ví dụ: "13+", "16+"
ADD COLUMN director VARCHAR(100);

-- Cập nhật dữ liệu mẫu
UPDATE movies SET age_rating = '13+', director = 'Anthony Russo, Joe Russo' WHERE movie_name = 'Avengers';
UPDATE movies SET age_rating = '13+', director = 'James Cameron' WHERE movie_name = 'Titanic';
UPDATE movies SET age_rating = '13+', director = 'Chris Columbus' WHERE movie_name = 'Harry Potter';
UPDATE movies SET age_rating = '16+', director = 'Frank Darabont' WHERE movie_name = 'The Shawshank Redemption';
UPDATE movies SET age_rating = '13+', director = 'Christopher Nolan' WHERE movie_name = 'Inception';
UPDATE movies SET age_rating = '13+', director = 'Christopher Nolan' WHERE movie_name = 'The Dark Knight';
UPDATE movies SET age_rating = '18+', director = 'Quentin Tarantino' WHERE movie_name = 'Pulp Fiction';

-- Bảng theaters
CREATE TABLE theaters (
    theater_id INT PRIMARY KEY AUTO_INCREMENT,
    theater_name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);

-- Cập nhật bảng showtimes để liên kết với theaters
ALTER TABLE showtimes 
DROP COLUMN room,
ADD COLUMN theater_id INT,
ADD FOREIGN KEY (theater_id) REFERENCES theaters(theater_id);

-- Thêm dữ liệu mẫu cho theaters
INSERT INTO theaters (theater_name, location) VALUES
('CGV Mỹ Tho', 'Mỹ Tho, Tiền Giang'),
('CGV Sài Gòn', 'Quận 1, TP.HCM'),
('Lotte Cinema', 'Quận 7, TP.HCM');

-- Cập nhật showtimes với theater_id
UPDATE showtimes SET theater_id = 1 WHERE showtime_id IN (1, 3, 4, 6); -- CGV Mỹ Tho
UPDATE showtimes SET theater_id = 2 WHERE showtime_id IN (2, 5); -- CGV Sài Gòn
UPDATE showtimes SET theater_id = 3 WHERE showtime_id = 7; -- Lotte Cinema