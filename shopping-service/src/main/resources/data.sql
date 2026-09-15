-- Factura demo. Sin IDs explícitos (la secuencia IDENTITY avanza). La primera factura
-- obtiene id 1 y sus ítems referencian invoice_id 1 y los product_id 1, 2, 3.
DELETE FROM tbl_invoice_items;
DELETE FROM tbl_invoices;

INSERT INTO tbl_invoices (number_invoice, description, customer_id, create_at, state)
VALUES ('0001', 'invoice office items', 1, DATE '2018-09-05', 'CREATED');

INSERT INTO tbl_invoice_items (invoice_id, product_id, quantity, price) VALUES (1, 1, 1, 178.89);
INSERT INTO tbl_invoice_items (invoice_id, product_id, quantity, price) VALUES (1, 2, 2, 12.5);
INSERT INTO tbl_invoice_items (invoice_id, product_id, quantity, price) VALUES (1, 3, 1, 40.06);
