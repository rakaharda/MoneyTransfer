DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS transfers;
CREATE TABLE IF NOT EXISTS messages (
  id                     VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  text                   VARCHAR      NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR NOT NULL,
    userid VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    balance FLOAT,
    token VARCHAR(60) DEFAULT RANDOM_UUID()
);

--ALTER TABLE users ADD COLUMN token VARCHAR(60) DEFAULT HASH('SHA256', FROM users );

CREATE TABLE IF NOT EXISTS transfers (
    transferid VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    transferdate DATETIME DEFAULT LOCALTIME(),
    senderid VARCHAR(60) NOT NULL,
    recipientid VARCHAR(60) NOT NULL,
    amount FLOAT NOT NULL
);



INSERT INTO transfers (senderid, recipientid, amount) VALUES('sender', 'recipient', 3);