DROP TABLE IF EXISTS transfers;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR NOT NULL,
    userid VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    balance FLOAT DEFAULT 0,
    token VARCHAR(60) DEFAULT RANDOM_UUID()
);

CREATE TABLE IF NOT EXISTS transfers (
    transferid VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    transferdate DATETIME DEFAULT LOCALTIME(),
    senderid VARCHAR(60) NOT NULL,
    recipientid VARCHAR(60) NOT NULL,
    amount FLOAT NOT NULL
);


INSERT INTO users (username, balance) VALUES('John Smith', 33.5);
INSERT INTO users (username, balance) VALUES('Ivan Ivanov', 30);