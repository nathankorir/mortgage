CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    name       VARCHAR(100),
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT Now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT Now()
);

CREATE TABLE roles
(
    id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users_roles
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Insert roles with fixed UUIDs
INSERT INTO roles (id, name)
VALUES ('85f351c1-0de1-4435-aa4b-307cff5ddf15', 'ROLE_APPLICANT'),
       ('bd4fea33-39cf-4a8f-b066-2de498defabe', 'ROLE_OFFICER');
-- Insert default applicant
INSERT INTO users (id, name, username, email, password)
VALUES ('322efad7-76a6-47c7-ba04-6366b849a595','Nathan Applicant','nathan_applicant',   'nathankipron@gmail.com','$2a$10$LxfJMsM3j2uf2upyt662N.tAzgt3qDcFA1OUVPKcIF8dk.cQqk8uO');
-- Insert default officer
INSERT INTO users (id, name, username, email, password)
VALUES ('afc5d2ba-7ab7-4ad2-93b5-10ae3eda373f','Nathan Officer','nathan_officer',   'nathankipron@yahoo.com','$2a$10$gqHrslMttQWSsDSVRTK1OehkkBiXsJ/a4z2OURU./dizwOQu5Lovu');

-- Assign APPLICANT role to default applicant
INSERT INTO users_roles (user_id, role_id)
VALUES ('322efad7-76a6-47c7-ba04-6366b849a595', '85f351c1-0de1-4435-aa4b-307cff5ddf15');

-- Assign OFFICER role to default officer
INSERT INTO users_roles (user_id, role_id)
VALUES ('afc5d2ba-7ab7-4ad2-93b5-10ae3eda373f', 'bd4fea33-39cf-4a8f-b066-2de498defabe');