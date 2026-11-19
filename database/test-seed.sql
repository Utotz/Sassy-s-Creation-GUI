
-- Sassy's Creation: Temporary Test Data
-- Use this file only for development and DAO testing.
-- Do not import together with seed.sql.

USE sassys_creation_db;

INSERT INTO products (
    name,
    category,
    price,
    stock_quantity,
    low_stock_threshold,
    is_customizable
) VALUES
      ('TEST Rose Bouquet', 'Bouquet', 350.00, 10, 3, FALSE),
      ('TEST Crochet Flower', 'Crochet', 120.00, 2, 3, FALSE),
      ('TEST Keychain', 'Keychain', 50.00, 20, 5, FALSE);
