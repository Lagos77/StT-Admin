package com.example.stadmin.screens.login.domain

import com.example.stadmin.screens.login.data.AuthRepository
import com.example.stadmin.screens.login.domain.model.Device
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAccessKeyUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(deviceId: String): Flow<Result<Device>> {
        return authRepository.validateDevice(deviceId)
    }
}