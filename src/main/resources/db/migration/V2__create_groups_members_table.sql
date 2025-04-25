CREATE TABLE groups
(
    id          serial NOT NULL,
    created_by  int4,
    updated_by  int4,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255),
    description VARCHAR(255),
    CONSTRAINT pk_groups PRIMARY KEY (id)
);

CREATE TABLE groups_members
(
    id       serial NOT NULL,
    group_id int4,
    user_id  int4,
    CONSTRAINT pk_groups_members PRIMARY KEY (id)
);


ALTER TABLE groups_members
    ADD CONSTRAINT FK_GROUPS_MEMBERS_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE groups_members
    ADD CONSTRAINT FK_GROUPS_MEMBERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE groups_members
    ADD CONSTRAINT uc_485c55904490151bcd55da852 UNIQUE (user_id);

ALTER TABLE groups_members
    ADD CONSTRAINT uc_c46cd57b8d4619aa98311da17 UNIQUE (group_id, user_id);

INSERT INTO groups (id, created_by, updated_by, created_at, updated_at, name, description)
VALUES ('999999', '999999',
        '999999', '2025-03-22 07:53:15.000000', '2025-03-22 07:53:17.000000',
        'superadmin_randomizer', 'superadmin');

INSERT INTO groups_members (id, group_id, user_id)
VALUES ('999999', '999999',
        '999999');
