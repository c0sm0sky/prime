ALTER TABLE customer_orders ADD COLUMN customer_id BIGINT;

UPDATE customer_orders AS orders
SET customer_id = customers.id
FROM customers
WHERE lower(customers.name) = lower(orders.customer)
   OR lower(customers.email) = lower(orders.customer);

INSERT INTO customers (name, email, status)
SELECT DISTINCT orders.customer,
       'legacy-order-' || orders.id || '@migration.invalid',
       'Zaimportowany'
FROM customer_orders AS orders
WHERE orders.customer_id IS NULL;

UPDATE customer_orders AS orders
SET customer_id = customers.id
FROM customers
WHERE customers.email = 'legacy-order-' || orders.id || '@migration.invalid'
  AND orders.customer_id IS NULL;

ALTER TABLE customer_orders ALTER COLUMN customer_id SET NOT NULL;
ALTER TABLE customer_orders
    ADD CONSTRAINT fk_customer_orders_customer
    FOREIGN KEY (customer_id) REFERENCES customers (id);
CREATE INDEX idx_customer_orders_customer_id ON customer_orders (customer_id);
ALTER TABLE customer_orders DROP COLUMN customer;
