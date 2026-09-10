-- Semillas del catálogo. Sin IDs explícitos: la columna IDENTITY los genera y la
-- secuencia avanza correctamente (evita colisiones al crear nuevos registros).
DELETE FROM tbl_products;
DELETE FROM tbl_categories;

INSERT INTO tbl_categories (name) VALUES ('shoes');        -- id 1
INSERT INTO tbl_categories (name) VALUES ('books');        -- id 2
INSERT INTO tbl_categories (name) VALUES ('electronics');  -- id 3

INSERT INTO tbl_products (name, description, stock, price, status, create_at, category_id)
VALUES ('adidas Cloudfoam Ultimate', 'Zapatilla running CLOUDFOAM ULTIMATE de ADIDAS, color negro', 5, 178.89, 'CREATED', DATE '2018-09-05', 1);

INSERT INTO tbl_products (name, description, stock, price, status, create_at, category_id)
VALUES ('under armour Micro G Assert 7', 'Malla ligera y transpirable con refuerzos de cuero para estabilidad', 4, 12.5, 'CREATED', DATE '2018-09-05', 1);

INSERT INTO tbl_products (name, description, stock, price, status, create_at, category_id)
VALUES ('Spring Boot in Action', 'Craig Walls, autor de Spring in Action', 12, 40.06, 'CREATED', DATE '2018-09-05', 2);
