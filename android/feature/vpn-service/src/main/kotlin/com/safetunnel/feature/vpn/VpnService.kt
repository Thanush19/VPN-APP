package com.safetunnel.feature.vpn

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.annotation.Nullable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.ByteBuffer

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

    private var vpnInterface: ParcelFileDescriptor? = null
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
                val tunnel = buildTunnel(config)
                vpnInterface = tunnel.start()
                Log.d(TAG, "VPN tunnel started successfully")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start VPN tunnel", e)
            }
        }
    }

    private fun stopVpn() {
        serviceScope.launch {
            try {
                vpnInterface?.close()
                vpnInterface = null
                currentConfig = null
                Log.d(TAG, "VPN tunnel stopped")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to stop VPN tunnel", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun buildTunnel(config: String): Builder {
        Log.d(TAG, "Building tunnel with config")

        val parsedConfig = parseWireGuardConfig(config)

        val builder = Builder()
        builder.setSession(VPN_NAME)
        builder.setMtu(DEFAULT_MTU)

        // Add DNS server (can be overridden from config)
        val dns = parsedConfig["DNS"] ?: DEFAULT_DNS_SERVER
        builder.addDnsServer(dns)

        // Add routes - for now, route all traffic through VPN
        builder.addRoute("0.0.0.0", 0)

        // Add the app package name to exempt it from VPN routing
        // This prevents the VPN traffic from routing back through itself
        builder.addDisallowedApplication(packageName)

        return builder
    }

    /**
     * Simple WireGuard config parser that extracts key-value pairs.
     */
    private fun parseWireGuardConfig(config: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val lines = config.lines()

        for (line in lines) {
            val trimmed = line.trim()

            // Skip section headers and empty lines
            if (trimmed.startsWith("[") || trimmed.isEmpty()) continue

            // Parse key = value
            if ("=" in trimmed) {
                val parts = trimmed.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    result[key] = value
                }
            }
        }

        return result
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            vpnInterface?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing VPN interface", e)
        }
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