package com.safetunnel.feature.settings.data

import com.safetunnel.core.common.mvi.BaseState

/**
 * Repository for settings data.
 * In Phase 1, this is a placeholder - actual data sources will be added in later phases.
 */
class SettingsRepository {
    // TODO: Inject data sources (DataStore, etc.) in later phases

    suspend fun getInitialSettings(): SettingsUiState {
        // Placeholder - return initial state
        return SettingsUiState(
            autoConnect = false,
            darkMode = true,
            version = "1.0.0"
        )
    }
}

/**
 * UI State for the Settings screen.
 */
data class SettingsUiState(
    val autoConnect: Boolean,
    val darkMode: Boolean,
    val version: String
) : BaseState

/**
 * Intents (user actions) for the Settings feature.
 */
sealed interface SettingsIntent {
    object LoadSettings : SettingsIntent
    data class ToggleAutoConnect(val enabled: Boolean) : SettingsIntent
    data class ToggleDarkMode(val enabled: Boolean) : SettingsIntent
    object Retry : SettingsIntent
}