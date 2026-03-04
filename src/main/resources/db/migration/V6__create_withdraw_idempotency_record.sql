CREATE TABLE withdraw_idempotency_record (
    id SERIAL PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    response_body TEXT,
    expires_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX ux_withdraw_idempotency_key_endpoint
    ON withdraw_idempotency_record (idempotency_key, endpoint);
