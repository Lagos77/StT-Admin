package com.example.stadmin.screens.trace.domain.usecase

import com.example.stadmin.screens.trace.data.TraceRepository
import com.example.stadmin.screens.trace.domain.model.Trace
import kotlinx.coroutines.flow.Flow

class CreateTraceUseCase(private val repository: TraceRepository) {
    operator fun invoke(trace: Trace): Flow<Result<Boolean>> {
        return repository.createTrace(trace)
    }
}