-- Enable h2gis extension
CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";
CALL H2GIS_SPATIAL();

-- Enable foreign key constraints
SET REFERENTIAL_INTEGRITY FALSE;

-- Creating tables
CREATE TABLE carpool_role (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE carpool_user (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(50) NOT NULL,
                              email VARCHAR(50) NOT NULL UNIQUE,
                              phone VARCHAR(15) NOT NULL UNIQUE,
                              university_id BIGINT,
                              role_id BIGINT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              password VARCHAR(255) NOT NULL,
                              is_active BOOLEAN DEFAULT TRUE,
                              FOREIGN KEY (role_id) REFERENCES carpool_role(id) ON DELETE NO ACTION
);

CREATE TABLE booking_status (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE ride_status (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE message_status (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE location (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          position GEOMETRY NOT NULL
);

CREATE TABLE ride (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      departure_datetime TIMESTAMP NOT NULL,
                      available_seats INT NOT NULL,
                      cost_per_seat REAL NOT NULL,
                      ride_description TEXT NOT NULL,
                      driver_id BIGINT NOT NULL,
                      ride_status_id BIGINT NOT NULL,
                      start_id BIGINT NOT NULL,
                      end_id BIGINT NOT NULL,
                      FOREIGN KEY (driver_id) REFERENCES carpool_user(id) ON DELETE NO ACTION,
                      FOREIGN KEY (ride_status_id) REFERENCES ride_status(id) ON DELETE NO ACTION,
                      FOREIGN KEY (start_id) REFERENCES location(id) ON DELETE NO ACTION,
                      FOREIGN KEY (end_id) REFERENCES location(id) ON DELETE NO ACTION
);

CREATE TABLE booking (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         created_at TIMESTAMP NOT NULL,
                         ride_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         booking_status_id BIGINT NOT NULL,
                         FOREIGN KEY (ride_id) REFERENCES ride(id) ON DELETE NO ACTION,
                         FOREIGN KEY (user_id) REFERENCES carpool_user(id) ON DELETE NO ACTION,
                         FOREIGN KEY (booking_status_id) REFERENCES booking_status(id) ON DELETE NO ACTION
);

CREATE TABLE message (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         timestamp TIMESTAMP NOT NULL,
                         content TEXT NOT NULL,
                         sender_id BIGINT NOT NULL,
                         receiver_id BIGINT NOT NULL,
                         ride_id BIGINT NOT NULL,
                         message_status_id BIGINT NOT NULL,
                         FOREIGN KEY (sender_id) REFERENCES carpool_user(id) ON DELETE NO ACTION,
                         FOREIGN KEY (receiver_id) REFERENCES carpool_user(id) ON DELETE NO ACTION,
                         FOREIGN KEY (ride_id) REFERENCES ride(id) ON DELETE NO ACTION,
                         FOREIGN KEY (message_status_id) REFERENCES message_status(id) ON DELETE NO ACTION
);

CREATE TABLE users_have_rides (
                                  user_id BIGINT NOT NULL,
                                  ride_id BIGINT NOT NULL,
                                  PRIMARY KEY (user_id, ride_id),
                                  FOREIGN KEY (user_id) REFERENCES carpool_user(id) ON DELETE NO ACTION,
                                  FOREIGN KEY (ride_id) REFERENCES ride(id) ON DELETE NO ACTION
);

-- Enable foreign key constraints
SET REFERENTIAL_INTEGRITY TRUE;
