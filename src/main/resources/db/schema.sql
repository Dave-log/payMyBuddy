CREATE DATABASE IF NOT EXISTS pay_my_buddy_db;
USE pay_my_buddy_db;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    username VARCHAR(50),
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    balance DECIMAL(10,2),
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
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `transaction` (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    type VARCHAR(50),
    status VARCHAR(50),
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10,2),
    fee DECIMAL(10,2),
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    fee_payer BOOLEAN DEFAULT TRUE,
    recipient_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES user (id),
    FOREIGN KEY (recipient_id) REFERENCES user (id)
);
