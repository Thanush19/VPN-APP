package com.safetunnel.feature.servers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServersViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServersUiState())
    val uiState: StateFlow<ServersUiState> = _uiState

    init {
        loadServers()
    }

    fun loadServers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val servers = serverRepository.getServers()
                // Stub latency: assign a random value between 10 and 200 ms for each server
                val serversWithLatency = servers.map { server ->
                    server.copy(loadPercentage = (10..200).random()) // Reusing loadPercentage for latency stub
                }
                _uiState.update { it.copy(isLoading = false, servers = serversWithLatency) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }

    fun refresh() {
        loadServers()
    }
}

data class ServersUiState(
    val isLoading: Boolean = false,
    val servers: List<ServerResponse> = emptyList(),
    val error: String? = null
)