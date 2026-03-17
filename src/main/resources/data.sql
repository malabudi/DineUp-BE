-- Menu Groups
INSERT INTO menu_groups (name) VALUES ('Burgers') ON CONFLICT DO NOTHING;
INSERT INTO menu_groups (name) VALUES ('Drinks') ON CONFLICT DO NOTHING;

-- Menu Items (assuming menu_group_id 1 = Burgers, 2 = Drinks)
INSERT INTO menu_items (name, description, price, image_url, menu_group_id) VALUES
                                                                                ('Classic Burger', 'Beef patty with lettuce and tomato', 9.99, '', 1),
                                                                                ('Cheese Burger', 'Classic with extra cheese', 10.99, '', 1),
                                                                                ('Veggie Burger', 'Plant based patty', 8.99, '', 1),
                                                                                ('Lemonade', 'Fresh squeezed', 3.99, '', 2),
                                                                                ('Iced Tea', 'Sweet or unsweet', 2.99, '', 2)
ON CONFLICT DO NOTHING;

-- Users (adjust columns to match your User model)
INSERT INTO users (first_name, last_name, email, password, role) VALUES
    ('john', 'doe', 'customer@test.com', 'hashed_password', 'CUSTOMER')
ON CONFLICT DO NOTHING;

-- Orders
INSERT INTO orders (customer_id, order_date, order_status, total) VALUES
                                                                      (1, NOW(), 'COMPLETE', 21.98),
                                                                      (1, NOW(), 'COMPLETE', 13.98),
                                                                      (1, NOW(), 'COMPLETE', 9.99);

-- Line Items (make Classic Burger and Cheese Burger the top sellers)
INSERT INTO line_items (order_id, menu_item_id, quantity, price) VALUES
                                                                     (1, 1, 3, 9.99),   -- Classic Burger x3
                                                                     (1, 2, 2, 10.99),  -- Cheese Burger x2
                                                                     (2, 1, 2, 9.99),   -- Classic Burger x2
                                                                     (2, 4, 1, 3.99),   -- Lemonade x1
                                                                     (3, 3, 1, 8.99),   -- Veggie Burger x1
                                                                     (3, 2, 1, 10.99);   -- Cheese Burger x1