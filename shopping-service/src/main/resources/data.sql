-- Factura demo. IDs explícitos estables + reinicio de secuencia a 100 (para que las facturas
-- creadas en runtime no colisionen con la semilla).
DELETE FROM tbl_invoice_items;
DELETE FROM tbl_invoices;

INSERT INTO tbl_invoices (id, number_invoice, description, customer_id, create_at, state)
VALUES (1, '0001', 'invoice office items', 1, DATE '2018-09-05', 'CREATED');

INSERT INTO tbl_invoice_items (id, invoice_id, product_id, quantity, price) VALUES (1, 1, 1, 1, 178.89);
INSERT INTO tbl_invoice_items (id, invoice_id, product_id, quantity, price) VALUES (2, 1, 2, 2, 12.5);
INSERT INTO tbl_invoice_items (id, invoice_id, product_id, quantity, price) VALUES (3, 1, 3, 1, 40.06);

ALTER TABLE tbl_invoices ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tbl_invoice_items ALTER COLUMN id RESTART WITH 100;
