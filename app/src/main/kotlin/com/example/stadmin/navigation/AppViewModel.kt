package com.example.stadmin.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadmin.core.crypto.KeyManager
import com.example.stadmin.screens.trace.domain.model.Trace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val keyManager: KeyManager
) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    fun navigateTo(screen: NavigationScreen) {
        _state.update { it.copy(currentScreen = screen, selectedTrace = null) }
    }

    fun onTraceSelected(trace: Trace) {
        _state.update { it.copy(selectedTrace = trace) }
    }

    fun onNavigateToTranslation(trace: Trace) {
        _state.update {
            it.copy(
                selectedTrace = trace,
                currentScreen = NavigationScreen.TRANSLATION
            )
        }
    }

    fun onSignOut() {
        viewModelScope.launch {
            keyManager.clear()
            _state.update { it.copy(currentScreen = NavigationScreen.LOGIN, selectedTrace = null) }
        }
    }

    data class AppState(
        val currentScreen: NavigationScreen = NavigationScreen.LOGIN,
        val selectedTrace: Trace? = null,
    )
}