package com.safetunnel.feature.servers

import retrofit2.http.GET
import kotlinx.serialization.Serializable

interface ServerApi {
    @GET("/api/v1/vpn/servers")
    suspend fun getServers(): List<ServerResponse>

    @GET("/api/v1/vpn/servers/{id}")
    suspend fun getServerById(@Path("id") id: String): ServerResponse
}

@Serializable
data class ServerResponse(
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val host: String,
    val port: Int,
    val publicKey: String,
    val status: String,
    val loadPercentage: Int?
)