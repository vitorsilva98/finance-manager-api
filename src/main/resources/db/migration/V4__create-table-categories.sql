CREATE TABLE categories (
    id UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
