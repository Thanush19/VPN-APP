package com.safetunnel.feature.vpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetunnel.core.common.mvi.BaseIntent
import com.safetunnel.core.common.mvi.BaseState
import com.safetunnel.core.common.mvi.BaseViewModel
import com.safetunnel.core.model.vpn.VpnConnectionState
import com.safetunnel.core.model.vpn.VpnState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VpnViewModel @Inject constructor(
    private val connectRepository: ConnectRepository,
    private val keyPairManager: VpnKeyPairManager,
    @ApplicationContext private val context: Context
) : BaseViewModel<VpnStateModel, VpnIntent>() {

    override val state = MutableStateFlow(VpnStateModel()).asStateFlow()

    override fun onIntent(intent: VpnIntent) {
        when (intent) {
            is VpnIntent.Connect -> handleConnect()
            is VpnIntent.Disconnect -> handleDisconnect()
            is VpnIntent.LoadState -> handleLoadState()
        }
    }

    private fun handleConnect() {
        viewModelScope.launch(Dispatchers.IO) {
            emit(VpnStateModel(state = VpnState.CONNECTING))

            try {
                // Get or generate client key pair
                val keyPair = keyPairManager.getOrGenerateKeyPair()
                val clientPublicKey = java.util.Base64.getEncoder().encodeToString(keyPair.public.encoded)

                // Call backend to get VPN configuration
                val response = connectRepository.connect(clientPublicKey)

                // Convert response to config model
                val config = com.safetunnel.core.model.vpn.WireGuardConfigModel(
                    serverPublicKey = response.serverPublicKey,
                    endpoint = response.endpoint,
                    clientAddress = response.clientAddress,
                    dns = response.dns,
                    allowedIps = response.allowedIps
                )

                // Start VPN service with config
                val intent = android.content.Intent(context, SafeTunnelVpnService::class.java)
                intent.action = SafeTunnelVpnService.ACTION_START_VPN
                intent.putExtra(SafeTunnelVpnService.EXTRA_VPN_CONFIG, config.toConfigString())
                context.startForegroundService(intent)

                emit(VpnStateModel(
                    state = VpnState.CONNECTED,
                    serverName = "Test Server 1",
                    vpnIp = response.clientAddress,
                    connectedAt = System.currentTimeMillis()
                ))
            } catch (e: Exception) {
                emit(VpnStateModel(
                    state = VpnState.ERROR,
                    errorMessage = e.message ?: "Unknown error"
                ))
            }
        }
    }

    private fun handleDisconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            emit(VpnStateModel(state = VpnState.DISCONNECTING))

            try {
                // Stop VPN service
                val intent = android.content.Intent(context, SafeTunnelVpnService::class.java)
                intent.action = SafeTunnelVpnService.ACTION_STOP_VPN
                context.startService(intent)

                emit(VpnStateModel(state = VpnState.DISCONNECTED))
            } catch (e: Exception) {
                emit(VpnStateModel(
                    state = VpnState.ERROR,
                    errorMessage = e.message ?: "Disconnect failed"
                ))
            }
        }
    }

    private fun handleLoadState() {
        // TODO: Load saved VPN state
    }
}

// MVI Intent
sealed interface VpnIntent : BaseIntent {
    data class Connect : VpnIntent
    data class Disconnect : VpnIntent
    data class LoadState : VpnIntent
}

// MVI State
data class VpnStateModel(
    val state: VpnState = VpnState.DISCONNECTED,
    val serverName: String? = null,
    val vpnIp: String? = null,
    val connectedAt: Long? = null,
    val bytesUploaded: Long = 0L,
    val bytesDownloaded: Long = 0L,
    val errorMessage: String? = null
) : BaseState