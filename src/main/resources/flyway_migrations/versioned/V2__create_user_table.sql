CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    username VARCHAR(128),
    password_hash VARCHAR(255),
    email VARCHAR(128),
    first_name VARCHAR(128),
    last_name VARCHAR(128),
    user_type VARCHAR(16),
    enabled BOOLEAN,
    deleted BOOLEAN,

    created_on TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_on TIMESTAMP WITHOUT TIME ZONE,
    deleted_on TIMESTAMP WITHOUT TIME ZONE,

    PRIMARY KEY (id),
    UNIQUE (username),
    UNIQUE (email)
);