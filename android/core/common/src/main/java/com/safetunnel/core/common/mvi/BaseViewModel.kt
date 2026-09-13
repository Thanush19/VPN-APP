package com.safetunnel.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI architecture.
 *
 * @param initialState The initial state of the ViewModel.
 */
abstract class BaseViewModel<S : BaseState>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    protected fun updateState(update: (S) -> S) {
        _state.value = update(_state.value)
    }

    protected fun handleIntent(intent: BaseIntent) {
        // To be overridden by subclasses
    }

    // Clears any ongoing coroutines when the ViewModel is cleared.
    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}