package com.example.stadmin.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {
    private val _currentScreen = MutableStateFlow(NavigationScreen.LOGIN)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }
}