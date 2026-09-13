--
-- Phase 3: Create Core Tables
-- This migration creates the tables that will be used by the backend to
-- persist user, VPN server, peer, and session information.

-- Users
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- VPN Servers
DROP TABLE IF EXISTS vpn_servers;
CREATE TABLE vpn_servers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    public_key VARCHAR(512) NOT NULL,
    status VARCHAR(50) NOT NULL,
    load_percentage INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- VPN Peers
DROP TABLE IF EXISTS vpn_peers;
CREATE TABLE vpn_peers (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    server_id UUID NOT NULL REFERENCES vpn_servers(id),
    public_key VARCHAR(512) NOT NULL,
    assigned_ip VARCHAR(45) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    revoked_at TIMESTAMP
);

-- VPN Sessions
DROP TABLE IF EXISTS vpn_sessions;
CREATE TABLE vpn_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    server_id UUID NOT NULL REFERENCES vpn_servers(id),
    connected_at TIMESTAMP NOT NULL DEFAULT now(),
    disconnected_at TIMESTAMP,
    bytes_uploaded BIGINT,
    bytes_downloaded BIGINT,
    disconnect_reason VARCHAR(255)
);

-- Trigger to keep updated_at column
CREATE FUNCTION update_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;

CREATE TRIGGER user_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER server_updated_at
BEFORE UPDATE ON vpn_servers
FOR EACH ROW
EXECUTE FUNCTION update_updated_at();