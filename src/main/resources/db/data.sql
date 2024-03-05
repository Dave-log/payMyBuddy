INSERT INTO `user` (firstname, lastname, email, password, role, balance)
VALUES
    ('John', 'Doe', 'john@example.com', '$2y$10$.qkbukzzX21D.bqbI.B2R.tvWP90o/Y16QRWVLodw51BHft7ZWbc.', 'USER', 100.00),
    ('Jane', 'Smith', 'jane@example.com', '$2y$10$.qkbukzzX21D.bqbI.B2R.tvWP90o/Y16QRWVLodw51BHft7ZWbc.', 'USER', 150.00),
    ('Alice', 'Johnson', 'alice@example.com', '$2y$10$kp1V7UYDEWn17WSK16UcmOnFd1mPFVF6UkLrOOCGtf24HOYt8p1iC', 'ADMIN', 200.00);

INSERT INTO `user_user` (user_id, buddy_id)
VALUES
    (1, 2),
    (1, 3),
    (2, 3);

INSERT INTO `bank_account` (user_id, account_number, account_type, iban, bic, bank_name)
VALUES
    (1, '123456789', 'Checking', 'GB29 NWBK 6016 1331 9268 19', 'NWBKGB21', 'Bank A'),
    (2, '987654321', 'Savings', 'GB28 NWBK 6016 1331 9268 18', 'NWBKGB20', 'Bank B'),
    (3, '555555555', 'Checking', 'GB27 NWBK 6016 1331 9268 17', 'NWBKGB19', 'Bank C');

INSERT INTO `transaction` (description, type, status, amount, fee, sender_id, recipient_user_id, recipient_bank_id, fee_payer)
VALUES
    ('Payment for services', 'TRANSFER', 'COMPLETED', 500.00, 2.50, 1, 2, NULL, TRUE),
    ('Reimbursement', 'TRANSFER', 'PENDING', 100.00, 0.50, 3, 1, NULL, TRUE),
    ('Deposit', 'DEPOSIT', 'COMPLETED', 200.00, 1.00, 2, NULL, 2, FALSE);