-- Semillas de regiones y cliente demo. Sin IDs explícitos (la secuencia IDENTITY avanza).
DELETE FROM tbl_customers;
DELETE FROM tbl_regions;

INSERT INTO tbl_regions (name) VALUES ('Sudamérica');     -- id 1
INSERT INTO tbl_regions (name) VALUES ('Centroamérica');  -- id 2
INSERT INTO tbl_regions (name) VALUES ('Norteamérica');   -- id 3
INSERT INTO tbl_regions (name) VALUES ('Europa');         -- id 4
INSERT INTO tbl_regions (name) VALUES ('Asia');           -- id 5
INSERT INTO tbl_regions (name) VALUES ('África');         -- id 6
INSERT INTO tbl_regions (name) VALUES ('Oceanía');        -- id 7
INSERT INTO tbl_regions (name) VALUES ('Antártida');      -- id 8

INSERT INTO tbl_customers (first_name, last_name, email, photo_url, region_id, state)
VALUES ('Andrés', 'Guzmán', 'profesor@bolsadeideas.com', '', 1, 'CREATED');
