-- Semillas del catálogo. IDs explícitos estables (para que las FK y las referencias entre
-- servicios sean consistentes entre reinicios) + reinicio de la secuencia IDENTITY a 100
-- para que los registros creados en runtime no colisionen con las semillas.
DELETE FROM tbl_products;
DELETE FROM tbl_categories;

INSERT INTO tbl_categories (id, name) VALUES (1, 'shoes');
INSERT INTO tbl_categories (id, name) VALUES (2, 'books');
INSERT INTO tbl_categories (id, name) VALUES (3, 'electronics');

INSERT INTO tbl_products (id, name, description, stock, price, status, create_at, category_id)
VALUES (1, 'adidas Cloudfoam Ultimate', 'Zapatilla running CLOUDFOAM ULTIMATE de ADIDAS, color negro', 5, 178.89, 'CREATED', DATE '2018-09-05', 1);
INSERT INTO tbl_products (id, name, description, stock, price, status, create_at, category_id)
VALUES (2, 'under armour Micro G Assert 7', 'Malla ligera y transpirable con refuerzos de cuero para estabilidad', 4, 12.5, 'CREATED', DATE '2018-09-05', 1);
INSERT INTO tbl_products (id, name, description, stock, price, status, create_at, category_id)
VALUES (3, 'Spring Boot in Action', 'Craig Walls, autor de Spring in Action', 12, 40.06, 'CREATED', DATE '2018-09-05', 2);

ALTER TABLE tbl_categories ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tbl_products ALTER COLUMN id RESTART WITH 100;
