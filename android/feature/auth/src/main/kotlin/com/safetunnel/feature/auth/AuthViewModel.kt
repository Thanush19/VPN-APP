package com.safetunnel.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> get() = _state

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                val resp = repository.register(AuthRequest(email, password))
                // Save tokens
                tokenDataStore.saveTokens(resp.accessToken, resp.refreshToken)
                _state.value = _state.value.copy(
                    isLoading = false,
                    token = resp.accessToken,
                    refreshToken = resp.refreshToken,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val resp = repository.login(AuthRequest(email, password))
                // Save tokens
                tokenDataStore.saveTokens(resp.accessToken, resp.refreshToken)
                _state.value = _state.value.copy(
                    isLoading = false,
                    token = resp.accessToken,
                    refreshToken = resp.refreshToken,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val currentRefresh = tokenDataStore.getRefreshToken() ?: return@launch
                val resp = repository.refresh(RefreshTokenRequest(currentRefresh))
                // Save new tokens
                tokenDataStore.saveTokens(resp.accessToken, resp.refreshToken)
                _state.value = _state.value.copy(
                    token = resp.accessToken,
                    refreshToken = resp.refreshToken
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    // Load tokens on initialization (optional)
    init {
        viewModelScope.launch {
            val accessToken = tokenDataStore.getAccessToken()
            val refreshToken = tokenDataStore.getRefreshToken()
            if (accessToken != null && refreshToken != null) {
                _state.value = _state.value.copy(
                    token = accessToken,
                    refreshToken = refreshToken,
                    isSuccess = true // Assume valid; could verify with backend
                )
            }
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val refreshToken: String? = null,
    val isSuccess: Boolean = false,
    val error: String? = null
)