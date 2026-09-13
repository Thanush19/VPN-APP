package com.safetunnel.backend.vpn.connect;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ConnectRequest {
    @NotBlank
    private String clientPublicKey;
}
