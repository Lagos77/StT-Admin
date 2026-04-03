package com.example.stadmin.screens.login.data

import com.example.stadmin.core.supabase.SupabaseClient
import com.example.stadmin.screens.login.domain.AuthInterface
import com.example.stadmin.screens.login.domain.model.Device
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository : AuthInterface {
    override fun validateDevice(deviceId: String): Flow<Result<Device>> {
        return flow {
            if (deviceId.isBlank()) emit(Result.failure(Exception("Device ID is blank")))
            try {
                val result = SupabaseClient.client
                    .postgrest["devices"]
                    .select {
                        filter {
                            eq("device_id", "1")
                        }
                    }
                    .decodeList<DeviceDto>()
                    .mapNotNull { it.toDomain() }

                if (result.isEmpty()) {
                    emit(Result.failure(Exception("Device not authorized")))
                } else {
                    emit(Result.success(result.first()))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    private fun DeviceDto.toDomain(): Device? {
        return Device(accessKey = this.accessKey ?: return null)
    }
}