package com.example.stadmin.screens.trace.domain.usecase

import com.example.stadmin.screens.trace.data.ImageRepository
import kotlinx.coroutines.flow.Flow

class DeleteImageUseCase(private val repository: ImageRepository) {
    operator fun invoke(imageUrl: String): Flow<Result<Boolean>> {
        return repository.deleteImage(imageUrl)
    }
}