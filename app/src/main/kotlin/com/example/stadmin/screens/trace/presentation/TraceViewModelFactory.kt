package com.example.stadmin.screens.trace.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stadmin.core.crypto.KeyManager
import com.example.stadmin.screens.trace.data.ImageRepository
import com.example.stadmin.screens.trace.data.TraceRepository
import com.example.stadmin.screens.trace.domain.usecase.CreateTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.DeleteImageUseCase
import com.example.stadmin.screens.trace.domain.usecase.DeleteTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.EditTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.GetTracesUseCase
import com.example.stadmin.screens.trace.domain.usecase.UploadImageUseCase

class TraceViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val keyManager = KeyManager(context)
        val traceRepository = TraceRepository(keyManager)
        val imageRepository = ImageRepository()
        val getTracesUseCase = GetTracesUseCase(traceRepository)
        val createTraceUseCase = CreateTraceUseCase(traceRepository)
        val editTraceUseCase = EditTraceUseCase(traceRepository)
        val deleteTraceUseCase = DeleteTraceUseCase(traceRepository)
        val uploadImageUseCase = UploadImageUseCase(imageRepository)
        val deleteImageUseCase = DeleteImageUseCase(imageRepository)
        return TraceViewModel(
            getTracesUseCase = getTracesUseCase,
            createTraceUseCase = createTraceUseCase,
            editTraceUseCase = editTraceUseCase,
            deleteTraceUseCase = deleteTraceUseCase,
            uploadImageUseCase = uploadImageUseCase,
            deleteImageUseCase = deleteImageUseCase,
        ) as T
    }
}