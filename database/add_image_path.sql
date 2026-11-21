USE sassys_creation_db;

-- Column already exists, run only on a fresh database
-- ALTER TABLE products
--     ADD COLUMN image_path VARCHAR(255) NULL AFTER is_customizable;

-- Bouquets: fillers and leaves (all guesses)
UPDATE products SET image_path = 'little-blossom-serenade.jpg'   WHERE product_id = 1;   -- Gypsophila (verify)
UPDATE products SET image_path = 'thumbelina-cone-bouquet.jpg'   WHERE product_id = 2;   -- Light Green Leaf (verify, placeholder)
UPDATE products SET image_path = 'blooming-grace-bouquet.jpg'    WHERE product_id = 3;   -- Spiral Leaf (verify, placeholder)
UPDATE products SET image_path = 'tangled-grand.jpg'             WHERE product_id = 4;   -- Dark Green Leaf (verify, placeholder)
UPDATE products SET image_path = 'florea-bouquet-v2.jpg'         WHERE product_id = 5;   -- Mini Daisy (Filler) (verify, placeholder)

-- Bouquets: fuzzy wire
UPDATE products SET image_path = 'single-lily-tulip.jpg'         WHERE product_id = 6;   -- Tulip - Fuzzy Wire
UPDATE products SET image_path = 'florea-bouquet-v3.jpg'         WHERE product_id = 7;   -- Lily - Fuzzy Wire (verify, placeholder)
UPDATE products SET image_path = 'stargazer-embrace-bouquet.jpg' WHERE product_id = 8;   -- Tiger Lily - Fuzzy Wire (verify, placeholder)
UPDATE products SET image_path = 'red-tulip-bouquet.jpg'         WHERE product_id = 9;   -- Garbera - Fuzzy Wire (verify)
UPDATE products SET image_path = 'sunflower-bouquet.jpg'         WHERE product_id = 10;  -- Sunflower - Fuzzy Wire
UPDATE products SET image_path = 'rosa-bouquet.jpg'              WHERE product_id = 11;  -- Rose - Fuzzy Wire
UPDATE products SET image_path = 'tangled.jpg'                   WHERE product_id = 12;  -- Sundrop/Tangled - Fuzzy Wire
UPDATE products SET image_path = 'single-stargazer-bouquet.jpg'  WHERE product_id = 13;  -- Stargazer - Fuzzy Wire

-- Bouquets: crochet
UPDATE products SET image_path = 'crochet-tulip-bouquet.jpg'     WHERE product_id = 14;  -- Tulip - Crochet
UPDATE products SET image_path = 'crochet-sunflower-pot.jpg'     WHERE product_id = 15;  -- Sunflower - Crochet
UPDATE products SET image_path = 'rose-serenade.jpg'             WHERE product_id = 16;  -- Rose - Crochet
UPDATE products SET image_path = 'single-stargazer-v2.jpg'       WHERE product_id = 17;  -- Stargazer - Crochet (verify)
UPDATE products SET image_path = 'crochet-single-tulip.jpg'      WHERE product_id = 18;  -- Lily - Crochet (verify)

-- Bouquets: satin ribbon and mixed
UPDATE products SET image_path = 'rosette-charm.jpg'             WHERE product_id = 19;  -- Rose - Satin Ribbon
UPDATE products SET image_path = 'mini-bouquet-fuzzy-wire.jpg'   WHERE product_id = 20;  -- Mixed Bouquet - Fuzzy Wire (Small) (verify)
UPDATE products SET image_path = 'florea-bouquet.jpg'            WHERE product_id = 21;  -- Mixed Bouquet - Fuzzy Wire (Medium) (verify)
UPDATE products SET image_path = 'set-gift.jpg'                  WHERE product_id = 22;  -- Mixed Bouquet - Crochet (Small) (verify)
UPDATE products SET image_path = 'sophia-bouquet.jpg'            WHERE product_id = 23;  -- Mixed Bouquet - Crochet (Medium) (verify)
UPDATE products SET image_path = 'sunblush-bouquet.jpg'          WHERE product_id = 24;  -- Gradient Bouquet - Satin Ribbon (verify)

-- Keychains
UPDATE products SET image_path = 'moana-inspired-keychain.jpg'   WHERE product_id = 25;  -- Octopus Keychain (verify)
UPDATE products SET image_path = 'jelly-first-keychain.jpg'      WHERE product_id = 26;  -- Jellyfish Keychain
UPDATE products SET image_path = 'mini-sunflower-50.jpg'         WHERE product_id = 27;  -- Sunflower Keychain (verify)
UPDATE products SET image_path = 'satin-single-tulip.jpg'        WHERE product_id = 28;  -- Rose Keychain (verify)
UPDATE products SET image_path = 'mini-bouquet-keychain.jpg'     WHERE product_id = 29;  -- Mini Bouquet Keychain
UPDATE products SET image_path = 'mini-tulip-in-card.jpg'        WHERE product_id = 30;  -- Baby Tulip Keychain (verify)
UPDATE products SET image_path = 'mini-flower-pouch.jpg'         WHERE product_id = 31;  -- Baby Mushroom Keychain (verify)
UPDATE products SET image_path = 'puff-flower-pouch.jpg'         WHERE product_id = 32;  -- Puff Flower Keychain
UPDATE products SET image_path = 'mini-labubu.jpg'               WHERE product_id = 33;  -- Baby Dinosaur Keychain (verify)
UPDATE products SET image_path = 'coffee-bouquet.jpg'            WHERE product_id = 34;  -- Mini Teddy Bear Keychain (verify, placeholder)
-- product 35 (Customized Keychain) stays NULL: no image left
UPDATE products SET image_path = 'crochet-tulip-pot.jpg'         WHERE product_id = 36;  -- Mini Tulip Keychain - Fuzzy Wire (verify, placeholder)
UPDATE products SET image_path = 'mini-sunflower-35.jpg'         WHERE product_id = 37;  -- Mini Daisy Keychain - Fuzzy Wire (verify)
UPDATE products SET image_path = 'big-rose-bouquet.jpg'          WHERE product_id = 38;  -- Ribbon Keychain - Fuzzy Wire (verify, placeholder)

-- Crochet
UPDATE products SET image_path = 'crochet-pot-set.jpg'           WHERE product_id = 39;  -- Crochet Bra (verify, placeholder)
UPDATE products SET image_path = 'diamond-top.jpg'               WHERE product_id = 40;  -- Diamond Top
UPDATE products SET image_path = 'frog-headband.jpg'             WHERE product_id = 41;  -- Hair Band (verify)
UPDATE products SET image_path = 'swim-bra.jpg'                  WHERE product_id = 42;  -- Swimwear - Customize
UPDATE products SET image_path = 'crochet-cactus-pot.jpg'        WHERE product_id = 43;  -- Hat - Customize (verify, placeholder)
UPDATE products SET image_path = 'thumbler-holder.jpg'           WHERE product_id = 44;  -- Bag - Customize (verify, placeholder)