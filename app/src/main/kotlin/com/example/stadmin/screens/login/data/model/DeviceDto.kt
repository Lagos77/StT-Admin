package com.example.stadmin.screens.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDto(
    @SerialName("device_id")
    val deviceId: String? = null,
    @SerialName("access_key")
    val accessKey: String? = null,
)