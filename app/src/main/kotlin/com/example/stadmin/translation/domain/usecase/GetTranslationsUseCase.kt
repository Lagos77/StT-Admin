package com.example.stadmin.translation.domain.usecase

import com.example.stadmin.translation.data.TraceTranslationRepository
import com.example.stadmin.translation.domain.model.TraceTranslation
import kotlinx.coroutines.flow.Flow

class GetTranslationsUseCase(private val repository: TraceTranslationRepository) {
    operator fun invoke(traceSlug: String): Flow<Result<List<TraceTranslation>>> {
        return repository.getTranslations(traceSlug)
    }
}