CREATE TABLE entries (
    id UUID NOT NULL UNIQUE PRIMARY KEY,
    payment_method VARCHAR(20) NOT NULL,
    type VARCHAR(10) NOT NULL,
    category_id UUID NOT NULL,
    user_id UUID NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    date_time TIMESTAMP(6) NOT NULL,
    reversed BOOLEAN NOT NULL,
    reversal_date_time TIMESTAMP(6),
    description VARCHAR(255),

    CONSTRAINT fk_entries_user_id FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_entries_category_id FOREIGN KEY(category_id) REFERENCES categories(id)
);
