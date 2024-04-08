INSERT INTO `user` (firstname, lastname, email, password, role, balance) VALUES
    ('Dave', 'Log', 'davelog@example.com', '$2a$10$awr5iBSGiyhcm7Z6jUI9wuCr8BlgBIJcMsivGMsV2R0g0O3P2SdAy', 'USER', 200.00), -- mdp: 0000
    ('Bruce', 'Wayne', 'batman@example.com', '$2a$10$olKRgndzhku3uLDJbzq2L.OOocAP7r03Gk9Lp0wy/NMglxuUhgWH.', 'USER', 50.00), -- mdp: chauvesouris
    ('Bruce', 'Banner', 'hulk@example.com', '$2a$10$2oe.Hf8E8ElSvUSo/jzBCer0RnZN5V0Qi8JIfMOinED2uaMWo0FJ2', 'USER', 0.00), -- mdp: vert
    ('John', 'Doe', 'johndoe@example.com', '$2a$10$z0eMy9pbR5/ZseHWyiiueeQdluuAFlKpK3W7m73hA.IOv4LOmB10S', 'USER', 0.00); -- mdp: unknown

INSERT INTO `bank_account` (account_type, account_number, bank_name, iban, bic, user_id) VALUES
    ('Checking', '123456789', 'Fiction Bank', 'FR7612345678901234567890123', 'FICBANK12345', 1),
    ('Saving', '987654321', 'Fiction Bank', 'FR7698765432109876543210987', 'FICBANK98765', 1);

INSERT INTO `user_user` (user_id, buddy_id) VALUES
    (1, 2),
    (1, 3),
    (2, 1);