package com.example.stadmin.screens.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadmin.core.crypto.KeyManager
import com.example.stadmin.screens.login.domain.GetAccessKeyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val getAccessKeyUseCase: GetAccessKeyUseCase,
    private val keyManager: KeyManager,
    private val deviceId: String,
) : ViewModel() {

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState> = _viewState.asStateFlow()

    fun onClear() {
        viewModelScope.launch {
            if (keyManager.clear()) {
                _viewState.update { it.copy(isAuthenticated = false, error = "Key cleared") }
            }
        }
    }
    fun onClearAuthenticated() {
        _viewState.update { it.copy(isAuthenticated = false) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null) }
            getAccessKeyUseCase.invoke(deviceId = deviceId).collectLatest { result ->
                result.fold(
                    onSuccess = { device ->
                    keyManager.saveAccessKey(device.accessKey)
                        _viewState.update { it.copy(isLoading = false, isAuthenticated = true) }
                    },
                    onFailure = { error ->
                        _viewState.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }

    data class LoginViewState(
        val isAuthenticated: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null,
    )
}