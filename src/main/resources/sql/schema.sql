DROP TABLE IF EXISTS transfers;

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR NOT NULL,
    userid VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    balance FLOAT,
    token VARCHAR(60) DEFAULT RANDOM_UUID()
);

CREATE TABLE IF NOT EXISTS transfers (
    transferid VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    transferdate DATETIME DEFAULT LOCALTIME(),
    senderid VARCHAR(60) NOT NULL,
    recipientid VARCHAR(60) NOT NULL,
    amount FLOAT NOT NULL
);



INSERT INTO transfers (senderid, recipientid, amount) VALUES('sender', 'recipient', 3);