CREATE DATABASE ga4w20;
USE ga4w20;


CREATE USER ga4w20@'localhost' IDENTIFIED WITH mysql_native_password BY 'pickle7slab' REQUIRE NONE;
CREATE USER ga4w20@'%' IDENTIFIED WITH mysql_native_password BY 'pickle7slab' REQUIRE NONE;


GRANT ALL ON ga4w20.* TO ga4w20@'localhost';
GRANT ALL ON ga4w20.* TO ga4w20@'%';

FLUSH PRIVILEGES;
