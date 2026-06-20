package com.example.stadmin.screens.trace.domain.usecase

import com.example.stadmin.screens.trace.data.TraceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteTraceUseCase @Inject constructor(private val repository: TraceRepository) {
    operator fun invoke(slug: String): Flow<Result<Boolean>> {
        return repository.deleteTrace(slug)
    }
}