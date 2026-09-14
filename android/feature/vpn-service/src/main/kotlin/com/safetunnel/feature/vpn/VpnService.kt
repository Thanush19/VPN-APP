package com.safetunnel.feature.vpn

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.annotation.Nullable
import com.safetunnel.feature.vpn.WireGuardConfig
import com.safetunnel.feature.vpn.WireGuardConfigParser
import com.wireguard.android.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * SafeTunnel VPN Service - handles WireGuard tunnel establishment and management.
 */
class SafeTunnelVpnService : VpnService() {

    companion object {
        private const val TAG = "SafeTunnelVpnService"
        private const val VPN_NAME = "SafeTunnel VPN"

        // Intent actions
        const val ACTION_START_VPN = "com.safetunnel.action.START_VPN"
        const val ACTION_STOP_VPN = "com.safetunnel.action.STOP_VPN"
        const val EXTRA_VPN_CONFIG = "vpn_config"

        // Default settings
        private const val DEFAULT_DNS_SERVER = "8.8.8.8"
        private const val DEFAULT_MTU = 1420
    }

    private var wgInterface: com.wireguard.android.config.WireGuardInterface? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentConfig: String? = null

    @Nullable
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "VPN Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "VPN Service started with intent: $intent")

        when (intent?.action) {
            ACTION_START_VPN -> {
                val config = intent.getStringExtra(EXTRA_VPN_CONFIG)
                if (config != null) {
                    startVpn(config)
                } else {
                    Log.e(TAG, "No VPN config provided")
                }
            }
            ACTION_STOP_VPN -> {
                stopVpn()
            }
        }

        return Service.START_STICKY
    }

    private fun startVpn(config: String) {
        serviceScope.launch {
            try {
                currentConfig = config
                // Parse the WireGuard config
                val wgConfig = WireGuardConfigParser().parse(config)

                // Establish the TUN interface via VpnService.Builder
                val tunnel = Builder()
                    .setSession(VPN_NAME)
                    .setMtu(wgConfig.mtu ?: DEFAULT_MTU)
                    .establish()

                // Configure WireGuard interface
                val wgInterface = com.wireguard.android.config.WireGuardInterface.Builder()
                    .setPrivateKey(wgConfig.privateKey)
                    .setInterfaceName(VPN_NAME)
                    .addPeer(
                        com.wireguard.android.config.Peer.Builder()
                            .setPublicKey(wgConfig.serverPublicKey)
                            .setEndpoint(wgConfig.endpoint)
                            .addAllowedIp(wgConfig.allowedIps.firstOrNull() ?: "0.0.0.0/0")
                            .setPersistentKeepalive(wgConfig.persistentKeepalive ?: 25)
                            .build()
                    )
                    .setFileDescriptor(tunnel.fileDescriptor)
                    .build()

                // Start the WireGuard interface
                wgInterface.startInterface()

                Log.d(TAG, "VPN tunnel started successfully")

                // Keep the service running while the interface is active
                // We can wait here until the interface is stopped or the service is stopped
                // For simplicity, we'll just keep the service running and stop the interface when the service stops
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start VPN tunnel", e)
            }
        }
    }

    private fun stopVpn() {
        serviceScope.launch {
            try {
                wgInterface?.stopInterface()
                wgInterface = null
                currentConfig = null
                Log.d(TAG, "VPN tunnel stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop VPN tunnel", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn() // Ensure the interface is stopped when service is destroyed
        Log.d(TAG, "VPN Service destroyed")
    }

    /**
     * Request VPN permission from the user.
     * Call this from an Activity before starting the VPN.
     */
    fun requestPermission(activity: android.app.Activity, requestCode: Int = 0) {
        val intent = Builder().createIntent()
        activity.startActivityForResult(intent, requestCode)
    }

    private companion object {
        /**
         * Check if the VPN permission is granted.
         */
        fun hasPermission(context: android.content.Context): Boolean {
            return VpnService.containsPermission(context)
        }
    }
}