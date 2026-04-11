package com.example.stadmin.translation.domain

import com.example.stadmin.translation.domain.model.TraceTranslation
import kotlinx.coroutines.flow.Flow

interface TraceTranslationInterface {
    fun getTranslations(traceSlug: String): Flow<Result<List<TraceTranslation>>>
    fun createTranslation(translation: TraceTranslation): Flow<Result<Boolean>>
    fun editTranslation(translation: TraceTranslation): Flow<Result<Boolean>>
    fun deleteTranslation(id: String): Flow<Result<Boolean>>
}