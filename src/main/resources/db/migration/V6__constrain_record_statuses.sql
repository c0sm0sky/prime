ALTER TABLE customers
    ADD CONSTRAINT ck_customers_status_allowed
    CHECK (status IN ('Nowy', 'Aktywny', 'Nieaktywny'));

ALTER TABLE customer_orders
    ADD CONSTRAINT ck_customer_orders_status_allowed
    CHECK (status IN ('Nowe', 'W realizacji', 'Zakończone', 'Anulowane'));
