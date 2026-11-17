WITH demo_customers (name, email, status) AS (
    VALUES
        ('Anna Nowak', 'anna.nowak@example.com', 'Aktywny'),
        ('Michał Wiśniewski', 'michal.wisniewski@example.com', 'Nowy'),
        ('Zofia Kaczmarek', 'zofia.kaczmarek@example.com', 'Aktywny')
)
INSERT INTO customers (name, email, status)
SELECT demo.name, demo.email, demo.status
FROM demo_customers AS demo
WHERE NOT EXISTS (
    SELECT 1 FROM customers WHERE email = demo.email
);

WITH demo_orders (name, email, status) AS (
    VALUES
        ('Pakiet wdrożeniowy — Anna Nowak', 'anna.nowak@example.com', 'W realizacji'),
        ('Obsługa serwisowa — Michał Wiśniewski', 'michal.wisniewski@example.com', 'Nowe'),
        ('Audyt systemu — Zofia Kaczmarek', 'zofia.kaczmarek@example.com', 'Zakończone')
)
INSERT INTO customer_orders (name, customer_id, status)
SELECT demo.name, customer.id, demo.status
FROM demo_orders AS demo
JOIN customers AS customer ON customer.email = demo.email
WHERE NOT EXISTS (
    SELECT 1
    FROM customer_orders AS existing
    WHERE existing.name = demo.name
      AND existing.customer_id = customer.id
);

WITH demo_revenues (order_name, amount, received_on) AS (
    VALUES
        ('Pakiet wdrożeniowy — Anna Nowak', 4850.00::NUMERIC(12, 2), CURRENT_DATE - 12),
        ('Obsługa serwisowa — Michał Wiśniewski', 1200.00::NUMERIC(12, 2), CURRENT_DATE - 5),
        ('Audyt systemu — Zofia Kaczmarek', 3200.00::NUMERIC(12, 2), CURRENT_DATE - 2)
)
INSERT INTO revenues (description, amount, received_on, order_id)
SELECT customer_order.name, demo.amount, demo.received_on, customer_order.id
FROM demo_revenues AS demo
JOIN customer_orders AS customer_order ON customer_order.name = demo.order_name
WHERE NOT EXISTS (
    SELECT 1
    FROM revenues AS existing
    WHERE existing.order_id = customer_order.id
      AND existing.description = customer_order.name
      AND existing.amount = demo.amount
);
