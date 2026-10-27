-- Sassy's Creation: database structure.
-- WARNING: running this ERASES the rows in the tables below. Test data only.
CREATE DATABASE IF NOT EXISTS sassys_creation_db;
USE sassys_creation_db;

DROP TABLE IF EXISTS products;

CREATE TABLE products (
                          product_id          INT AUTO_INCREMENT PRIMARY KEY,
                          name                VARCHAR(100) NOT NULL,
                          category            ENUM('Bouquet','Crochet','Keychain') NOT NULL,
                          price               DECIMAL(10,2) NOT NULL,
                          stock_quantity      INT NOT NULL DEFAULT 0,
                          low_stock_threshold INT NOT NULL DEFAULT 3,
                          CONSTRAINT chk_price CHECK (price >= 0),
                          CONSTRAINT chk_stock CHECK (stock_quantity >= 0)
);