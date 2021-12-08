DROP TABLE IF EXISTS bank_transactions;
DROP TABLE IF EXISTS bank_coordinates;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS wallets_links;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS users;

CREATE TABLE users(
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(16) NOT NULL,
    password BINARY(60) NOT NULL,
    PRIMARY KEY(id)
)ENGINE = InnoDB;

--Password cash@man.com == Meg@rich1337
--Password bank@forbucks.com == PognonP@rt0ut
--Password pas@pauvre.com == Simp@PortM0nnaie
INSERT INTO users(email, name, password) VALUES
('cash@man.com', 'pleinauxas', '$2y$10$.qSFMa4Fq8SioKPMx5R1RO.7bnh5sH3EXZJ0TQ6RhzhJ/MXkkG/0S'),
('bank@forbucks.com', 'krezus', '$2y$10$UvOZZexyo.1IDG.e6WToAu.GTyb8pq7yoA9Q0uQKNbeOB3Zrmc5dO'),
('pas@pauvre.com', 'mydas', '$2y$10$3jryP9DU5XHO5p1zJmCp0uFcWNRzYf3FHyIc8elESFyuBlY6xX2Hy');

CREATE TABLE wallets(
    id BIGINT NOT NULL AUTO_INCREMENT,
    currency VARCHAR(3) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    owner_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_wallet_owner
    FOREIGN KEY(owner_id) REFERENCES users(id)
)ENGINE = InnoDB;

INSERT INTO wallets (currency, amount, owner_id) VALUES
('EUR', 1500.00, 1),
('EUR', 1000.00, 2),
('EUR', 500.00, 3);

CREATE TABLE wallets_links(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(16) NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_sender_wallet
    FOREIGN KEY(sender_id) REFERENCES wallets(id),
    CONSTRAINT fk_receiver_id
    FOREIGN KEY(receiver_id) REFERENCES wallets(id)
)ENGINE = InnoDB;

INSERT INTO wallets_links(name, sender_id, receiver_id) VALUES
('Crésus', 1, 2),
('Le grec', 1, 3),
('Pleindethune', 2, 1),
('Le lydien', 3, 2);

CREATE TABLE transactions(
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DECIMAL(19,2) NOT NULL,
    date DATETIME NOT NULL,
    description VARCHAR(255) NOT NULL,
    fee DECIMAL(19,2) NOT NULL,
    link_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_wallet_link
    FOREIGN KEY(link_id) REFERENCES wallets_links(id)
)ENGINE = InnoDB;

CREATE TABLE bank_coordinates(
    id BIGINT NOT NULL AUTO_INCREMENT,
    account_number VARCHAR(34) NOT NULL UNIQUE,
    PRIMARY KEY(id)
)ENGINE = InnoDB;

CREATE TABLE bank_transactions(
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DECIMAL(19,2) NOT NULL,
    date DATETIME NOT NULL,
    type VARCHAR(3) NOT NULL,
    bank_coordinate_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_bank_coordinate
    FOREIGN KEY(bank_coordinate_id) REFERENCES bank_coordinates(id),
    CONSTRAINT fk_wallet
    FOREIGN KEY(wallet_id) REFERENCES wallets(id)
)ENGINE = InnoDB;
