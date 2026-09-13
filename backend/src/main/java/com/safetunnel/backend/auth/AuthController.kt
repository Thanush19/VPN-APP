package com.safetunnel.backend.auth

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request.email, request.password)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request.email, request.password)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        val response = authService.refreshToken(request.refreshToken)
        return ResponseEntity.ok(response)
    }
}
