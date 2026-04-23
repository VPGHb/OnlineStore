CREATE SCHEMA online_store;
USE online_store;

CREATE TABLE online_store.users (
    user_id int AUTO_INCREMENT,
    username varchar(50) UNIQUE,
    password varchar(255),
    full_name varchar(100),
    address varchar(255),
    user_type varchar(20), 
    PRIMARY KEY (user_id)
);


CREATE TABLE online_store.products (
    product_id int AUTO_INCREMENT,
    product_name varchar(100),
    price decimal(10,2),
    stock_quantity int,
    description varchar(255),
    PRIMARY KEY (product_id)
);

CREATE TABLE online_store.coupons (
    coupon_id int AUTO_INCREMENT,
    coupon_code varchar(50) UNIQUE,
    discount_percent decimal(5,2),
    is_active varchar(3),
    created_by int,
    PRIMARY KEY (coupon_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE online_store.orders (
    order_id int AUTO_INCREMENT,
    customer_id int,
    order_date timestamp DEFAULT CURRENT_TIMESTAMP,
    total_amount decimal(10,2),
    coupon_code varchar(50),
    discount_amount decimal(10,2),
    status varchar(20),
    PRIMARY KEY (order_id),
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE online_store.order_items (
    order_item_id int AUTO_INCREMENT,
    order_id int,
    product_id int,
    quantity int,
    price_at_time decimal(10,2),
    PRIMARY KEY (order_item_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

INSERT INTO online_store.users (username, password, full_name, address, user_type) 
VALUES 
('admin', 'admin123', 'System Admin', '123 Admin St', 'admin'),
('john.employee', 'emp123', 'John Employee', '456 Work Ave', 'employee'),
('jane.employee', 'emp456', 'Jane Smith', '789 Work Blvd', 'employee'),
('alice.customer', 'cust123', 'Alice Wonder', '123 Home St', 'customer'),
('bob.customer', 'cust456', 'Bob Builder', '456 Home Ave', 'customer');

INSERT INTO online_store.products (product_name, price, stock_quantity, description) 
VALUES 
('Gaming Mouse', 49.99, 50, 'RGB gaming mouse with 6 buttons'),
('Mechanical Keyboard', 89.99, 30, 'Mechanical keyboard with blue switches'),
('27" Monitor', 299.99, 15, '4K UHD monitor'),
('USB-C Headphones', 79.99, 40, 'Noise-cancelling headphones'),
('Laptop Stand', 29.99, 100, 'Adjustable aluminum stand');

INSERT INTO online_store.coupons (coupon_code, discount_percent, is_active, created_by) 
VALUES 
('WELCOME10', 10.00, 'YES', 2),
('SAVE20', 20.00, 'YES', 2),
('FLASH15', 15.00, 'YES', 3);

INSERT INTO online_store.orders (customer_id, total_amount, coupon_code, discount_amount, status) 
VALUES 
(4, 139.98, 'WELCOME10', 13.99, 'delivered'),
(5, 89.99, NULL, 0, 'processing'),
(4, 329.98, 'SAVE20', 65.99, 'shipped');

INSERT INTO online_store.order_items (order_id, product_id, quantity, price_at_time) 
VALUES 
(1, 1, 2, 49.99),
(1, 4, 1, 79.99),
(2, 2, 1, 89.99),
(3, 3, 1, 299.99),
(3, 5, 1, 29.99);

SELECT * FROM online_store.users;

SELECT * FROM online_store.products;

SELECT orders.order_id, users.full_name, orders.order_date, orders.total_amount, orders.status 
FROM online_store.orders
JOIN online_store.users
ON orders.customer_id = users.user_id;

SELECT order_items.order_id, products.product_name, order_items.quantity, order_items.price_at_time 
FROM online_store.order_items
JOIN online_store.products
ON order_items.product_id = products.product_id;

SELECT user_type, COUNT(*) 
FROM online_store.users
GROUP BY user_type;

SELECT * FROM online_store.coupons
WHERE is_active = 'YES';

SELECT users.full_name, orders.order_id, orders.total_amount
FROM online_store.orders
JOIN online_store.users
ON orders.customer_id = users.user_id
ORDER BY users.full_name ASC;

USE online_store;
ALTER TABLE users ADD COLUMN is_first_login BOOLEAN DEFAULT FALSE;

UPDATE users SET is_first_login = TRUE WHERE user_type = 'employee';

DESCRIBE users;

SELECT user_id, username, user_type, is_first_login FROM users;

USE online_store;
SELECT user_id, username, user_type, is_first_login FROM users;


