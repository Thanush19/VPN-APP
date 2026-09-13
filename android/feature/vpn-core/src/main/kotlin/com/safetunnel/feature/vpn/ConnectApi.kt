package com.safetunnel.feature.vpn

import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable

interface ConnectApi {
    @POST("/api/v1/vpn/connect")
    suspend fun connect(@Body request: ConnectRequest): ConnectResponse
}

@Serializable
data class ConnectRequest(val clientPublicKey: String)

@Serializable
data class ConnectResponse(
    val serverPublicKey: String,
    val endpoint: String,
    val clientAddress: String,
    val dns: String,
    val allowedIps: List<String>
)
