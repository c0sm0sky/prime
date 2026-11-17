INSERT INTO customers (name, email, status)
VALUES
    ('Anna Nowak', 'anna.nowak@example.com', 'Aktywny'),
    ('Michał Wiśniewski', 'michal.wisniewski@example.com', 'Nowy'),
    ('Zofia Kaczmarek', 'zofia.kaczmarek@example.com', 'Aktywny');

INSERT INTO customer_orders (name, customer_id, status)
SELECT 'Pakiet wdrożeniowy — Anna Nowak', id, 'W realizacji'
FROM customers WHERE email = 'anna.nowak@example.com';

INSERT INTO customer_orders (name, customer_id, status)
SELECT 'Obsługa serwisowa — Michał Wiśniewski', id, 'Nowe'
FROM customers WHERE email = 'michal.wisniewski@example.com';

INSERT INTO customer_orders (name, customer_id, status)
SELECT 'Audyt systemu — Zofia Kaczmarek', id, 'Zakończone'
FROM customers WHERE email = 'zofia.kaczmarek@example.com';

INSERT INTO revenues (description, amount, received_on, order_id)
SELECT name, 4850.00, date('now', '-12 days'), id
FROM customer_orders WHERE name = 'Pakiet wdrożeniowy — Anna Nowak';

INSERT INTO revenues (description, amount, received_on, order_id)
SELECT name, 1200.00, date('now', '-5 days'), id
FROM customer_orders WHERE name = 'Obsługa serwisowa — Michał Wiśniewski';

INSERT INTO revenues (description, amount, received_on, order_id)
SELECT name, 3200.00, date('now', '-2 days'), id
FROM customer_orders WHERE name = 'Audyt systemu — Zofia Kaczmarek';
