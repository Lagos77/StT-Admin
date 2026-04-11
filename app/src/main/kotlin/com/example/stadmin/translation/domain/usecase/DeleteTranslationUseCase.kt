package com.example.stadmin.translation.domain.usecase

import com.example.stadmin.translation.data.TraceTranslationRepository
import kotlinx.coroutines.flow.Flow

class DeleteTranslationUseCase(private val repository: TraceTranslationRepository) {
    operator fun invoke(id: String): Flow<Result<Boolean>> {
        return repository.deleteTranslation(id)
    }
}