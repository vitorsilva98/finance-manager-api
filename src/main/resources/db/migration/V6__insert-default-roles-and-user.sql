DO $$
DECLARE
    user_id uuid := gen_random_uuid();
    role_user_id uuid := gen_random_uuid();
    role_admin_id uuid := gen_random_uuid();
BEGIN
    INSERT INTO roles (id, name) VALUES (role_user_id, 'ROLE_USER'), (role_admin_id, 'ROLE_ADMIN');
    INSERT INTO users (id, email, name, password, disabled) VALUES (user_id, 'vitor.augsilva98@gmail.com', 'Vitor Silva', '$2a$10$0.O9v2mF5wezWymDx016nOR18w7yDztpsusJuQtNiZLuJYX8ShMau', false);
    INSERT INTO users_roles (user_id, role_id) VALUES (user_id, role_user_id), (user_id, role_admin_id);
END $$;
