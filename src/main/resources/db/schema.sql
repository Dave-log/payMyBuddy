CREATE DATABASE IF NOT EXISTS pay_my_buddy_db;
USE pay_my_buddy_db;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    balance DECIMAL(10,2) DEFAULT 0.00,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS `user_user` (
    user_id BIGINT NOT NULL,
    buddy_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (buddy_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `bank_account` (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_type VARCHAR(50),
    iban VARCHAR(50) NOT NULL,
    bic VARCHAR(50) NOT NULL,
    bank_name VARCHAR(100),
    UNIQUE (iban),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `transaction` (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10,2),
    fee DECIMAL(10,2),
    sender_id BIGINT NOT NULL,
    fee_payer BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (sender_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `bank_transaction` (
    transaction_id BIGINT NOT NULL,
    recipient_bank_id BIGINT NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES `transaction` (id),
    FOREIGN KEY (recipient_bank_id) REFERENCES `bank_account` (id)
);

CREATE TABLE IF NOT EXISTS `buddy_transaction` (
    transaction_id BIGINT NOT NULL,
    recipient_user_id BIGINT NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES `transaction` (id),
    FOREIGN KEY (recipient_user_id) REFERENCES `user` (id)
);