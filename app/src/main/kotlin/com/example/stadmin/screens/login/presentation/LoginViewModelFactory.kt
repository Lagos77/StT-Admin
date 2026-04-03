package com.example.stadmin.screens.login.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stadmin.core.crypto.KeyManager
import com.example.stadmin.screens.login.data.AuthRepository
import com.example.stadmin.screens.login.domain.GetAccessKeyUseCase

@SuppressLint("HardwareIds")
class LoginViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val keyManager = KeyManager(context)
        val authRepository = AuthRepository()
        val device = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return LoginViewModel(
            keyManager = keyManager,
            getAccessKeyUseCase = GetAccessKeyUseCase(authRepository),
            deviceId = device,
        ) as T
    }
}