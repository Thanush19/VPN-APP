# WireGuard VPN POC Configuration

## Overview

This document describes the manual WireGuard client configuration needed to establish a VPN connection to the existing WireGuard server during the POC phase.

## Server Configuration (Existing Infrastructure)

The existing WireGuard server must provide:

- **Public Key**: `<server-public-key>` (Base64 encoded)
- **Endpoint**: `<server-host>:51820` (e.g., `vpn1.safetunnel.example.com:51820`)
- **VPN Network**: `10.8.0.0/24`
- **DNS Server**: `8.8.8.8` (or internal DNS)

## Client Configuration (Android App)

The Android client generates its own key pair locally using the `VpnKeyPairManager`.

### Manual Client Configuration Template

```
[Interface]
PrivateKey = <client-private-key>
Address = 10.8.0.2/32
DNS = 8.8.8.8

[Peer]
PublicKey = <server-public-key>
Endpoint = <server-host>:51820
AllowedIPs = 0.0.0.0/0
PersistentKeepalive = 25
```

### Key Generation

The client key pair is generated using Android Keystore with EC secp256r1 algorithm:

```kotlin
// In VpnKeyPairManager
val kpg = KeyPairGenerator.getInstance("EC", "AndroidKeyStore")
val spec = KeyGenParameterSpec.Builder(
    KEY_ALIAS,
    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
).apply {
    setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
    setUserAuthenticationRequired(false)
}.build()
kpg.initialize(spec)
val pair = kpg.generateKeyPair()
```

The public key is stored in DataStore for easy access and sent to the backend during the connect request.

## Backend Configuration

The backend (`ConnectService`) provides the VPN configuration:

1. Validates the client public key
2. Selects an active VPN server
3. Allocates a VPN IP (from 10.8.0.0/24)
4. Persists the peer in the database
5. Returns the WireGuard configuration

### Connect API Response

```json
{
  "serverPublicKey": "<server-public-key>",
  "endpoint": "<server-host>:51820",
  "clientAddress": "10.8.0.2/24",
  "dns": "8.8.8.8",
  "allowedIps": ["0.0.0.0/0"]
}
```

## Peer Registration on Server

The Android client's public key must be manually added as a peer on the existing WireGuard server.

### Server-side Configuration

On the WireGuard server, add the peer with:

```bash
wg set wg0 peer <client-public-key> allowed-ips 10.8.0.2/32
```

Or in the server's WireGuard config file:

```
[Peer]
PublicKey = <client-public-key>
AllowedIPs = 10.8.0.2/32
```

## POC Validation Checklist

Before implementing the Android VPN service, verify:

- [ ] WireGuard server is reachable
- [ ] Client peer can establish a handshake
- [ ] Android receives the VPN IP
- [ ] Android traffic is routed through the tunnel
- [ ] Internet access works through the tunnel
- [ ] Public IP changes to the VPN server's public IP
- [ ] DNS resolution works
- [ ] Disconnecting the tunnel restores normal connectivity

## Success Criteria

```
Android App
    ↓
WireGuard Tunnel
    ↓
Existing WireGuard Server
    ↓
Internet
```

## Next Steps

Once the manual configuration is validated, implement the Android VPN service to:

1. Generate/load client key pair
2. Call `/api/v1/vpn/connect` with client public key
3. Parse the backend response
4. Build WireGuard tunnel configuration
5. Start VPN service
6. Handle connection lifecycle