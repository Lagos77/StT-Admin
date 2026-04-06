package com.example.stadmin.screens.trace.presentation.components.detail

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.stadmin.screens.trace.presentation.ImageType
import com.example.stadmin.screens.trace.presentation.components.detail.image.ImagePickerSection
import com.example.stadmin.screens.trace.presentation.components.detail.image.ImagePickerType
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Border
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing

@Composable
fun BasicInfoSection(
    title: String,
    slug: String,
    year: String,
    isNt: Boolean,
    imageUrl: String,
    heroImageUrl: String,
    isUploadingImage: Boolean,
    onTitleChanged: (String) -> Unit,
    onSlugChanged: (String) -> Unit,
    onYearChanged: (String) -> Unit,
    onIsNtChanged: (Boolean) -> Unit,
    onImageSelected: (ImageType, Uri) -> Unit,
    onImageDeleted: (ImageType) -> Unit,
) {
    SectionCard(title = "Basic Info") {
        TraceTextField(label = "Title", value = title, onValueChange = onTitleChanged)
        TraceTextField(
            label = "URL Path",
            value = slug,
            onValueChange = onSlugChanged,
            enabled = false
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            TraceTextField(
                label = "Year",
                value = year,
                onValueChange = onYearChanged,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            TestamentDropdown(
                isNt = isNt,
                onIsNtChanged = onIsNtChanged,
                modifier = Modifier.weight(1f)
            )
        }
        ImagePickerSection(
            type = ImagePickerType.CARD,
            imageUrl = imageUrl,
            isUploading = isUploadingImage,
            onImageCropped = { uri -> onImageSelected(ImageType.CARD, uri) },
            onImageDeleted = { onImageDeleted(ImageType.CARD) }
        )
        ImagePickerSection(
            type = ImagePickerType.HERO,
            imageUrl = heroImageUrl,
            isUploading = isUploadingImage,
            onImageCropped = { uri -> onImageSelected(ImageType.HERO, uri) },
            onImageDeleted = { onImageDeleted(ImageType.HERO) }
        )
    }
}

@Composable
private fun TestamentDropdown(
    isNt: Boolean,
    onIsNtChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = "Testament",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(Spacing.extraSmall))
        OutlinedCard(
            onClick = { expanded = true },
            shape = Shapes.small,
            border = BorderStroke(Border.small, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.small, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isNt) "New" else "Old",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(Sizing.iconSmall)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Old Testament") },
                onClick = {
                    onIsNtChanged(false)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("New Testament") },
                onClick = {
                    onIsNtChanged(true)
                    expanded = false
                }
            )
        }
    }
}