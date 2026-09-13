package com.safetunnel.feature.servers.data

import com.safetunnel.core.common.mvi.BaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.viewModelScope
import androidx.lifecycle.ViewModel

/**
 * Repository for VPN servers data.
 * In Phase 1, this returns mock data - actual network calls will be added in later phases.
 */
class ServersRepository {
    // TODO: Inject remote data source in later phases

    suspend fun getServers(): List<VpnServer> {
        // Mock data for development - replace with actual API call in later phases
        return listOf(
            VpnServer(
                id = "1",
                name = "India - Mumbai",
                country = "India",
                city = "Mumbai",
                status = "ONLINE",
                loadPercentage = 32
            ),
            VpnServer(
                id = "2",
                name = "United States - New York",
                country = "United States",
                city = "New York",
                status = "ONLINE",
                loadPercentage = 45
            ),
            VpnServer(
                id = "3",
                name = "Germany - Frankfurt",
                country = "Germany",
                city = "Frankfurt",
                status = "ONLINE",
                loadPercentage = 28
            ),
            VpnServer(
                id = "4",
                name = "Japan - Tokyo",
                country = "Japan",
                city = "Tokyo",
                status = "MAINTENANCE",
                loadPercentage = null
            ),
            VpnServer(
                id = "5",
                name = "Australia - Sydney",
                country = "Australia",
                city = "Sydney",
                status = "OFFLINE",
                loadPercentage = null
            )
        )
    }
}

/**
 * Data class representing a VPN server.
 */
data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val status: String,
    val loadPercentage: Int? = null
) : BaseState

/**
 * Intents (user actions) for the Servers feature.
 */
sealed interface ServersIntent {
    object LoadServers : ServersIntent
    data class ServerSelected(val server: VpnServer) : ServersIntent
    object Retry : ServersIntent
}

/**
 * ViewModel for the Servers feature.
 */
class ServersViewModel(
    private val repository: ServersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServersUiState(
        servers = emptyList(),
        isLoading = false,
        error = null
    ))
    val uiState: StateFlow<ServersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadServers()
        }
    }

    fun loadServers() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val servers = repository.getServers()
                _uiState.value = _uiState.value.copy(
                    servers = servers,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    servers = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun handleIntent(intent: ServersIntent) {
        when (intent) {
            is ServersIntent.LoadServers -> loadServers()
            is ServersIntent.Retry -> loadServers()
            is ServersIntent.ServerSelected -> {
                // Server selection handled by UI layer
                // This would typically notify parent or use event bus
            }
        }
    }
}

/**
 * UI State for the Servers screen.
 */
data class ServersUiState(
    val servers: List<VpnServer>,
    val isLoading: Boolean,
    val error: String?
) : BaseState