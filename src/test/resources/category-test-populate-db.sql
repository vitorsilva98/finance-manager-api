INSERT INTO categories (id, name) VALUES
(
    'c225d540-fa04-44ec-9841-73802752d241',
    'Teste'
);

INSERT INTO entries (id, payment_method, type, category_id, user_id, amount, date_time, reversed, reversal_date_time, description) VALUES
(
    gen_random_uuid(),
    'PIX',
    'PURCHASE',
    (SELECT id FROM categories WHERE name = 'Teste'),
    (SELECT id FROM users WHERE email = 'vitor.augsilva98@gmail.com'),
    100,
    now(),
    false,
    null,
    null
);
