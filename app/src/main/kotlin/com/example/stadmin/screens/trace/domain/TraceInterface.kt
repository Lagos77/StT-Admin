package com.example.stadmin.screens.trace.domain

import com.example.stadmin.screens.trace.domain.model.Trace
import kotlinx.coroutines.flow.Flow

interface TraceInterface {
    fun getTraces(): Flow<Result<List<Trace>>>
    fun createTrace(trace: Trace): Flow<Result<Boolean>>
    fun editTrace(trace: Trace): Flow<Result<Boolean>>
    fun deleteTrace(slug: String): Flow<Result<Boolean>>
}