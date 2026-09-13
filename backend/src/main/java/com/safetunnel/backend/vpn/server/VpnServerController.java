package com.safetunnel.backend.vpn.server;

import com.safetunnel.backend.users.VpnServer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vpn/servers")
@RequiredArgsConstructor
public class VpnServerController {

    private final VpnServerRepository vpnServerRepository;

    @GetMapping
    public ResponseEntity<List<VpnServerResponse>> getAllServers() {
        List<VpnServer> servers = vpnServerRepository.findAll();
        List<VpnServerResponse> responses = servers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VpnServerResponse> getServerById(@PathVariable String id) {
        VpnServer server = vpnServerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));
        return ResponseEntity.ok(toResponse(server));
    }

    private VpnServerResponse toResponse(VpnServer server) {
        return VpnServerResponse.builder()
                .id(server.getId().toString())
                .name(server.getName())
                .country(server.getCountry())
                .city(server.getCity())
                .host(server.getHost())
                .port(server.getPort())
                .publicKey(server.getPublicKey())
                .status(server.getStatus())
                .loadPercentage(server.getLoadPercentage())
                .build();
    }
}