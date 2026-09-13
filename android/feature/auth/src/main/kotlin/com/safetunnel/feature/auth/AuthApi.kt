package com.safetunnel.feature.auth

import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable

interface AuthApi {
    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("/api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): AuthResponse
}

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
