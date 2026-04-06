package com.example.stadmin.screens.trace.domain

import android.content.Context
import android.net.Uri
import com.example.stadmin.screens.trace.presentation.ImageType
import kotlinx.coroutines.flow.Flow

interface ImageInterface {
    fun uploadImage(context: Context, uri: Uri, imageType: ImageType): Flow<Result<String>>
    fun deleteImage(imageIrl: String): Flow<Result<Boolean>>
}