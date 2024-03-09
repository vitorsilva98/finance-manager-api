INSERT INTO users (id, email, name, password, disabled) VALUES
(
    gen_random_uuid(),
    'fulano.enabled@gmail.com',
    'Fulano Enabled',
    '$2a$10$EH3xj0RMlXUWO0Jez4IMhewmLY.pwEdiAL7HVtutmEL2ukmu0TzuS',
    false
),
(
    gen_random_uuid(),
    'fulano.disabled@gmail.com',
    'Fulano Disabled',
    '$2a$10$EH3xj0RMlXUWO0Jez4IMhewmLY.pwEdiAL7HVtutmEL2ukmu0TzuS',
    true
);

INSERT INTO users_roles (user_id, role_id) VALUES
(
    (SELECT id FROM users WHERE email = 'fulano.enabled@gmail.com'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
),
(
    (SELECT id FROM users WHERE email = 'fulano.disabled@gmail.com'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
);
