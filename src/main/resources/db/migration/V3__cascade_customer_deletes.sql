ALTER TABLE customer_orders
    DROP CONSTRAINT fk_customer_orders_customer;

ALTER TABLE customer_orders
    ADD CONSTRAINT fk_customer_orders_customer
    FOREIGN KEY (customer_id)
    REFERENCES customers (id)
    ON DELETE CASCADE;

DO $$
DECLARE
    foreign_key_name TEXT;
BEGIN
    FOR foreign_key_name IN
        SELECT conname
        FROM pg_constraint
        WHERE conrelid = 'revenues'::regclass
          AND confrelid = 'customer_orders'::regclass
          AND contype = 'f'
    LOOP
        EXECUTE format('ALTER TABLE revenues DROP CONSTRAINT %I', foreign_key_name);
    END LOOP;
END
$$;

ALTER TABLE revenues
    ADD CONSTRAINT fk_revenues_order
    FOREIGN KEY (order_id)
    REFERENCES customer_orders (id)
    ON DELETE CASCADE;

CREATE INDEX idx_revenues_order_id ON revenues (order_id);
