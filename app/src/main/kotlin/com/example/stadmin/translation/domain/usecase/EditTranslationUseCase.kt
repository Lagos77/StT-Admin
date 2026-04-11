package com.example.stadmin.translation.domain.usecase

import com.example.stadmin.translation.data.TraceTranslationRepository
import com.example.stadmin.translation.domain.model.TraceTranslation
import kotlinx.coroutines.flow.Flow

class EditTranslationUseCase(private val repository: TraceTranslationRepository) {
    operator fun invoke(translation: TraceTranslation): Flow<Result<Boolean>> {
        return repository.editTranslation(translation)
    }
}