package com.safetunnel.core.model.vpn

/**
 * Represents a WireGuard configuration for a client.
 */
data class WireGuardConfigModel(
    val serverPublicKey: String,
    val endpoint: String,
    val clientAddress: String,
    val dns: String = "8.8.8.8",
    val allowedIps: List<String> = listOf("0.0.0.0/0"),
    val persistentKeepalive: Int = 25
) {
    fun toConfigString(): String {
        val sb = StringBuilder()
        sb.append("[Interface]\n")
        sb.append("Address = $clientAddress\n")
        sb.append("DNS = $dns\n")
        sb.append("\n[Peer]\n")
        sb.append("PublicKey = $serverPublicKey\n")
        sb.append("Endpoint = $endpoint\n")
        sb.append("AllowedIPs = ${allowedIps.joinToString(",")}\n")
        sb.append("PersistentKeepalive = $persistentKeepalive\n")
        return sb.toString()
    }
}