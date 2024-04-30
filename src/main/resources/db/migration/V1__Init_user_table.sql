CREATE TABLE users
(
    id           UUID                NOT NULL PRIMARY KEY,
    email        VARCHAR(255) UNIQUE NOT NULL,
    first_name   VARCHAR(255)        NOT NULL,
    last_name    VARCHAR(255)        NOT NULL,
    birth_date   DATE                NOT NULL,
    address      TEXT,
    phone_number VARCHAR(50)
);
