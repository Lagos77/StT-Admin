package com.example.stadmin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stadmin.screens.dashboard.presentation.DashboardScreen
import com.example.stadmin.screens.login.presentation.screens.LoginScreen

@Composable
fun NavigationFlow(viewModel: AppViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when(currentScreen) {
        NavigationScreen.LOGIN -> LoginScreen(onLoginSuccess = { viewModel.navigateTo(NavigationScreen.DASHBOARD) })
        NavigationScreen.DASHBOARD -> DashboardScreen(
            onNavigateToTraces = { viewModel.navigateTo(NavigationScreen.TRACE_LIST)},
            onNavigateToHome = { viewModel.navigateTo(NavigationScreen.HOME_DETAIL)},
            onNavigateToAbout = { viewModel.navigateTo(NavigationScreen.ABOUT_DETAIL)},
            onSignOut = { viewModel.navigateTo(NavigationScreen.LOGIN)},
        )
        NavigationScreen.TRACE_LIST -> TODO()
        NavigationScreen.TRACE_DETAIL -> TODO()
        NavigationScreen.HOME_DETAIL -> TODO()
        NavigationScreen.ABOUT_DETAIL -> TODO()
    }
}