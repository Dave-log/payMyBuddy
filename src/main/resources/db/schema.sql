CREATE DATABASE IF NOT EXISTS pay_my_buddy_db;
USE pay_my_buddy_db;

CREATE TABLE IF NOT EXISTS `user` (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    birthdate DATE,
    address VARCHAR(255),
    phone VARCHAR(50),
    current_balance DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS `user_account` (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `user_user` (
    user_id INT NOT NULL,
    buddy_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (buddy_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `bank_account` (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    accountNumber VARCHAR(50) NOT NULL,
    accountType VARCHAR(50),
    iban VARCHAR(50) NOT NULL ,
    bic VARCHAR(50) NOT NULL ,
    bankName VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `transaction` (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    type VARCHAR(50),
    status VARCHAR(50),
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10,2),
    fee DECIMAL(10,2),
    sender_id INT NOT NULL,
    recipient_id INT NOT NULL,
    fee_paid_by_sender BOOLEAN DEFAULT TRUE,
    recipient_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES user (id),
    FOREIGN KEY (recipient_id) REFERENCES user (id)
);