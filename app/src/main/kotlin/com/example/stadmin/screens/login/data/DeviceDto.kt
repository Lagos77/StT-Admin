package com.example.stadmin.screens.login.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDto(
    @SerialName("device_id")
    val deviceId: String? = null,
    @SerialName("access_key")
    val accessKey: String? = null,
)
