USE sassys_creation_db;

-- 1) New column for product pictures (file name inside resources/.../images/)
ALTER TABLE products
    ADD COLUMN image_path VARCHAR(255) NULL AFTER is_customizable;

-- 2) Link existing pictures to products
UPDATE products SET image_path = 'single-lily-tulip.jpg'        WHERE product_id = 6;   -- Tulip - Fuzzy Wire
UPDATE products SET image_path = 'rosa-bouquet.jpg'             WHERE product_id = 11;  -- Rose - Fuzzy Wire
UPDATE products SET image_path = 'tangled.jpg'                  WHERE product_id = 12;  -- Sundrop/Tangled - Fuzzy Wire
UPDATE products SET image_path = 'single-stargazer-bouquet.jpg' WHERE product_id = 13;  -- Stargazer - Fuzzy Wire
UPDATE products SET image_path = 'crochet-tulip-bouquet.jpg'    WHERE product_id = 14;  -- Tulip - Crochet
UPDATE products SET image_path = 'crochet-sunflower-pot.jpg'    WHERE product_id = 15;  -- Sunflower - Crochet
UPDATE products SET image_path = 'rose-serenade.jpg'            WHERE product_id = 16;  -- Rose - Crochet
UPDATE products SET image_path = 'rosette-charm.jpg'            WHERE product_id = 19;  -- Rose - Satin Ribbon
UPDATE products SET image_path = 'jelly-first-keychain.jpg'     WHERE product_id = 26;  -- Jellyfish Keychain
UPDATE products SET image_path = 'mini-bouquet-keychain.jpg'    WHERE product_id = 29;  -- Mini Bouquet Keychain
UPDATE products SET image_path = 'puff-flower-pouch.jpg'        WHERE product_id = 32;  -- Puff Flower Keychain
UPDATE products SET image_path = 'diamond-top.jpg'              WHERE product_id = 40;  -- Diamond Top
UPDATE products SET image_path = 'swim-bra.jpg'                 WHERE product_id = 42;  -- Swimwear - Customize