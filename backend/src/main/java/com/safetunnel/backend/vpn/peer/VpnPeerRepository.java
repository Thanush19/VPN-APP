package com.safetunnel.backend.vpn.peer;

import com.safetunnel.backend.users.VpnPeer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VpnPeerRepository extends JpaRepository<VpnPeer, UUID> {
    VpnPeer findByUserIdAndServerId(UUID userId, UUID serverId);
}