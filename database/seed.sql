USE sassys_creation_db;

INSERT INTO products (name, category, price, stock_quantity, low_stock_threshold) VALUES
                     ('TEST Rose Bouquet',   'Bouquet',  350.00, 10, 3),
                     ('TEST Crochet Flower', 'Crochet',  120.00,  2, 3),
                     ('TEST Keychain',       'Keychain',  50.00, 20, 5);