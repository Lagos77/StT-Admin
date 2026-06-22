package com.example.stadmin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.stadmin.screens.dashboard.presentation.DashboardScreen
import com.example.stadmin.screens.login.presentation.screens.LoginScreen
import com.example.stadmin.screens.trace.presentation.screens.TraceDetailScreen
import com.example.stadmin.screens.tracelist.presentation.screens.TraceListScreen
import com.example.stadmin.translation.presentation.screens.TranslationScreen

@Composable
fun NavigationFlow() {
    val viewModel: AppViewModel = hiltViewModel()
    val viewState by viewModel.state.collectAsState()

    when (viewState.currentScreen) {
        NavigationScreen.LOGIN -> LoginScreen(onLoginSuccess = {
            viewModel.navigateTo(
                NavigationScreen.DASHBOARD
            )
        })

        NavigationScreen.DASHBOARD -> DashboardScreen(
            onNavigateToTraces = { viewModel.navigateTo(NavigationScreen.TRACE_LIST) },
            onNavigateToHome = { viewModel.navigateTo(NavigationScreen.HOME_DETAIL) },
            onNavigateToAbout = { viewModel.navigateTo(NavigationScreen.ABOUT_DETAIL) },
            onSignOut = viewModel::onSignOut,
        )

        NavigationScreen.TRACE_LIST -> TraceListScreen(
            onBack = { viewModel.navigateTo(NavigationScreen.DASHBOARD) },
            onNavigateToDetail = { viewModel.navigateTo(NavigationScreen.TRACE_DETAIL) }
        )

        NavigationScreen.TRACE_DETAIL -> TraceDetailScreen(
            onBack = {
                viewModel.navigateTo(
                    NavigationScreen.TRACE_LIST
                )
            },
            onNavigateToTranslation = { viewModel.onNavigateToTranslation(it) },
        )

        NavigationScreen.TRANSLATION -> {
            viewState.selectedTrace?.let {
                TranslationScreen(
                    trace = it,
                    onBack = { viewModel.navigateTo(NavigationScreen.TRACE_DETAIL) })
            }
        }

        NavigationScreen.HOME_DETAIL -> {}
        NavigationScreen.ABOUT_DETAIL -> {}
    }
}