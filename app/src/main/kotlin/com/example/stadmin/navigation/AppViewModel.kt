package com.example.stadmin.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadmin.core.crypto.KeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val keyManager: KeyManager
) : ViewModel() {
    private val _currentScreen = MutableStateFlow(NavigationScreen.LOGIN)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }

    fun onSignOut() {
        viewModelScope.launch {
            keyManager.clear()
            _currentScreen.value = NavigationScreen.LOGIN
        }
    }
}