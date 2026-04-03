package com.example.stadmin.screens.trace.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stadmin.core.crypto.KeyManager
import com.example.stadmin.screens.trace.data.TraceRepository
import com.example.stadmin.screens.trace.domain.usecase.CreateTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.DeleteTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.EditTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.GetTracesUseCase

class TraceViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val keyManager = KeyManager(context)
        val traceRepository = TraceRepository(keyManager)
        val getTracesUseCase = GetTracesUseCase(traceRepository)
        val createTraceUseCase = CreateTraceUseCase(traceRepository)
        val editTraceUseCase = EditTraceUseCase(traceRepository)
        val deleteTraceUseCase = DeleteTraceUseCase(traceRepository)
        return TraceViewModel(
            getTracesUseCase,
            createTraceUseCase,
            editTraceUseCase,
            deleteTraceUseCase
        ) as T
    }
}