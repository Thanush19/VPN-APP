package com.safetunnel.backend.vpn.connect;

import lombok.Data;
import java.util.List;

@Data
public class ConnectResponse {
    private String serverPublicKey;
    private String endpoint;
    private String clientAddress;
    private String dns;
    private List<String> allowedIps;
}
