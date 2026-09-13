package com.safetunnel.backend.vpn.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VpnServerResponse {
    private String id;
    private String name;
    private String country;
    private String city;
    private String host;
    private int port;
    private String publicKey;
    private String status;
    private Integer loadPercentage;
}