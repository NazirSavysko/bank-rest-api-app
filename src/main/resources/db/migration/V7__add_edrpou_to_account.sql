ALTER TABLE account
    ADD COLUMN account_type VARCHAR(20) NOT NULL DEFAULT 'CURRENT',
    ADD COLUMN edrpou VARCHAR(10);

ALTER TABLE account
    ADD CONSTRAINT uk_account_edrpou UNIQUE (edrpou);
