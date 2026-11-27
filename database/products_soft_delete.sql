-- Soft delete for products. Run ONCE on an existing database.
-- Only adds a column and a rule; no rows are removed.
-- Do not run schema.sql for this: it drops tables.
USE sassys_creation_db;

ALTER TABLE products
    ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE products
    ADD CONSTRAINT uq_products_name UNIQUE (name);