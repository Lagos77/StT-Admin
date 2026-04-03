package com.example.stadmin.screens.trace.domain.usecase

import com.example.stadmin.screens.trace.data.TraceRepository
import com.example.stadmin.screens.trace.domain.model.Trace
import kotlinx.coroutines.flow.Flow

class GetTracesUseCase(private val repository: TraceRepository) {
    operator fun invoke(): Flow<Result<List<Trace>>> {
        return repository.getTraces()
    }
}