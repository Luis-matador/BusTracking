drop database if exists bus_tracking;
create database bus_tracking;
create user if not exists 'bus_admin'@'localhost' identified by 'Luis2002';
grant all privileges on bus_tracking.* to 'bus_admin'@'localhost';
flush privileges;

use bus_tracking;
CREATE TABLE IF NOT EXISTS gps_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bus_id VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    speed DOUBLE NOT NULL
);