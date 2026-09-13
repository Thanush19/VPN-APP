package com.safetunnel.backend.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class HealthController {

    @GetMapping("/api/v1/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("service", "safetunnel-backend");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/health/liveness")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of("status", "ALIVE"));
    }

    @GetMapping("/api/v1/health/readiness")
    public ResponseEntity<Map<String, String>> readiness() {
        // In a real application, this would check database connectivity, etc.
        return ResponseEntity.ok(Map.of("status", "READY"));
    }
}