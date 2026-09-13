package com.safetunnel.backend.vpn.session

import com.safetunnel.backend.users.VpnSession
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VpnSessionRepository : JpaRepository<VpnSession, UUID> {
    fun findByUserIdOrderByConnectedAtDesc(userId: UUID): List<VpnSession>
}