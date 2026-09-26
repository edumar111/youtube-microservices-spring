-- Semillas de regiones y cliente demo. IDs explícitos estables + reinicio de secuencia a 100.
DELETE FROM tbl_customers;
DELETE FROM tbl_regions;

INSERT INTO tbl_regions (id, name) VALUES (1, 'Sudamérica');
INSERT INTO tbl_regions (id, name) VALUES (2, 'Centroamérica');
INSERT INTO tbl_regions (id, name) VALUES (3, 'Norteamérica');
INSERT INTO tbl_regions (id, name) VALUES (4, 'Europa');
INSERT INTO tbl_regions (id, name) VALUES (5, 'Asia');
INSERT INTO tbl_regions (id, name) VALUES (6, 'África');
INSERT INTO tbl_regions (id, name) VALUES (7, 'Oceanía');
INSERT INTO tbl_regions (id, name) VALUES (8, 'Antártida');

INSERT INTO tbl_customers (id, first_name, last_name, email, photo_url, region_id, state)
VALUES (1, 'Andrés', 'Guzmán', 'profesor@bolsadeideas.com', '', 1, 'CREATED');

ALTER TABLE tbl_regions ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tbl_customers ALTER COLUMN id RESTART WITH 100;
