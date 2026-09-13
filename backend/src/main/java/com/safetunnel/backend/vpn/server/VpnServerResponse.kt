package com.safetunnel.backend.vpn.server

data class VpnServerResponse(
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