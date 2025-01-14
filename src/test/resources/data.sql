INSERT INTO carpool_role (name) VALUES
                                ('Student'),
                                ('Driver'),
                                ('Admin');

INSERT INTO carpool_user (name, email, phone, university_id, role_id) VALUES
                                                                        ('Alice Johnson', 'alice.johnson@university.edu', 1234567890, 1, 2),
                                                                        ('Bob Smith', 'bob.smith@university.edu', 1234567891, 1, 1),
                                                                        ('Charlie Brown', 'charlie.brown@university.edu', 1234567892, 2, 2),
                                                                        ('Diana Prince', 'diana.prince@university.edu', 1234567893, 2, 1),
                                                                        ('Eve Adams', 'eve.adams@university.edu', 1234567894, 3, 2);

INSERT INTO location (position) VALUES
                                        (ST_SETSRID(ST_MakePoint(-74.0060, 40.7128), 4326)),  -- New York
                                        (ST_SETSRID(ST_MakePoint(-118.2437, 34.0522), 4326)), -- Los Angeles
                                        (ST_SETSRID(ST_MakePoint(-122.4194, 37.7749), 4326)), -- San Francisco
                                        (ST_SETSRID(ST_MakePoint(-71.0589, 42.3601), 4326)),  -- Boston
                                        (ST_SETSRID(ST_MakePoint(-122.3321, 47.6062), 4326)); -- Seattle

INSERT INTO ride_status (name) VALUES
                                       ('Available'),
                                       ('Full'),
                                       ('Cancelled');

INSERT INTO ride (departure_datetime, available_seats, cost_per_seat, ride_description, driver_id, ride_status_id, start_id, end_id) VALUES
                                                                                                                                           ('2025-01-15 08:00:00', 3, 10.50, 'Morning ride from NYU to UCLA', 1, 1, 1, 2),
                                                                                                                                           ('2025-01-16 12:30:00', 2, 15.00, 'Afternoon trip from San Francisco to Boston', 3, 1, 3, 4),
                                                                                                                                           ('2025-01-17 18:45:00', 4, 7.00, 'Evening ride from Seattle to NYU', 5, 2, 5, 1),
                                                                                                                                           ('2025-01-18 15:00:00', 1, 20.00, 'Single-seat luxury ride from LA to San Francisco', 1, 1, 2, 3),
                                                                                                                                           ('2025-01-19 09:00:00', 5, 5.00, 'Budget-friendly trip from Boston to Seattle', 4, 1, 4, 5);

INSERT INTO users_have_rides (user_id, ride_id) VALUES
                                                    (2, 1),  -- Bob joins Alice's ride
                                                    (4, 1),  -- Diana joins Alice's ride
                                                    (5, 2),  -- Eve joins Charlie's ride
                                                    (3, 3),  -- Charlie joins Eve's ride
                                                    (2, 4);  -- Bob joins Alice's second ride

INSERT INTO booking_status (name) VALUES
                                          ('Pending'),
                                          ('Confirmed'),
                                          ('Cancelled');

INSERT INTO booking (created_at, ride_id, user_id, booking_status_id) VALUES
                                                                              (NOW(), 1, 2, 2),  -- Bob confirmed Alice's ride
                                                                              (NOW(), 1, 4, 2),  -- Diana confirmed Alice's ride
                                                                              (NOW(), 2, 5, 1),  -- Eve's booking is pending
                                                                              (NOW(), 3, 3, 2),  -- Charlie confirmed Eve's ride
                                                                              (NOW(), 4, 2, 3);  -- Bob cancelled Alice's second ride
INSERT INTO message_status (name) VALUES
                                          ('Sent'),
                                          ('Delivered'),
                                          ('Read');
INSERT INTO message (timestamp, content, sender_id, receiver_id, ride_id, message_status_id) VALUES
                                                                                                     (NOW(), 'Is this ride still available?', 2, 1, 1, 1),
                                                                                                     ( NOW(), 'Yes, there are 2 seats left!', 1, 2, 1, 2),
                                                                                                     ( NOW(), 'Great! I’ll join.', 2, 1, 1, 3),
                                                                                                     (NOW(), 'Can I book this ride?', 5, 3, 2, 1),
                                                                                                     (NOW(), 'Sure, I’ll confirm it.', 3, 5, 2, 2);
