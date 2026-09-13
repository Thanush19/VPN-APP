package com.safetunnel.backend.vpn.server

import com.safetunnel.backend.users.VpnServer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

import java.util.List
import java.util.UUID

@RestController
@RequestMapping("/api/v1/vpn/servers")
class VpnServerController(
    private val vpnServerRepository: VpnServerRepository
) {

    @GetMapping
    fun getAllServers(): ResponseEntity<List<VpnServerResponse>> {
        val servers = vpnServerRepository.findAll()
        val responses = servers.map { toResponse(it) }
        return ResponseEntity.ok(responses.toList())
    }

    @GetMapping("/{id}")
    fun getServerById(@PathVariable id: String): ResponseEntity<VpnServerResponse> {
        val server = vpnServerRepository.findById(UUID.fromString(id))
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found") }
        return ResponseEntity.ok(toResponse(server))
    }

    private fun toResponse(server: VpnServer): VpnServerResponse {
        return VpnServerResponse(
            id = server.id.toString(),
            name = server.name,
            country = server.country,
            city = server.city,
            host = server.host,
            port = server.port,
            publicKey = server.publicKey,
            status = server.status,
            loadPercentage = server.loadPercentage
        )
    }
}