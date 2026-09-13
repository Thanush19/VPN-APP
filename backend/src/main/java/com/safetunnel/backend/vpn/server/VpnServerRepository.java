package com.safetunnel.backend.vpn.server;

import com.safetunnel.backend.users.VpnServer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VpnServerRepository extends JpaRepository<VpnServer, UUID> {
    VpnServer findByHost(String host);
}