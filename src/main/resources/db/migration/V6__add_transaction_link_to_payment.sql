ALTER TABLE payment
    ADD COLUMN IF NOT EXISTS transaction_id INTEGER;

ALTER TABLE payment
    ADD CONSTRAINT fk_payment_transaction
        FOREIGN KEY (transaction_id) REFERENCES transaction (transaction_id);
