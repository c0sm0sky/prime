CREATE TABLE customers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL CHECK (length(trim(name)) > 0),
    email VARCHAR(255) NOT NULL CHECK (length(trim(email)) > 0),
    status VARCHAR(255) NOT NULL CHECK (status IN ('Nowy', 'Aktywny', 'Nieaktywny'))
);

CREATE UNIQUE INDEX uk_customers_email_lower ON customers (lower(email));

CREATE TABLE customer_orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL CHECK (length(trim(name)) > 0),
    customer_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('Nowe', 'W realizacji', 'Zakończone', 'Anulowane')),
    CONSTRAINT fk_customer_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_orders_customer_id ON customer_orders (customer_id);

CREATE TABLE revenues (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    description VARCHAR(255) NOT NULL CHECK (length(trim(description)) > 0),
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    received_on DATE NOT NULL,
    order_id BIGINT,
    CONSTRAINT fk_revenues_order
        FOREIGN KEY (order_id) REFERENCES customer_orders (id) ON DELETE CASCADE
);

CREATE INDEX idx_revenues_order_id ON revenues (order_id);
