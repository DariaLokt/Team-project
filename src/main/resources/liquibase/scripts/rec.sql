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