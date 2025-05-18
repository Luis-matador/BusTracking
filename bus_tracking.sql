create database if not exists bus_tracking;
create user if not exists 'bus_admin'@'localhost' identified by 'Luis2002';
grant all privileges on bus_tracking.* to 'bus_admin'@'localhost';
flush privileges;
use bus_tracking;


