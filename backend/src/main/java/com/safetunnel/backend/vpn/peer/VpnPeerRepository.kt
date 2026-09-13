package com.safetunnel.backend.vpn.peer

import com.safetunnel.backend.users.VpnPeer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VpnPeerRepository : JpaRepository<VpnPeer, UUID> {
    fun findByUserIdAndServerId(userId: UUID, serverId: UUID): VpnPeer?
}