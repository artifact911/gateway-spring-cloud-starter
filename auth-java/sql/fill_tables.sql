INSERT INTO roles (id, name, description)
VALUES
    (1, 'ADMIN', 'admin'),
    (2, 'USER', 'user');

INSERT INTO users(id, name, login, password)
VALUES
    (1, 'adminName', 'adminLogin', 'adminPassword'),
    (2, 'userName', 'userLogin', 'userPassword'),
    (3, 'auName', 'auLogin', 'auPassword');

INSERT INTO users_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 1),
    (3, 2);