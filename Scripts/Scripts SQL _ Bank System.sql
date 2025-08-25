
CREATE DATABASE BankSystem;
USE BankSystem;

-- Table for Clients
CREATE TABLE Client (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL
);

-- Table for Accounts
CREATE TABLE Account (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    account_type ENUM('SAVINGS', 'CURRENT') NOT NULL,
    client_id INT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES Client(client_id)
);

-- Insertar clientes
INSERT INTO Client (first_name, last_name, dni, email)
VALUES
('Anais', 'Bustamante', '71205648', 'anaisbustamantetorres@mail.com'),
('Yesenia ', 'Peche', '52487963', 'Lizet.peche@gmail.com'),
('Luz ', 'Mongollón', '52487578', 'oriamna@gmail.com'),
('Alexis', 'Pescoran', '87654321', 'pfernandez.alexis@gmail.com');

-- Insertar cuentas
INSERT INTO Account (account_number, balance, account_type, client_id)
VALUES
('SAVING001', 1000.00, 'SAVINGS', 1),
('CURRENT001', 500.00, 'CURRENT', 1),
('SAVING002', 2000.00, 'SAVINGS', 2),
('CURRENT002', 500.00, 'CURRENT', 3);


-- Select all clients
SELECT * FROM Client;

-- Select all accounts
SELECT * FROM Account;

-- Select accounts of a specific client by DNI
SELECT c.first_name, a.*
FROM Account a
JOIN Client c ON a.client_id = c.client_id
WHERE c.dni = '71205648';

-- Check account balance by account number
SELECT account_number, balance
FROM Account
WHERE account_number = 'SAVING001';

-- Deposit money into an account
UPDATE Account
SET balance = balance + 300
WHERE account_number = 'SAVING001';

UPDATE Account
SET balance = balance + 700
WHERE account_number = 'SAVING002';

SELECT account_number, balance
FROM Account
WHERE account_number IN ('SAVING001', 'SAVING002');


-- Withdraw money
UPDATE Account
SET balance = balance - 200
WHERE account_number = 'SAVING001';
SELECT account_number, balance
FROM Account
WHERE account_number IN ('SAVING001');

-- Get clients with more than one account
SELECT c.first_name, c.last_name, COUNT(a.account_id) AS total_accounts
FROM Client c
JOIN Account a ON c.client_id = a.client_id
GROUP BY c.client_id
HAVING COUNT(a.account_id) > 1;

-- Get all savings accounts with balance greater than 500
SELECT account_number, balance
FROM Account
WHERE account_type = 'SAVINGS'
  AND balance > 500;

-- Get total balance per client
SELECT c.first_name, c.last_name, SUM(a.balance) AS total_balance
FROM Client c
JOIN Account a ON c.client_id = a.client_id
GROUP BY c.client_id;

