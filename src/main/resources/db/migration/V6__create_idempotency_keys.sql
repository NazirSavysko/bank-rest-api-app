CREATE TABLE IF NOT EXISTS idempotency_keys (
    idempotency_key VARCHAR(150) PRIMARY KEY,
    response_body TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
