package com.example.stadmin.translation.domain.usecase

import com.example.stadmin.translation.data.TraceTranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteTranslationUseCase @Inject constructor(private val repository: TraceTranslationRepository) {
    operator fun invoke(id: String): Flow<Result<Boolean>> {
        return repository.deleteTranslation(id)
    }
}