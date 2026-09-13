package com.safetunnel.backend.vpn.connect;

import com.safetunnel.backend.users.User;
import com.safetunnel.backend.users.VpnPeer;
import com.safetunnel.backend.users.VpnServer;
import com.safetunnel.backend.vpn.peer.VpnPeerRepository;
import com.safetunnel.backend.vpn.server.VpnServerRepository;
import com.safetunnel.backend.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ConnectService {

    @Autowired
    private VpnServerRepository serverRepo;

    @Autowired
    private VpnPeerRepository peerRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WireGuardConfigBuilder configBuilder;

    @Transactional
    public ConnectResponse connect(String clientPublicKey) {
        // 1. Validate key length (placeholder)
        if (clientPublicKey == null || clientPublicKey.isEmpty()) {
            throw new IllegalArgumentException("clientPublicKey required");
        }

        // 2. pick first active server (for POC we ignore status)
        VpnServer server = serverRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No VPN servers configured"));

        // 3. allocate next free IP – hard‑coded subnet 10.8.0.0/24
        String subnet = "10.8.0.0/24";
        String allocatedIp = allocateIp(server, subnet);

        // 4. Get current authenticated user
        User user = getCurrentUser();

        // 5. persist peer
        VpnPeer peer = new VpnPeer();
        peer.setUser(user);
        peer.setServer(server);
        peer.setPublicKey(clientPublicKey);
        peer.setAssignedIp(allocatedIp);
        peerRepo.save(peer);

        // 6. build return config string
        String wgConfig = configBuilder.build(clientPublicKey, allocatedIp, server);

        // 7. Build ConnectResponse
        ConnectResponse resp = new ConnectResponse();
        resp.setServerPublicKey(server.getPublicKey());
        resp.setEndpoint(server.getHost() + ":" + server.getPort());
        resp.setClientAddress(allocatedIp);
        resp.setDns("8.8.8.8");
        resp.setAllowedIps(List.of("0.0.0.0/0"));
        return resp;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email);
        if (user == null) throw new IllegalStateException("Unauthenticated user");
        return user;
    }

    // Simple IP allocator: skip .0 and .255, return first free in 10.8.0.x
    private String allocateIp(VpnServer server, String subnet) {
        // naive implementation: use .2 if free else .3 ...
        List<VpnPeer> peers = peerRepo.findAll();
        for (int i = 2; i <= 254; i++) {
            String candidate = "10.8.0." + i;
            boolean used = peers.stream().anyMatch(p -> p.getAssignedIp().equals(candidate));
            if (!used) return candidate;
        }
        throw new IllegalStateException("No free IPs available");
    }
}
