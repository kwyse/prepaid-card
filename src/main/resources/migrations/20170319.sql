-- liquibase formatted sql

-- changeset krishan:3
ALTER TABLE cards ADD CONSTRAINT positive_balance CHECK (balance >= 0);
ALTER TABLE merchants ADD CONSTRAINT positive_balance CHECK (balance >= 0);
ALTER TABLE transactions ADD CONSTRAINT positive_remaining_amount CHECK (remaining_amount >= 0);
ALTER TABLE transactions ADD CONSTRAINT positive_captured_amount CHECK (captured_amount >= 0);

-- rollback ALTER TABLE cards DROP CONSTRAINT positive_balance;
-- rollback ALTER TABLE merchants DROP CONSTRAINT positive_balance;
-- rollback ALTER TABLE transactions DROP CONSTRAINT positive_remaining_amount, DROP CONSTRAINT positive_captured_amount;

-- changeset krishan:4
CREATE VIEW card_view AS
    SELECT
        C.id,
        C.name,
        C.balance - COALESCE(SUM(T.remaining_amount), 0) AS balance,
        COALESCE(SUM(T.remaining_amount), 0) AS blocked
    FROM cards C
    LEFT JOIN transactions T ON C.id = T.card_id
    GROUP BY C.id
    ORDER BY C.id ASC;

-- rollback DROP VIEW card_view;