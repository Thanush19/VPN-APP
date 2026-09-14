package com.safetunnel.core.model.vpn

/**
 * Represents the VPN connection state.
 */
enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

/**
 * Represents a VPN connection state with additional metadata.
 */
data class VpnConnectionState(
    val state: VpnState = VpnState.DISCONNECTED,
    val serverName: String? = null,
    val vpnIp: String? = null,
    val connectedAt: Long? = null,
    val bytesUploaded: Long = 0L,
    val bytesDownloaded: Long = 0L,
    val errorMessage: String? = null
) {
    val isConnected: Boolean get() = state == VpnState.CONNECTED
    val isDisconnected: Boolean get() = state == VpnState.DISCONNECTED || state == VpnState.ERROR
    val connectionDuration: Long? get() = if (connectedAt != null) System.currentTimeMillis() - connectedAt else null
}