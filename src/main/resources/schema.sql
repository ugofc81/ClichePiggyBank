CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role_order INTEGER NOT NULL,
    role VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, role_order)
);

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL UNIQUE,
    balance NUMERIC(19, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS sanctions (
    id UUID PRIMARY KEY,
    reporter_id UUID NOT NULL,
    reporter_name VARCHAR(255),
    receiver_id UUID NOT NULL,
    receiver_name VARCHAR(255),
    amount VARCHAR(20) NOT NULL,
    reason TEXT,
    created_at TIMESTAMP,
    likes INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sanction_user_roles (
    sanction_id UUID NOT NULL,
    side VARCHAR(20) NOT NULL,
    role_order INTEGER NOT NULL,
    role VARCHAR(100) NOT NULL,
    PRIMARY KEY (sanction_id, side, role_order)
);

CREATE TABLE IF NOT EXISTS sanction_likes (
    sanction_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (sanction_id, user_id)
);

CREATE TABLE IF NOT EXISTS legacy_data_imports (
    migration_id VARCHAR(100) PRIMARY KEY,
    imported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
