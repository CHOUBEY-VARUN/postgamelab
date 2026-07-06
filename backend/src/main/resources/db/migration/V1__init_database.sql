CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE app_metadata (
    id UUID PRIMARY KEY,
    app_name VARCHAR(100) NOT NULL,
    initialized_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_metadata (id, app_name)
VALUES (gen_random_uuid(), 'PostGameLab');