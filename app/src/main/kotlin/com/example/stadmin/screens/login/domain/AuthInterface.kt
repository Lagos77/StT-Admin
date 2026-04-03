package com.example.stadmin.screens.login.domain

import com.example.stadmin.screens.login.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface AuthInterface {
    fun validateDevice(deviceId: String): Flow<Result<Device>>
}