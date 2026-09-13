package com.safetunnel.feature.vpn

import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import androidx.annotation.Nullable

/**
 * A very small placeholder implementation of an Android VpnService.
 * In a real app you would plug in the wireguard‑android library
 * or use the WireGuard config string produced by the backend.
 */
class SafeTunnelVpnService : VpnService() {
    @Nullable
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // TODO: Build the tunnel with the config obtained from the backend
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Parse config, start tunnel, notify
        return Service.START_STICKY
    }

    override fun onDestroy() {
        // TODO: Teardown
        super.onDestroy()
    }
}
