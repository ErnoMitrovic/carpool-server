-- Insert roles
INSERT INTO carpool_role (name) VALUES ( 'DRIVER'), ('USER'), ('ADMIN');

-- Insert users
INSERT INTO carpool_user (name, email, phone, university_id, role_id, created_at, updated_at, password, is_active)
VALUES
    ('Alice Johnson', 'alice@example.com', '1234567890', 101, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'hashed_password', TRUE),
    ('Bob Smith', 'bob@example.com', '987654321', 102, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'hashed_password', TRUE),
    ('Charlie Brown', 'charlie@example.com', '5647382910', 103, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'hashed_password', TRUE);


-- Insert ride statuses
INSERT INTO ride_status (name) VALUES ('AVAILABLE'), ('CANCELLED'), ('FULL');

-- Insert booking statuses
INSERT INTO booking_status (name) VALUES ('PENDING'),
                                         ('ACCEPTED'),
                                         ('REJECTED'),
                                         ('CANCELLED'),
                                         ('COMPLETED');

-- Insert message statuses
INSERT INTO message_status (name) VALUES ('SENT'), ('DELIVERED'), ('READ');

-- Insert locations (using H2GIS's ST_GeomFromText)
INSERT INTO location (position) VALUES ( ST_GeomFromText('POINT(48.8588443 2.2943506)', 4326)); -- Eiffel Tower
INSERT INTO location (position) VALUES (ST_GeomFromText('POINT(40.748817 -73.985428)', 4326)); -- Empire State Building
INSERT INTO location (position)VALUES (ST_GeomFromText('POINT(51.5007292 -0.1246254)', 4326)); -- Big Ben, London
-- Insert rides
INSERT INTO ride (departure_datetime, available_seats, cost_per_seat, ride_description, driver_id, ride_status_id, start_id, end_id)
VALUES
    (TIMESTAMP '2025-02-20 08:30:00', 3, 10.5, 'Morning commute to university', 2, 1, 1, 2),
    (TIMESTAMP '2025-02-21 18:00:00', 2, 15.0, 'Evening return trip', 2, 1, 2, 1),
    (TIMESTAMP '2025-02-22 13:15:00', 4, 20.0, 'Going to Eiffel tour', 2, 1, 2, 3),
    (TIMESTAMP '2025-02-23 10:45:00', 0, 5.0, 'London Trip', 2, 3, 3, 2);

-- Insert bookings
INSERT INTO booking (created_at, ride_id, user_id, booking_status_id)
VALUES
    (CURRENT_TIMESTAMP, 1, 1, 1),
    (CURRENT_TIMESTAMP, 2, 1, 1),
    (CURRENT_TIMESTAMP, 1, 1, 1),
    (CURRENT_TIMESTAMP, 2, 1, 1),
    (CURRENT_TIMESTAMP, 1, 1, 1),
    (CURRENT_TIMESTAMP, 2, 1, 1);

-- Insert messages
INSERT INTO message (timestamp, content, sender_id, receiver_id, ride_id, message_status_id)
VALUES
    (CURRENT_TIMESTAMP, 'Hey, what time should we meet?', 1, 2, 1, 1),
    (CURRENT_TIMESTAMP, 'See you at 8:15 AM!', 2, 1, 1, 2);

-- Insert users joining rides
INSERT INTO users_have_rides (user_id, ride_id) VALUES (1, 1);
INSERT INTO users_have_rides (user_id, ride_id) VALUES (1, 2);
