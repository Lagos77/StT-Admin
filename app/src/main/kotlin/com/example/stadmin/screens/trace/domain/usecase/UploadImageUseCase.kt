package com.example.stadmin.screens.trace.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.stadmin.screens.trace.data.ImageRepository
import com.example.stadmin.screens.trace.presentation.ImageType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(private val repository: ImageRepository) {
    operator fun invoke(context: Context, uri: Uri, imageType: ImageType): Flow<Result<String>> {
        return repository.uploadImage(context, uri, imageType)
    }
}