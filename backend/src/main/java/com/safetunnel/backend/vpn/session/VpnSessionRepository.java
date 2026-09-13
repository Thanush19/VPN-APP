package com.safetunnel.backend.vpn.session;

import com.safetunnel.backend.users.VpnSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface VpnSessionRepository extends JpaRepository<VpnSession, UUID> {
    List<VpnSession> findByUserIdOrderByConnectedAtDesc(UUID userId);
}