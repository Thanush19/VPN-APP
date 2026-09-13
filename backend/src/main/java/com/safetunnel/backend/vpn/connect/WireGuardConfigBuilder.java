package com.safetunnel.backend.vpn.connect;

import com.safetunnel.backend.users.VpnServer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class WireGuardConfigBuilder {
    public String build(String clientPrivateKey, String clientIp, VpnServer server) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Interface]\n");
        sb.append("PrivateKey = ").append(clientPrivateKey).append("\n");
        sb.append("Address = ").append(clientIp).append("\n");
        sb.append("DNS = 8.8.8.8\n\n");
        sb.append("[Peer]\n");
        sb.append("PublicKey = ").append(server.getPublicKey()).append("\n");
        sb.append("Endpoint = ").append(server.getHost()).append(":").append(server.getPort()).append("\n");
        sb.append("AllowedIPs = 0.0.0.0/0\n");
        sb.append("PersistentKeepalive = 25\n");
        return sb.toString();
    }
}
