-- Initialize the DevicesAPI database
-- This script will be executed when the PostgreSQL container starts

-- Create the devices table
CREATE TABLE IF NOT EXISTS devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    brand VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create an index on brand for better query performance
CREATE INDEX IF NOT EXISTS idx_devices_brand ON devices(brand);

-- Create an index on state for better query performance
CREATE INDEX IF NOT EXISTS idx_devices_state ON devices(state);

-- Insert some sample data
INSERT INTO devices (name, brand, state) VALUES
    ('iPhone 15 Pro', 'APPLE', 'AVAILABLE'),
    ('Galaxy S24', 'SAMSUNG', 'AVAILABLE'),
    ('Pixel 8', 'GOOGLE', 'INACTIVE'),
    ('MacBook Pro', 'APPLE', 'IN_USE'),
    ('Mi 13 Pro', 'XIAOMI', 'AVAILABLE')
ON CONFLICT (name) DO NOTHING;