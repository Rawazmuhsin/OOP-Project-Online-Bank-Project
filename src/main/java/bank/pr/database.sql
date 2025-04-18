CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type ENUM('Checking', 'Savings') NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type ENUM('Deposit', 'Withdrawal', 'Transfer') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);


CREATE TABLE transfers (
    transfer_id INT AUTO_INCREMENT PRIMARY KEY,
    from_account_id INT NOT NULL,
    to_account_id INT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transfer_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (to_account_id) REFERENCES accounts(account_id)
);