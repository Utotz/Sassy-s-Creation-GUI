
-- Sassy's Creation: Initial Product Catalog
-- Source: Original C application's products.txt
-- This file inserts the 44 original product records.
--
-- IMPORTANT:
-- Run schema.sql first to create an empty products table.
-- Do not run test-seed.sql alongside this file.

USE sassys_creation_db;

INSERT INTO products (
    product_id,
    name,
    category,
    price,
    stock_quantity,
    low_stock_threshold,
    is_customizable
) VALUES
      (1, 'Gypsophila', 'Bouquet', 30.00, 12, 3, FALSE),
      (2, 'Light Green Leaf', 'Bouquet', 30.00, 20, 3, FALSE),
      (3, 'Spiral Leaf', 'Bouquet', 30.00, 20, 3, FALSE),
      (4, 'Dark Green Leaf', 'Bouquet', 30.00, 20, 3, FALSE),
      (5, 'Mini Daisy (Filler)', 'Bouquet', 30.00, 20, 3, FALSE),
      (6, 'Tulip - Fuzzy Wire', 'Bouquet', 55.00, 15, 3, FALSE),
      (7, 'Lily - Fuzzy Wire', 'Bouquet', 70.00, 15, 3, FALSE),
      (8, 'Tiger Lily - Fuzzy Wire', 'Bouquet', 75.00, 15, 3, FALSE),
      (9, 'Garbera - Fuzzy Wire', 'Bouquet', 55.00, 15, 3, FALSE),
      (10, 'Sunflower - Fuzzy Wire', 'Bouquet', 55.00, 15, 3, FALSE),
      (11, 'Rose - Fuzzy Wire', 'Bouquet', 90.00, 15, 3, FALSE),
      (12, 'Sundrop/Tangled - Fuzzy Wire', 'Bouquet', 145.00, 10, 3, FALSE),
      (13, 'Stargazer - Fuzzy Wire', 'Bouquet', 160.00, 10, 3, FALSE),
      (14, 'Tulip - Crochet', 'Bouquet', 95.00, 10, 3, FALSE),
      (15, 'Sunflower - Crochet', 'Bouquet', 120.00, 10, 3, FALSE),
      (16, 'Rose - Crochet', 'Bouquet', 100.00, 10, 3, FALSE),
      (17, 'Stargazer - Crochet', 'Bouquet', 200.00, 8, 3, FALSE),
      (18, 'Lily - Crochet', 'Bouquet', 150.00, 8, 3, FALSE),
      (19, 'Rose - Satin Ribbon', 'Bouquet', 60.00, 12, 3, FALSE),
      (20, 'Mixed Bouquet - Fuzzy Wire (Small)', 'Bouquet', 250.00, 5, 3, FALSE),
      (21, 'Mixed Bouquet - Fuzzy Wire (Medium)', 'Bouquet', 400.00, 5, 3, FALSE),
      (22, 'Mixed Bouquet - Crochet (Small)', 'Bouquet', 350.00, 5, 3, FALSE),
      (23, 'Mixed Bouquet - Crochet (Medium)', 'Bouquet', 550.00, 4, 3, FALSE),
      (24, 'Gradient Bouquet - Satin Ribbon', 'Bouquet', 300.00, 4, 3, FALSE),
      (25, 'Octopus Keychain', 'Keychain', 110.00, 10, 3, FALSE),
      (26, 'Jellyfish Keychain', 'Keychain', 110.00, 10, 3, FALSE),
      (27, 'Sunflower Keychain', 'Keychain', 50.00, 15, 3, FALSE),
      (28, 'Rose Keychain', 'Keychain', 50.00, 5, 3, FALSE),
      (29, 'Mini Bouquet Keychain', 'Keychain', 110.00, 10, 3, FALSE),
      (30, 'Baby Tulip Keychain', 'Keychain', 40.00, 15, 3, FALSE),
      (31, 'Baby Mushroom Keychain', 'Keychain', 80.00, 10, 3, FALSE),
      (32, 'Puff Flower Keychain', 'Keychain', 45.00, 10, 3, FALSE),
      (33, 'Baby Dinosaur Keychain', 'Keychain', 90.00, 10, 3, FALSE),
      (34, 'Mini Teddy Bear Keychain', 'Keychain', 90.00, 10, 3, FALSE),
      (35, 'Customized Keychain', 'Keychain', 0.00, 0, 3, TRUE),
      (36, 'Mini Tulip Keychain - Fuzzy Wire', 'Keychain', 20.00, 20, 3, FALSE),
      (37, 'Mini Daisy Keychain - Fuzzy Wire', 'Keychain', 20.00, 20, 3, FALSE),
      (38, 'Ribbon Keychain - Fuzzy Wire', 'Keychain', 10.00, 20, 3, FALSE),
      (39, 'Crochet Bra', 'Crochet', 200.00, 5, 3, FALSE),
      (40, 'Diamond Top', 'Crochet', 300.00, 4, 3, FALSE),
      (41, 'Hair Band', 'Crochet', 100.00, 8, 3, FALSE),
      (42, 'Swimwear - Customize', 'Crochet', 0.00, 0, 3, TRUE),
      (43, 'Hat - Customize', 'Crochet', 0.00, 0, 3, TRUE),
      (44, 'Bag - Customize', 'Crochet', 0.00, 5, 3, TRUE);
