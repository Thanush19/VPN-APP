package com.safetunnel.backend.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vpn_peers")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class VpnPeer {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private VpnServer server;

    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "assigned_ip", nullable = false)
    private String assignedIp;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "revoked_at")
    private Instant revokedAt;
}