DROP DATABASE IF EXISTS MovieTicketDB_New;
CREATE DATABASE MovieTicketDB_New;
USE MovieTicketDB_New;

CREATE TABLE movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_name VARCHAR(100) NOT NULL UNIQUE,
    duration INT,
    genre VARCHAR(50),
    age_rating VARCHAR(10),
    director VARCHAR(100)
);

CREATE TABLE theaters (
    theater_id INT PRIMARY KEY AUTO_INCREMENT,
    theater_name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);

CREATE TABLE showtimes (
    showtime_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT,
    showtime DATETIME NOT NULL,
    theater_id INT,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    FOREIGN KEY (theater_id) REFERENCES theaters(theater_id)
);

CREATE TABLE seats (
    seat_id INT PRIMARY KEY AUTO_INCREMENT,
    showtime_id INT,
    seat_number VARCHAR(5) NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE,
    seat_type ENUM('NORMAL', 'VIP', 'SWEETBOX') DEFAULT 'NORMAL',
    FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id)
);

CREATE TABLE tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    showtime_id INT,
    seat_number VARCHAR(5),
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id)
);

INSERT INTO movies (movie_name, duration, genre, age_rating, director) VALUES
('Avengers', 120, 'Action', '13+', 'Anthony Russo, Joe Russo'),
('Titanic', 180, 'Romance', '13+', 'James Cameron'),
('Harry Potter', 150, 'Fantasy', '13+', 'Chris Columbus'),
('The Shawshank Redemption', 142, 'Drama', '16+', 'Frank Darabont'),
('Inception', 148, 'Sci-Fi', '13+', 'Christopher Nolan'),
('The Dark Knight', 152, 'Action', '13+', 'Christopher Nolan'),
('Pulp Fiction', 154, 'Crime', '18+', 'Quentin Tarantino');

INSERT INTO theaters (theater_name, location) VALUES
('CGV Mỹ Tho', 'Mỹ Tho, Tiền Giang'),
('CGV Sài Gòn', 'Quận 1, TP.HCM'),
('Lotte Cinema', 'Quận 7, TP.HCM'),
('BHD Star Cineplex', 'Quận 3, TP.HCM'),
('Galaxy Cinema', 'Quận Tân Bình, TP.HCM');

INSERT INTO showtimes (movie_id, showtime, theater_id) VALUES
(1, '2025-02-24 19:00:00', 1), -- Avengers, CGV Mỹ Tho, Thứ 2
(1, '2025-02-24 21:00:00', 2), -- Avengers, CGV Sài Gòn, Thứ 2
(3, '2025-02-25 15:00:00', 1), -- Harry Potter, CGV Mỹ Tho, Thứ 3
(2, '2025-02-26 20:00:00', 3), -- Titanic, Lotte Cinema, Thứ 4
(4, '2025-02-27 18:00:00', 4), -- The Shawshank Redemption, BHD Star Cineplex, Thứ 5
(1, '2025-02-28 19:00:00', 1), -- Avengers, CGV Mỹ Tho, Thứ 6
(1, '2025-02-28 21:00:00', 2), -- Avengers, CGV Sài Gòn, Thứ 6
(1, '2025-03-01 10:00:00', 1), -- Avengers, CGV Mỹ Tho, 01/03, 10:00
(1, '2025-03-01 13:00:00', 1), -- Avengers, CGV Mỹ Tho, 01/03, 13:00
(1, '2025-03-01 16:00:00', 1), -- Avengers, CGV Mỹ Tho, 01/03, 16:00
(1, '2025-03-01 19:00:00', 1), -- Avengers, CGV Mỹ Tho, 01/03, 19:00
(3, '2025-03-01 15:00:00', 1), -- Harry Potter, CGV Mỹ Tho, 01/03, 15:00
(3, '2025-03-01 17:00:00', 1), -- Harry Potter, CGV Mỹ Tho, 01/03, 17:00
(3, '2025-03-01 19:00:00', 1), -- Harry Potter, CGV Mỹ Tho, 01/03, 19:00
(3, '2025-03-01 21:00:00', 1), -- Harry Potter, CGV Mỹ Tho, 01/03, 21:00
(2, '2025-03-01 11:00:00', 1), -- Titanic, CGV Mỹ Tho, 01/03, 11:00
(2, '2025-03-01 14:00:00', 1), -- Titanic, CGV Mỹ Tho, 01/03, 14:00
(2, '2025-03-01 17:00:00', 1), -- Titanic, CGV Mỹ Tho, 01/03, 17:00
(2, '2025-03-01 20:00:00', 1), -- Titanic, CGV Mỹ Tho, 01/03, 20:00
(6, '2025-03-02 20:00:00', 2); -- The Dark Knight, CGV Sài Gòn, Chủ nhật

INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) VALUES 
(1, 'A1', FALSE, 'NORMAL'), (1, 'A2', FALSE, 'NORMAL'), (1, 'A3', FALSE, 'NORMAL'), (1, 'A4', FALSE, 'NORMAL'), (1, 'A5', FALSE, 'NORMAL'), (1, 'A6', FALSE, 'NORMAL'), (1, 'A7', FALSE, 'NORMAL'), (1, 'A8', FALSE, 'NORMAL'),
(1, 'B1', FALSE, 'NORMAL'), (1, 'B2', FALSE, 'NORMAL'), (1, 'B3', FALSE, 'NORMAL'), (1, 'B4', TRUE, 'NORMAL'), (1, 'B5', FALSE, 'NORMAL'), (1, 'B6', FALSE, 'NORMAL'), (1, 'B7', FALSE, 'NORMAL'), (1, 'B8', FALSE, 'NORMAL'),
(1, 'C1', FALSE, 'NORMAL'), (1, 'C2', FALSE, 'NORMAL'), (1, 'C3', FALSE, 'NORMAL'), (1, 'C4', FALSE, 'NORMAL'), (1, 'C5', FALSE, 'NORMAL'), (1, 'C6', FALSE, 'NORMAL'), (1, 'C7', FALSE, 'NORMAL'), (1, 'C8', FALSE, 'NORMAL'),
(1, 'D1', FALSE, 'VIP'), (1, 'D2', FALSE, 'VIP'), (1, 'D3', FALSE, 'VIP'), (1, 'D4', FALSE, 'VIP'), (1, 'D5', TRUE, 'VIP'), (1, 'D6', FALSE, 'VIP'), (1, 'D7', FALSE, 'VIP'), (1, 'D8', FALSE, 'VIP'),
(1, 'E1', FALSE, 'VIP'), (1, 'E2', FALSE, 'VIP'), (1, 'E3', FALSE, 'VIP'), (1, 'E4', TRUE, 'VIP'), (1, 'E5', FALSE, 'VIP'), (1, 'E6', TRUE, 'VIP'), (1, 'E7', FALSE, 'VIP'), (1, 'E8', FALSE, 'VIP'),
(1, 'F1', FALSE, 'SWEETBOX'), (1, 'F2', FALSE, 'SWEETBOX'), (1, 'F3', FALSE, 'SWEETBOX'), (1, 'F4', FALSE, 'SWEETBOX'), (1, 'F5', FALSE, 'SWEETBOX'), (1, 'F6', FALSE, 'SWEETBOX'), (1, 'F7', FALSE, 'SWEETBOX'), (1, 'F8', FALSE, 'SWEETBOX');

-- Sao chép ghế cho các showtime_id khác (2 đến 20)
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 2, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 3, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 4, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 5, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 6, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 7, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 8, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 9, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 10, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 11, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 12, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 13, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 14, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 15, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 16, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 17, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 18, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 19, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;
INSERT INTO seats (showtime_id, seat_number, is_booked, seat_type) SELECT 20, seat_number, FALSE, seat_type FROM seats WHERE showtime_id = 1;

INSERT INTO tickets (showtime_id, seat_number) VALUES 
(7, 'B4'), -- Avengers, 28/02/2025, CGV Mỹ Tho
(7, 'D5'),
(7, 'E6'),
(7, 'E4');

SELECT s.showtime_id, s.showtime
FROM showtimes s
JOIN theaters t ON s.theater_id = t.theater_id
JOIN movies m ON s.movie_id = m.movie_id
WHERE m.movie_name = 'Avengers' 
  AND t.theater_name = 'CGV Mỹ Tho' 
  AND DATE(s.showtime) = '2025-03-01';
  
  SELECT s.showtime_id, s.showtime
FROM showtimes s
JOIN theaters t ON s.theater_id = t.theater_id
JOIN movies m ON s.movie_id = m.movie_id
WHERE m.movie_name = 'Avengers' AND t.theater_name = 'CGV Mỹ Tho' AND DATE(s.showtime) = STR_TO_DATE('01/03/2025', '%d/%m/%Y')