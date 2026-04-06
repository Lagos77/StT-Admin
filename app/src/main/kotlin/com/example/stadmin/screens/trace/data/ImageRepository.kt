package com.example.stadmin.screens.trace.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.stadmin.core.supabase.SupabaseClient
import com.example.stadmin.screens.trace.domain.ImageInterface
import com.example.stadmin.screens.trace.presentation.ImageType
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val IMAGE_CARD = "image-card"
private const val IMAGES = "images"

class ImageRepository : ImageInterface {

    private val storage = SupabaseClient.client.storage

    override fun uploadImage(
        context: Context,
        uri: Uri,
        imageType: ImageType,
    ): Flow<Result<String>> {
        return flow {
            try {
                val bucket = when (imageType) {
                    ImageType.CARD -> IMAGE_CARD
                    ImageType.HERO -> IMAGES
                }
                val fileName = "${imageType.name.lowercase()}_${System.currentTimeMillis()}.jpg"
                val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                    ?: throw Exception("Failed to read image")

                storage[bucket].upload(fileName, bytes)
                val publicUrl = storage[bucket].publicUrl(fileName)
                emit(Result.success(publicUrl))
            } catch (e: Exception) {
                Log.e("Failed to upload image:", "${e.message}")
                emit(Result.failure(e))
            }
        }
    }

    override fun deleteImage(imageIrl: String): Flow<Result<Boolean>> {
        return flow {
            try {
                val uri = imageIrl.toUri()
                val pathSegments = uri.pathSegments
                val bucket = pathSegments[pathSegments.indexOf("public") + 1]
                val fileName = pathSegments.last()

                storage[bucket].delete(listOf(fileName))
                emit(Result.success(true))
            } catch (e: Exception) {
                Log.e("Failed to delete image:", "${e.message}")
                emit(Result.failure(e))
            }
        }
    }
}