package com.safetunnel.backend.users

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,
    @Column(nullable = false)
    var role: String,
    @Column(nullable = false)
    var status: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)

@Entity
@Table(name = "vpn_servers")
class VpnServer(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val country: String,
    @Column(nullable = false)
    val city: String,
    @Column(nullable = false)
    val host: String,
    @Column(nullable = false)
    val port: Int,
    @Column(nullable = false)
    val publicKey: String,
    @Column(nullable = false)
    var status: String,
    @Column(name = "load_percentage")
    var loadPercentage: Int? = null,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)

@Entity
@Table(name = "vpn_peers")
class VpnPeer(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    var server: VpnServer,
    @Column(name = "public_key", nullable = false)
    var publicKey: String,
    @Column(name = "assigned_ip", nullable = false)
    var assignedIp: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(name = "revoked_at")
    var revokedAt: Instant? = null
)

@Entity
@Table(name = "vpn_sessions")
class VpnSession(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    var server: VpnServer,
    @Column(name = "connected_at", nullable = false)
    val connectedAt: Instant = Instant.now(),
    @Column(name = "disconnected_at")
    var disconnectedAt: Instant? = null,
    @Column(name = "bytes_uploaded")
    var bytesUploaded: Long? = null,
    @Column(name = "bytes_downloaded")
    var bytesDownloaded: Long? = null,
    @Column(name = "disconnect_reason")
    var disconnectReason: String? = null
)
