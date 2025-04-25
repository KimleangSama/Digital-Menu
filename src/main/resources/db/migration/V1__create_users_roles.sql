CREATE TABLE users
(
    id                 serial       NOT NULL,
    created_by         int4,
    updated_by         int4,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    username           VARCHAR(255) NOT NULL,
    password           VARCHAR(255) NOT NULL,
    raw                VARCHAR(255) NOT NULL,
    fullname           VARCHAR(255),
    email              VARCHAR(255),
    phone              VARCHAR(255),
    address            VARCHAR(255),
    emergency_contact  VARCHAR(255),
    emergency_relation VARCHAR(255),
    profile            VARCHAR(255),
    last_login_at      TIMESTAMP WITHOUT TIME ZONE,
    provider           VARCHAR(255) NOT NULL,
    status             VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   serial       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    role_id serial NOT NULL,
    user_id serial NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (role_id, user_id)
);

ALTER TABLE roles
    ADD CONSTRAINT unq_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_8eba69f9c904252e3321f0bae UNIQUE (phone);

ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_77584fbe74cc86922be2a3560 UNIQUE (username);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

INSERT INTO users (id, created_by, updated_by, created_at, updated_at, username, password, raw, fullname, email, phone,
                   address, emergency_contact, emergency_relation, profile, last_login_at, provider, status)

VALUES ('999999', null, null, '2025-03-21 21:36:52.603573',
        '2025-03-21 21:36:52.603595', 'superadmin', '$2a$10$Oet966h8Aimau6Eu0p5gYufsj4gTVFlz7aE9J85YhWyzTouY6T8ym',
        'superadmin@_@', 'superadmin', 'superadmin', null, null,
        null, null, null, null, 'local',
        'pending');

INSERT INTO roles (id, name)
VALUES (1, 'admin');
INSERT INTO roles (id, name)
VALUES (2, 'manager');

INSERT INTO users_roles (role_id, user_id)
VALUES (1, '999999');
INSERT INTO users_roles (role_id, user_id)
VALUES (2, '999999');
