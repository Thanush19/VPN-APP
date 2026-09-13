package com.safetunnel.backend.vpn.connect;

import com.safetunnel.backend.users.User;
import com.safetunnel.backend.users.VpnPeer;
import com.safetunnel.backend.users.VpnServer;
import com.safetunnel.backend.vpn.peer.VpnPeerRepository;
import com.safetunnel.backend.vpn.server.VpnServerRepository;
import com.safetunnel.backend.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConnectServiceTest {

    @Mock
    private VpnServerRepository serverRepo;

    @Mock
    private VpnPeerRepository peerRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private WireGuardConfigBuilder configBuilder;

    @InjectMocks
    private ConnectService connectService;

    private VpnServer testServer;
    private User testUser;

    @BeforeEach
    void setUp() {
                // Mock user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hash");
        when(userRepo.findByEmail("test@example.com")).thenReturn(testUser);

        // Prepare a test VPN server
        testServer = new VpnServer();
        testServer.setId(UUID.randomUUID());
        testServer.setHost("127.0.0.1");
        testServer.setPort(51820);
        testServer.setPublicKey("serverpubkey");
        when(serverRepo.findAll()).thenReturn(java.util.List.of(testServer));
    }

    @Test
    void connect_returnsValidResponse() {
        // Arrange
        String clientPubKey = "clientpubkey123";
        String allocatedIp = "10.8.0.2";

        // Stub the config builder to return a dummy config string
        when(configBuilder.build(anyString(), eq(allocatedIp), any(VpnServer.class)))
                .thenReturn("dummy config");

        // Act
        var response = connectService.connect(clientPubKey);

        // Assert
        assertNotNull(response);
        assertEquals(testServer.getPublicKey(), response.getServerPublicKey());
        assertEquals(testServer.getHost() + ":" + testServer.getPort(), response.getEndpoint());
        assertEquals(allocatedIp, response.getClientAddress());
        assertEquals("8.8.8.8", response.getDns());
        assertEquals(java.util.List.of("0.0.0.0/0"), response.getAllowedIps());

        // Verify peer saved with correct properties
        verify(peerRepo).save(argThat(peer ->
                peer.getPublicKey().equals(clientPubKey) &&
                peer.getAssignedIp().equals(allocatedIp) &&
                peer.getUser().equals(testUser) &&
                peer.getServer().equals(testServer)
        ));
    }

    @Test
    void connect_throwsWhenNoServers() {
        when(serverRepo.findAll()).thenReturn(java.util.List.of());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> connectService.connect("somekey"));
        assertTrue(ex.getMessage().contains("No VPN servers configured"));
    }
}
