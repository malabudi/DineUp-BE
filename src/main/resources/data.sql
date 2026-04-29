-- Menu Groups
INSERT INTO menu_groups (name) VALUES ('Starters');
INSERT INTO menu_groups (name) VALUES ('Mains');
INSERT INTO menu_groups (name) VALUES ('Sides');
INSERT INTO menu_groups (name) VALUES ('Desserts');
INSERT INTO menu_groups (name) VALUES ('Drinks');

-- Starters (menu_group_id = 1)
INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Bruschetta', 'Toasted bread topped with fresh tomatoes, garlic, and basil drizzled with olive oil', 8.99, NULL, 1);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Soup of the Day', 'Ask your server for today''s freshly made soup served with warm bread', 7.99, NULL, 1);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Calamari', 'Lightly battered fried squid served with marinara dipping sauce', 11.99, NULL, 1);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Chicken Wings', 'Crispy wings tossed in your choice of buffalo, BBQ, or honey garlic sauce', 13.99, NULL, 1);

-- Mains (menu_group_id = 2)
INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Grilled Salmon', 'Atlantic salmon fillet grilled to perfection served with seasonal vegetables and lemon butter sauce', 24.99, NULL, 2);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Ribeye Steak', '12oz prime cut ribeye cooked to your liking served with mashed potatoes and grilled asparagus', 38.99, NULL, 2);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Chicken Parmesan', 'Breaded chicken breast topped with marinara sauce and melted mozzarella served over spaghetti', 19.99, NULL, 2);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Mushroom Risotto', 'Creamy arborio rice with wild mushrooms, parmesan, and fresh thyme', 17.99, NULL, 2);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Classic Burger', 'Half pound beef patty with lettuce, tomato, onion, and pickles on a brioche bun', 16.99, NULL, 2);

-- Sides (menu_group_id = 3)
INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Truffle Fries', 'Crispy fries tossed in truffle oil and parmesan', 7.99, NULL, 3);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Side Salad', 'Mixed greens with cherry tomatoes, cucumber, and your choice of dressing', 5.99, NULL, 3);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Garlic Bread', 'Toasted sourdough with garlic butter and fresh parsley', 4.99, NULL, 3);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Onion Rings', 'Beer battered onion rings served with chipotle dipping sauce', 6.99, NULL, 3);

-- Desserts (menu_group_id = 4)
INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Chocolate Lava Cake', 'Warm chocolate cake with a molten center served with vanilla ice cream', 9.99, NULL, 4);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Cheesecake', 'New York style cheesecake with a graham cracker crust and berry compote', 8.99, NULL, 4);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Tiramisu', 'Classic Italian dessert with espresso soaked ladyfingers and mascarpone cream', 8.99, NULL, 4);

-- Drinks (menu_group_id = 5)
INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Fresh Lemonade', 'House made lemonade with fresh squeezed lemons and mint', 4.99, NULL, 5);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Iced Tea', 'Freshly brewed black tea served over ice with lemon', 3.99, NULL, 5);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Sparkling Water', 'San Pellegrino sparkling mineral water', 3.49, NULL, 5);

INSERT INTO menu_items (name, description, price, image_url, menu_group_id)
VALUES ('Coffee', 'Freshly brewed house blend coffee', 3.99, NULL, 5);