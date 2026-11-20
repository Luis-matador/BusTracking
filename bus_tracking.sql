-- Crea la base de datos y un usuario dedicado. Cambia la contraseña y usa la misma en DB_PASSWORD.
create database if not exists bus_tracking;
create user if not exists 'bus_admin'@'localhost' identified by 'cambia_esta_contraseña';
grant all privileges on bus_tracking.* to 'bus_admin'@'localhost';
flush privileges;
use bus_tracking;
