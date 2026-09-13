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
@Table(name = "vpn_sessions")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class VpnSession {

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

    @Column(name = "connected_at", nullable = false)
    private Instant connectedAt = Instant.now();

    @Column(name = "disconnected_at")
    private Instant disconnectedAt;

    @Column(name = "bytes_uploaded")
    private Long bytesUploaded;

    @Column(name = "bytes_downloaded")
    private Long bytesDownloaded;

    @Column(name = "disconnect_reason")
    private String disconnectReason;
}