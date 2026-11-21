-- Sassy's Creation: database structure.
-- WARNING: running this ERASES the rows in the tables below. Test data only.
CREATE DATABASE IF NOT EXISTS sassys_creation_db;
USE sassys_creation_db;

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;

CREATE TABLE products (
                          product_id          INT AUTO_INCREMENT PRIMARY KEY,
                          name                VARCHAR(100) NOT NULL,
                          category            ENUM('Bouquet','Crochet','Keychain') NOT NULL,
                          price               DECIMAL(10,2) NOT NULL,
                          stock_quantity      INT NOT NULL DEFAULT 0,
                          low_stock_threshold INT NOT NULL DEFAULT 3,
                          is_customizable BOOLEAN NOT NULL DEFAULT FALSE,
                          image_path VARCHAR(255) NULL,
                          CONSTRAINT chk_price CHECK (price >= 0),
                          CONSTRAINT chk_stock CHECK (stock_quantity >= 0)
);

CREATE TABLE orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY,

                        customer_name VARCHAR(100) NOT NULL,

                        product_id INT NOT NULL,

                        color VARCHAR(50) NOT NULL,

                        custom_description TEXT NULL,

                        quantity INT NOT NULL DEFAULT 1,

                        unit_price DECIMAL(10,2) NOT NULL,

                        arrangement ENUM('Pickup', 'Meetup') NOT NULL,

                        schedule_date DATE NOT NULL,

                        status ENUM('Pending', 'Done', 'Cancelled')
                            NOT NULL DEFAULT 'Pending',

                        CONSTRAINT chk_order_quantity
                            CHECK (quantity > 0),

                        CONSTRAINT chk_order_price
                            CHECK (unit_price >= 0),

                        CONSTRAINT fk_orders_product
                            FOREIGN KEY (product_id)
                                REFERENCES products(product_id)
                                ON DELETE RESTRICT
);