package com.safetunnel.feature.vpn

import java.util.regex.Pattern

data class WireGuardConfig(
    val privateKey: String,
    val address: String,
    val dns: String?,
    val serverPublicKey: String,
    val endpoint: String,
    val allowedIps: List<String>,
    val persistentKeepalive: Int?
) {
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[Interface]\n")
        builder.append("PrivateKey = $privateKey\n")
        builder.append("Address = $address\n")
        dns?.let { builder.append("DNS = $it\n") }
        builder.append("\n[Peer]\n")
        builder.append("PublicKey = $serverPublicKey\n")
        builder.append("Endpoint = $endpoint\n")
        builder.append("AllowedIPs = ${allowedIps.joinToString(",")}\n")
        persistentKeepalive?.let { builder.append("PersistentKeepalive = $it\n") }
        return builder.toString()
    }
}

class WireGuardConfigParser {
    private val interfacePattern = Pattern.compile(
        """PrivateKey\s*=\s*(.+)\s*Address\s*=\s*(.+?)\s*(?:DNS\s*=\s*(.+?))?\s*$$[Peer]"""
        , Pattern.DOTALL)
    private val peerPattern = Pattern.compile(
        """PublicKey\s*=\s*(.+?)\s*Endpoint\s*=\s*(.+?)\s*AllowedIPs\s*=\s*(.+?)(?:\s*PersistentKeepalive\s*=\s*(\d+))?"""
        , Pattern.DOTALL)

    fun parse(config: String): WireGuardConfig {
        val interfaceMatcher = interfacePattern.matcher(config)
        if (!interfaceMatcher.find()) {
            throw IllegalArgumentException("Invalid WireGuard config: missing [Interface] section")
        }

        val privateKey = interfaceMatcher.group(1).trim()
        val address = interfaceMatcher.group(2).trim()
        val dns = interfaceMatcher.group(3)?.trim()?.takeIf { it.isNotEmpty() }

        val peerMatcher = peerPattern.matcher(config)
        if (!peerMatcher.find()) {
            throw IllegalArgumentException("Invalid WireGuard config: missing [Peer] section")
        }

        val serverPublicKey = peerMatcher.group(1).trim()
        val endpoint = peerMatcher.group(2).trim()
        val allowedIps = peerMatcher.group(3).trim().split(",").map { it.trim() }
        val persistentKeepalive = peerMatcher.group(4)?.trim()?.toIntOrNull()

        return WireGuardConfig(
            privateKey = privateKey,
            address = address,
            dns = dns,
            serverPublicKey = serverPublicKey,
            endpoint = endpoint,
            allowedIps = allowedIps,
            persistentKeepalive = persistentKeepalive
        )
    }

    fun parseConfigString(config: String): Pair<String, String> {
        // For backward compatibility, also return raw keys for simpler parsing
        val matcher = Pattern.compile("""PrivateKey\s*=\s*(\S+)""").matcher(config)
        if (matcher.find()) {
            val privateKey = matcher.group(1)
            return Pair(privateKey, config.substringAfter("Address = ").substringBefore("\n"))
        }
        throw IllegalArgumentException("Invalid WireGuard config: cannot find PrivateKey")
    }
}
