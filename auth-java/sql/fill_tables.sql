INSERT INTO roles (id, name, description)
VALUES
    (1, 'ADMIN', 'admin'),
    (2, 'USER', 'user');

INSERT INTO users(id, login, password)
VALUES
    (1, 'adminLogin', 'adminPassword'),
    (2, 'userLogin', 'userPassword'),
    (3, 'auLogin', 'auPassword');

INSERT INTO users_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 1),
    (3, 2);