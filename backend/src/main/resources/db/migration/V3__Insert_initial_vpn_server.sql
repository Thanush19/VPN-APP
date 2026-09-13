-- Insert a sample VPN server for development/testing
INSERT INTO vpn_servers (id, name, country, city, host, port, public_key, status, load_percentage, created_at, updated_at)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Test Server 1',
    'United States',
    'New York',
    'vpn1.safetunnel.example.com',
    51820,
    'BASE64_PUBLIC_KEY_PLACEHOLDER',
    'ACTIVE',
    0,
    NOW(),
    NOW()
);