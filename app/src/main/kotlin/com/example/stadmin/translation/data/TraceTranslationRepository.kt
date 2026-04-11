package com.example.stadmin.translation.data

import android.util.Log
import com.example.stadmin.core.crypto.KeyManager
import com.example.stadmin.core.supabase.SupabaseClient
import com.example.stadmin.core.supabase.SupabaseTable
import com.example.stadmin.translation.data.model.TraceTranslationDto
import com.example.stadmin.translation.data.model.toDto
import com.example.stadmin.translation.domain.TraceTranslationInterface
import com.example.stadmin.translation.domain.model.TraceTranslation
import com.example.stadmin.translation.domain.model.toDomain
import com.example.stadmin.util.Constants
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

class TraceTranslationRepository(
    private val keyManager: KeyManager,
) : TraceTranslationInterface {
    private val table = SupabaseClient.from(SupabaseTable.TRACE_TRANSLATIONS)

    override fun getTranslations(traceSlug: String): Flow<Result<List<TraceTranslation>>> {
        return flow {
            try {
                val result = table
                    .select { filter { eq("trace_slug", traceSlug) } }
                    .decodeList<TraceTranslationDto>()
                    .mapNotNull { it.toDomain() }
                emit(Result.success(result))
            } catch (e: Exception) {
                Log.e(
                    Constants.TAG,
                    "TraceTranslationRepository getTranslations() error: ${e.message}"
                )
                emit(Result.failure(e))
            }
        }
    }

    override fun createTranslation(translation: TraceTranslation): Flow<Result<Boolean>> {
        return flow {
            try {
                setAccessKey()
                table.insert(translation.toDto())
                emit(Result.success(true))
            } catch (e: Exception) {
                Log.e(
                    Constants.TAG,
                    "TraceTranslationRepository createTranslation() error: ${e.message}"
                )
                emit(Result.failure(e))
            }
        }
    }

    override fun editTranslation(translation: TraceTranslation): Flow<Result<Boolean>> {
        return flow {
            try {
                setAccessKey()
                table.update(translation.toDto()) {
                    filter { eq("id", translation.id!!) }
                }
                emit(Result.success(true))
            } catch (e: Exception) {
                Log.e(
                    Constants.TAG,
                    "TraceTranslationRepository editTranslation() error: ${e.message}"
                )
                emit(Result.failure(e))
            }
        }
    }

    override fun deleteTranslation(id: String): Flow<Result<Boolean>> {
        return flow {
            try {
                setAccessKey()
                table.delete { filter { eq("id", id) } }
                emit(Result.success(true))
            } catch (e: Exception) {
                Log.e(
                    Constants.TAG,
                    "TraceTranslationRepository deleteTranslation() error: ${e.message}"
                )
                emit(Result.failure(e))
            }
        }
    }

    @Serializable
    data class SetAccessKeyParams(val key: String)

    private suspend fun setAccessKey() {
        val accessKey = keyManager.getAccessKey()
            ?: throw Exception("Not authorized")
        SupabaseClient.client.postgrest.rpc(
            function = "set_app_access_key",
            parameters = SetAccessKeyParams(key = accessKey)
        )
    }
}