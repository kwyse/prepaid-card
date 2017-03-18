-- liquibase formatted sql

-- changeset krishan:1
CREATE TABLE cards (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    balance NUMERIC(14, 2) NOT NULL
);

CREATE TABLE merchants (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    balance NUMERIC(14, 2) NOT NULL
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    card_id INTEGER REFERENCES cards(id),
    merchant_id INTEGER REFERENCES merchants(id),
    remaining_amount NUMERIC(14, 2) NOT NULL,
    captured_amount NUMERIC(14, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- rollback DROP TABLE cards, merchants, transactions;

-- changeset krishan:2
INSERT INTO merchants (name, balance) VALUES ('Magic Coffee', 10000);

-- rollback DELETE FROM merchants WHERE name = 'Magic Coffee';
