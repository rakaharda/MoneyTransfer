CREATE TABLE IF NOT EXISTS users (
    username VARCHAR NOT NULL,
    userid uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    balance FLOAT DEFAULT 0,
    token uuid DEFAULT gen_random_uuid()
);

CREATE TABLE IF NOT EXISTS transfers (
    transferid uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    transferdate timestamp with time zone DEFAULT now(),
    senderid uuid NOT NULL,
    recipientid uuid NOT NULL,
    amount FLOAT NOT NULL,
    FOREIGN KEY (senderid) REFERENCES users(userid),
    FOREIGN KEY (recipientid) REFERENCES users(userid)
);

ALTER TABLE transfers (
    FOREIGN KEY (senderid) REFERENCES users(userid),
    FOREIGN KEY (recipientid) REFERENCES users(userid)
);
