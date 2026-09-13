package com.safetunnel.feature.home.data

import com.safetunnel.core.common.mvi.BaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.viewModelScope
import androidx.lifecycle.ViewModel

/**
 * Repository for the Home feature.
 * In Phase 1, this is a placeholder - actual data sources will be added in later phases.
 */
class HomeRepository {
    // TODO: Inject data sources (Remote, Local, etc.) in later phases

    suspend fun getInitialState(): HomeUiState {
        // Placeholder - return initial state
        return HomeUiState(
            connectionState = ConnectionState.DISCONNECTED,
            selectedServer = null,
            connectionDuration = 0L,
            downloadBytes = 0L,
            uploadBytes = 0L,
            isLoading = false,
            error = null
        )
    }
}

/**
 * UI State for the Home screen.
 */
data class HomeUiState(
    val connectionState: ConnectionState,
    val selectedServer: VpnServer?,
    val connectionDuration: Long,
    val downloadBytes: Long,
    val uploadBytes: Long,
    val isLoading: Boolean,
    val error: String?
) : BaseState

/**
 * Connection state enum.
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    RECONNECTING,
    ERROR
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
)

/**
 * Intents (user actions) for the Home feature.
 */
sealed interface HomeIntent : BaseIntent {
    object Connect : HomeIntent
    object Disconnect : HomeIntent
    data class SelectServer(val server: VpnServer) : HomeIntent
    object Retry : HomeIntent
}

/**
 * Base interface for ViewModel intents (user actions) in the feature.
 */
interface BaseIntent

/**
 * ViewModel for the Home feature.
 */
class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(
        connectionState = ConnectionState.DISCONNECTED,
        selectedServer = null,
        connectionDuration = 0L,
        downloadBytes = 0L,
        uploadBytes = 0L,
        isLoading = false,
        error = null
    ))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val initialState = repository.getInitialState()
            _state.value = initialState
        }
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Connect -> {
                _state.value = _state.value.copy(isLoading = true, error = null)
                // TODO: Actual connection logic will be added in later phases
                viewModelScope.launch {
                    try {
                        // Simulate connection delay
                        delay(1000)
                        // TODO: Replace with actual connection use case
                        _state.value = _state.value.copy(
                            connectionState = ConnectionState.CONNECTED,
                            isLoading = false
                        )
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            connectionState = ConnectionState.ERROR,
                            isLoading = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
            }
            is HomeIntent.Disconnect -> {
                _state.value = _state.value.copy(isLoading = true)
                // TODO: Actual disconnection logic
                viewModelScope.launch {
                    delay(500)
                    _state.value = _state.value.copy(
                        connectionState = ConnectionState.DISCONNECTED,
                        isLoading = false
                    )
                }
            }
            is HomeIntent.SelectServer -> {
                _state.value = _state.value.copy(
                    selectedServer = (intent as HomeIntent.SelectServer).server,
                    connectionState = ConnectionState.DISCONNECTED  // Reset connection when server changes
                )
            }
            is HomeIntent.Retry -> {
                _state.value = _state.value.copy(isLoading = true, error = null)
                // TODO: Retry logic
                viewModelScope.launch {
                    delay(1000)
                    _state.value = _state.value.copy(
                        connectionState = ConnectionState.CONNECTED,
                        isLoading = false
                    )
                }
            }
        }
    }
}