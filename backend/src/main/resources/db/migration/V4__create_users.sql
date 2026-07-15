CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(40) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_users_username_lower
ON users (lower(username));

CREATE UNIQUE INDEX uq_users_email_lower
ON users (lower(email));