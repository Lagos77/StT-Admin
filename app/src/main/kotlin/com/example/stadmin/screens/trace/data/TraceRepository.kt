package com.example.stadmin.screens.trace.data

import android.util.Log
import com.example.stadmin.core.crypto.KeyManager
import com.example.stadmin.core.supabase.SupabaseClient
import com.example.stadmin.core.supabase.SupabaseTable
import com.example.stadmin.screens.trace.data.model.TraceDto
import com.example.stadmin.screens.trace.data.model.toDomain
import com.example.stadmin.screens.trace.domain.TraceInterface
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.screens.trace.domain.model.toDto
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.MDC.put

private const val SET_CONFIG_FUNCTION = "set_config"
private const val ACCESS_KEY_SETTING = "app.access_key"
private const val FILTER_KEY = "slug"

class TraceRepository(
    private val keyManager: KeyManager,
) : TraceInterface {
    private val table = SupabaseClient.from(SupabaseTable.TRACES)

    override fun getTraces(): Flow<Result<List<Trace>>> {
        return flow {
            try {
                val result = table
                    .select()
                    .decodeList<TraceDto>()
                    .mapNotNull { it.toDomain() }
                emit(Result.success(result))
            } catch (e: Exception) {
                Log.e("TraceRepository getTraces()", "Error: ${e.message}")
                emit(Result.failure(e))
            }
        }
    }

    override fun createTrace(trace: Trace): Flow<Result<Boolean>> {
        return flow {
            try {
                setAccessKey()
                table.insert(trace.toDto())
                emit(Result.success(true))
            } catch (e: Exception) {
                Log.e("TraceRepository createTrace()", "Error: ${e.message}")
                emit(Result.failure(e))
            }
        }
    }

    override fun editTrace(trace: Trace): Flow<Result<Boolean>> {
        return flow {
            try {
                setAccessKey()
                table.update(trace.toDto()) {
                    filter { eq(FILTER_KEY, trace.slug) }
                }
                emit(Result.success(true))
            } catch (e: Exception) {
                Log.e("TraceRepository editTrace()", "Error: ${e.message}")
                emit(Result.failure(e))
            }
        }
    }

    override fun deleteTrace(slug: String): Flow<Result<Boolean>> {
        return flow {
            try {
                setAccessKey()
                table.delete {
                    filter { eq(FILTER_KEY, slug) }
                }
                emit(Result.success(true))
            } catch (e: Exception) {
                Log.e("TraceRepository deleteTrace()", "Error: ${e.message}")
                emit(Result.failure(e))
            }
        }
    }

    private suspend fun setAccessKey() {
        val accessKey = keyManager.getAccessKey()
            ?: throw Exception("Not authorized")

        SupabaseClient.client.postgrest.rpc(
            SET_CONFIG_FUNCTION,
            buildJsonObject {
                put("setting_name", ACCESS_KEY_SETTING)
                put("new_value", accessKey)
                put("is_local", "true")
            }
        )
    }
}