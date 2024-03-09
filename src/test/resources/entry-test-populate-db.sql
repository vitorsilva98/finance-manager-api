INSERT INTO users (id, email, name, password, disabled) VALUES
(
    gen_random_uuid(),
    'fulano.user@gmail.com',
    'Fulano User',
    '$2a$10$EH3xj0RMlXUWO0Jez4IMhewmLY.pwEdiAL7HVtutmEL2ukmu0TzuS',
    false
),
(
    gen_random_uuid(),
    'fulano.admin@gmail.com',
    'Fulano Admin',
    '$2a$10$EH3xj0RMlXUWO0Jez4IMhewmLY.pwEdiAL7HVtutmEL2ukmu0TzuS',
    true
);

INSERT INTO users_roles (user_id, role_id) VALUES
(
    (SELECT id FROM users WHERE email = 'fulano.user@gmail.com'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
),
(
    (SELECT id FROM users WHERE email = 'fulano.admin@gmail.com'),
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
);

INSERT INTO categories (id, name) VALUES
(
    'b8f4bec3-93da-41fe-9830-3f3a6eb424bc',
    'Categoria Teste'
);
