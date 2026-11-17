UPDATE customers SET email = lower(btrim(email));

CREATE UNIQUE INDEX uk_customers_email_lower ON customers (lower(email));

ALTER TABLE customers
    ADD CONSTRAINT ck_customers_name_not_blank CHECK (length(btrim(name)) > 0),
    ADD CONSTRAINT ck_customers_email_not_blank CHECK (length(btrim(email)) > 0),
    ADD CONSTRAINT ck_customers_status_not_blank CHECK (length(btrim(status)) > 0);

ALTER TABLE customer_orders
    ADD CONSTRAINT ck_customer_orders_name_not_blank CHECK (length(btrim(name)) > 0),
    ADD CONSTRAINT ck_customer_orders_status_not_blank CHECK (length(btrim(status)) > 0);

ALTER TABLE revenues
    ADD CONSTRAINT ck_revenues_description_not_blank CHECK (length(btrim(description)) > 0),
    ADD CONSTRAINT ck_revenues_amount_positive CHECK (amount > 0);
