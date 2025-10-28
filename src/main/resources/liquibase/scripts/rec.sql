-- liquibase formatted sql

-- changeset dlok:1
CREATE TABLE product (
    id UUID PRIMARY KEY,
    product_name TEXT,
    product_id UUID,
    product_text TEXT
);

CREATE TABLE rule (
    id UUID PRIMARY KEY,
    query TEXT,
    arguments TEXT,
    negate BOOL,
    product_id UUID REFERENCES product (id)
);

-- changeset dlok:2
ALTER TABLE rule ALTER COLUMN arguments TYPE TEXT[] USING arguments::text[];

-- changeset dlok:3
CREATE TABLE telegram_bot_users (
    chat_id BIGINT PRIMARY KEY,
    given_instructions BOOL,
    user_name VARCHAR(255)
);