package com.example.stadmin.screens.trace.presentation.components.detail.image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.screens.trace.presentation.components.list.StatusPill
import com.example.stadmin.ui.Border
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.theme.STAdminTheme
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
fun ImagePickerSection(
    type: ImagePickerType,
    isUploading: Boolean,
    imageUrl: String,
    onImageCropped: (Uri) -> Unit,
    onImageDeleted: () -> Unit,
) {
    val context = LocalContext.current

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == -1) {
            val croppedUri =
                result.data?.let { UCrop.getOutput(it) } ?: return@rememberLauncherForActivityResult
            onImageCropped(croppedUri)
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val destinationUri = Uri.fromFile(
            File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )
        val cropIntent = UCrop.of(uri, destinationUri)
            .withAspectRatio(type.aspectRatioX, type.aspectRatioY)
            .withMaxResultSize(type.maxWidth, type.maxHeight)
            .withOptions(UCrop.Options().apply {
                setCompressionQuality(90)
                setFreeStyleCropEnabled(false)
            })
            .getIntent(context)
        cropLauncher.launch(cropIntent)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(Border.small, MaterialTheme.colorScheme.outline),
        shape = Shapes.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.extraSmall, horizontal = Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Sizing.iconSmall),
                        strokeWidth = Spacing.extraSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    StatusPill(
                        text = if (imageUrl.isBlank()) "No Image" else "Uploaded ✓",
                        isPositive = imageUrl.isNotBlank()
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    if (imageUrl.isNotBlank()) {
                        TextButton(
                            onClick = onImageDeleted,
                            enabled = !isUploading,
                            contentPadding = PaddingValues(all = Spacing.extraSmall)
                        ) {
                            Text(
                                text = "Delete",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            pickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        enabled = !isUploading,
                        contentPadding = PaddingValues(all = Spacing.extraSmall)
                    ) {
                        Text(
                            text = if (imageUrl.isBlank()) "Select Image" else "Change Image",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        ImagePickerSection(
            type = ImagePickerType.CARD,
            isUploading = false,
            imageUrl = "test",
            onImageCropped = {},
            onImageDeleted = {},
        )
    }
}