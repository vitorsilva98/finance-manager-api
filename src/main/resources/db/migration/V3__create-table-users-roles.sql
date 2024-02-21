CREATE TABLE users_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    CONSTRAINT fk_users_roles_user_id FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_users_roles_role_id FOREIGN KEY(role_id) REFERENCES roles(id)
);
