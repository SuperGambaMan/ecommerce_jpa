-- Datos de ejemplo para ecommerce_bd

-- Categorías
INSERT INTO ecommerce_bd.category (name, descrip) VALUES
                                                      ('Juguetes', 'Juguetes para gatos de todo tipo'),
                                                      ('Comida', 'Comida premium para gatos'),
                                                      ('Accesorios', 'Accesorios para el día a día de tu gato');

-- Productos
INSERT INTO ecommerce_bd.product (name, descrip, image_url, sku, price, quantity, category_id) VALUES
                                                                                                   ('Pelota interactiva', 'Pelota con luz y movimiento', 'https://img.com/p1', 'SKU005', 9.990, 100, 1),
                                                                                                   ('Rascador grande', 'Rascador de varios pisos', 'https://img.com/p2', 'SKU002', 49.990, 50, 1),
                                                                                                   ('Pienso premium', 'Pienso de salmón y arroz', 'https://img.com/p3', 'SKU003', 29.990, 200, 2),
                                                                                                   ('Collar con cascabel', 'Collar ajustable con cascabel', 'https://img.com/p4', 'SKU004', 5.990, 150, 3);

-- Usuarios
INSERT INTO ecommerce_bd.user (email, username, password, birth_date) VALUES
                                                                          ('ana@email.com', 'ana', 'pass123', '1990-05-12'),
                                                                          ('luis@email.com', 'luis', 'pass456', '1985-11-23'),
                                                                          ('marta@email.com', 'marta', 'pass789', '2000-02-01');

-- CartItems
INSERT INTO ecommerce_bd.cart_item (user_id, product_id, quantity, created_date, modified_date) VALUES
                                                                                                    (1, 1, 2, '2026-01-20 10:00:00', '2026-01-20 10:05:00'),
                                                                                                    (2, 3, 1, '2026-01-21 12:00:00', '2026-01-21 12:10:00'),
                                                                                                    (3, 2, 3, '2026-01-22 09:00:00', '2026-01-22 09:15:00');
