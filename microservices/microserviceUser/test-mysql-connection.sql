-- Test MySQL Connection
-- Run this in your MySQL client to verify connection

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS ProjetPI;

-- Use the database
USE ProjetPI;

-- Show tables (should be empty initially)
SHOW TABLES;

-- Test connection
SELECT 'MySQL Connection Successful!' as status;
