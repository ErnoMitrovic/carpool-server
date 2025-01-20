-- This script creates the schema for the H2 database
CREATE SCHEMA IF NOT EXISTS carpool;

-- Configurations for H2 database with spatial data
-- Source: https://medium.com/@dev.hr.kim/unit-test-using-h2gis-and-query-dsl-in-spring-boot-5409cb1570f5
CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";
CALL H2GIS_SPATIAL();

-- Drop tables if they exist
DROP TABLE IF EXISTS booking_status, ride_status, carpool_role, carpool_user, location, ride, booking, message_status, message, users_have_rides;

-- Create tables for H2
CREATE TABLE booking_status (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE ride_status (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE carpool_role (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE carpool_user (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(50) NOT NULL,
                        email VARCHAR(50) NOT NULL UNIQUE,
                        phone VARCHAR(20) NOT NULL UNIQUE,
                        university_id INT NOT NULL,
                        role_id INT NOT NULL,
                        FOREIGN KEY (role_id) REFERENCES carpool_role (id)
);

CREATE TABLE location (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          position GEOMETRY(POINT, 4326) NOT NULL
);

CREATE TABLE ride (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      departure_datetime TIMESTAMP NOT NULL,
                      available_seats INT NOT NULL,
                      cost_per_seat DECIMAL(10,2) NOT NULL,
                      ride_description TEXT NOT NULL,
                      driver_id INT NOT NULL,
                      ride_status_id INT NOT NULL,
                      start_id INT NOT NULL,
                      end_id INT NOT NULL,
                      FOREIGN KEY (driver_id) REFERENCES carpool_user (id),
                      FOREIGN KEY (ride_status_id) REFERENCES ride_status (id),
                      FOREIGN KEY (start_id) REFERENCES location (id),
                      FOREIGN KEY (end_id) REFERENCES location (id)
);

CREATE TABLE booking (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         ride_id INT NOT NULL,
                         user_id INT NOT NULL,
                         booking_status_id INT NOT NULL,
                         FOREIGN KEY (ride_id) REFERENCES ride (id),
                         FOREIGN KEY (user_id) REFERENCES carpool_user (id),
                         FOREIGN KEY (booking_status_id) REFERENCES booking_status (id)
);

CREATE TABLE message_status (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE message (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         timestamp TIMESTAMP NOT NULL,
                         content TEXT NOT NULL,
                         sender_id INT NOT NULL,
                         receiver_id INT NOT NULL,
                         ride_id INT NOT NULL,
                         message_status_id INT NOT NULL,
                         FOREIGN KEY (sender_id) REFERENCES carpool_user (id),
                         FOREIGN KEY (receiver_id) REFERENCES carpool_user (id),
                         FOREIGN KEY (ride_id) REFERENCES ride (id),
                         FOREIGN KEY (message_status_id) REFERENCES message_status (id)
);

CREATE TABLE users_have_rides (
                                  user_id INT NOT NULL,
                                  ride_id INT NOT NULL,
                                  PRIMARY KEY (user_id, ride_id),
                                  FOREIGN KEY (user_id) REFERENCES carpool_user (id),
                                  FOREIGN KEY (ride_id) REFERENCES ride (id)
);
