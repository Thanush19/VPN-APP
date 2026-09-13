package com.safetunnel.backend.vpn.connect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/vpn")
public class ConnectController {

    @Autowired
    private ConnectService connectService;

    @PostMapping("/connect")
    public ResponseEntity<ConnectResponse> connect(@RequestBody ConnectRequest request) {
        ConnectResponse response = connectService.connect(request.getClientPublicKey());
        return ResponseEntity.ok(response);
    }
}
