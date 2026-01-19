-- 1. Таблица пользователей (AuthUser)
CREATE TABLE auth_user (
                           user_id SERIAL PRIMARY KEY,
                           email VARCHAR(255) UNIQUE NOT NULL,
                           password_hash VARCHAR(255) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Таблица ролей (Role)
CREATE TABLE role (
                      id SERIAL PRIMARY KEY,
                      role_name VARCHAR(50) UNIQUE NOT NULL -- 'ROLE_ADMIN', 'ROLE_USER'
);

-- 3. Связующая таблица пользователей и ролей (ManyToMany)
CREATE TABLE customer_roles (
                                user_id INTEGER NOT NULL,
                                role_id INTEGER NOT NULL,
                                PRIMARY KEY (user_id, role_id),
                                CONSTRAINT fk_customer_roles_user FOREIGN KEY (user_id) REFERENCES auth_user (user_id) ON DELETE CASCADE,
                                CONSTRAINT fk_customer_roles_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);

-- 4. Таблица клиентов (Customer)
CREATE TABLE customer (
                          customer_id SERIAL PRIMARY KEY,
                          first_name VARCHAR(100),
                          last_name VARCHAR(100),
                          phone VARCHAR(20),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          user_id INTEGER UNIQUE, -- OneToOne с AuthUser
                          CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES auth_user (user_id) ON DELETE CASCADE
);

-- 5. Таблица счетов (Account)
CREATE TABLE account (
                         account_id SERIAL PRIMARY KEY,
                         account_number VARCHAR(50) UNIQUE NOT NULL,
                         balance DECIMAL(19, 2) DEFAULT 0.00,
                         currency_code VARCHAR(10) NOT NULL, -- 'UAH', 'USD', 'EUR'
                         status VARCHAR(20) NOT NULL,        -- 'ACTIVE', 'BLOCKED'
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         customer_id INTEGER,
                         CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE CASCADE
);

-- 6. Таблица карт (Card)
CREATE TABLE card (
                      card_id SERIAL PRIMARY KEY,
                      card_number VARCHAR(20) UNIQUE NOT NULL,
                      expiry_date TIMESTAMP NOT NULL,
                      cvv VARCHAR(4) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      account_id INTEGER UNIQUE, -- OneToOne с Account
                      CONSTRAINT fk_card_account FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE CASCADE
);

-- 7. Таблица платежей (Payment)
CREATE TABLE payment (
                         payment_id SERIAL PRIMARY KEY,
                         amount DECIMAL(19, 2) NOT NULL,
                         currency_code VARCHAR(10) NOT NULL,
                         beneficiary_name VARCHAR(255),
                         beneficiary_acc VARCHAR(255),
                         purpose VARCHAR(255),
                         payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         account_id INTEGER,
                         CONSTRAINT fk_payment_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);

-- 8. Таблица транзакций (Transaction)
CREATE TABLE transaction (
                             transaction_id SERIAL PRIMARY KEY,
                             amount DECIMAL(19, 2) NOT NULL,
                             currency_code VARCHAR(10) NOT NULL,
                             description VARCHAR(255),
                             transaction_type VARCHAR(20) NOT NULL, -- 'TRANSFER', 'PAYMENT'
                             status VARCHAR(20) NOT NULL,           -- 'COMPLETED', 'FAILED'
                             transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             from_account_id INTEGER,
                             to_account_id INTEGER,
                             CONSTRAINT fk_transaction_from FOREIGN KEY (from_account_id) REFERENCES account (account_id),
                             CONSTRAINT fk_transaction_to FOREIGN KEY (to_account_id) REFERENCES account (account_id)
);

-- 9. Таблица кодов верификации Email
CREATE TABLE email_verification_codes (
                                          id SERIAL PRIMARY KEY,
                                          email VARCHAR(255) NOT NULL,
                                          code VARCHAR(50) NOT NULL,
                                          is_verified BOOLEAN DEFAULT FALSE,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- НАЧАЛЬНЫЕ ДАННЫЕ (ЧТОБЫ СИСТЕМА НЕ БЫЛА ПУСТОЙ)
-- =============================================

INSERT INTO role (role_name) VALUES ('ROLE_ADMIN');
INSERT INTO role (role_name) VALUES ('ROLE_USER');