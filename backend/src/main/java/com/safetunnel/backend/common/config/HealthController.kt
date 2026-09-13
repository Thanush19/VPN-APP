package com.safetunnel.backend.common.config

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.HashMap

@RestController
class HealthController {

    @GetMapping("/api/v1/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        val response = HashMap<String, Any>()
        response["status"] = "UP"
        response["timestamp"] = Instant.now().toString()
        response["service"] = "safetunnel-backend"
        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/v1/health/liveness")
    fun liveness(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "ALIVE"))
    }

    @GetMapping("/api/v1/health/readiness")
    fun readiness(): ResponseEntity<Map<String, String>> {
        // In a real application, this would check database connectivity, etc.
        return ResponseEntity.ok(mapOf("status" to "READY"))
    }
}