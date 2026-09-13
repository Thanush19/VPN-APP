package com.safetunnel.feature.settings.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings feature.
 */
class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(
        autoConnect = false,
        darkMode = true,
        version = "1.0.0"
    ))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadSettings()
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            val settings = repository.getInitialSettings()
            _uiState.value = settings
        }
    }

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadSettings -> loadSettings()
            is SettingsIntent.ToggleAutoConnect -> {
                _uiState.value = _uiState.value.copy(autoConnect = intent.enabled)
            }
            is SettingsIntent.ToggleDarkMode -> {
                _uiState.value = _uiState.value.copy(darkMode = intent.enabled)
            }
            is SettingsIntent.Retry -> loadSettings()
        }
    }
}