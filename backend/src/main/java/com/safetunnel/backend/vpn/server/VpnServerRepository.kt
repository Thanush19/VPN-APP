package com.safetunnel.backend.vpn.server

import com.safetunnel.backend.users.VpnServer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VpnServerRepository : JpaRepository<VpnServer, UUID> {
    fun findByHost(host: String): VpnServer?
}