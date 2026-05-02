-- Create isolated databases per service
CREATE DATABASE IF NOT EXISTS order_service;
CREATE DATABASE IF NOT EXISTS inventory_service;

-- Create service-scoped users (idempotent)
CREATE USER IF NOT EXISTS 'order_user'@'%' IDENTIFIED BY 'order_pass';
CREATE USER IF NOT EXISTS 'inventory_user'@'%' IDENTIFIED BY 'inventory_pass';

-- Ensure credentials stay aligned even if users already exist
ALTER USER 'order_user'@'%' IDENTIFIED BY 'order_pass';
ALTER USER 'inventory_user'@'%' IDENTIFIED BY 'inventory_pass';

-- Grant least-privilege access per service database
GRANT ALL PRIVILEGES ON order_service.* TO 'order_user'@'%';
GRANT ALL PRIVILEGES ON inventory_service.* TO 'inventory_user'@'%';

FLUSH PRIVILEGES;
