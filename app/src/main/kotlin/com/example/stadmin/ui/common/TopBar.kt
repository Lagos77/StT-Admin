package com.example.stadmin.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.stadmin.R
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing

@Composable
fun TopBar(
    type: TopBarType,
    title: String,
    isEditMode: Boolean = false,
    isSaving: Boolean = false,
    isLoadingTranslation: Boolean = false,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRefresh: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            enabled = !isSaving,
            colors = IconButtonDefaults.iconButtonColors(
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .size(Sizing.thumbnailSmall)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = Shapes.small
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Sizing.iconMedium)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        when (type) {
            TopBarType.LIST -> {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.extraLarge)) {
                    RefreshButton(onClick = onRefresh)
                    AddButton(onClick = onAdd)
                }
            }

            TopBarType.SAVE -> {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.large)) {
                    if (isEditMode) {
                        RefreshTranslationButton(
                            onClick = onRefresh,
                            isLoading = isLoadingTranslation
                        )
                    }
                    SaveButton(onClick = onAdd, isSaving = isSaving)
                }
            }
        }
    }
}

@Composable
private fun AddButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(Sizing.thumbnailSmall)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = Shapes.small
            )
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add trace",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(Sizing.iconMedium)
        )
    }
}

@Composable
private fun RefreshButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(Sizing.thumbnailSmall)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = Shapes.small
            )
    ) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Refresh list",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(Sizing.iconMedium)
        )
    }
}

@Composable
private fun RefreshTranslationButton(onClick: () -> Unit, isLoading: Boolean) {
    Button(
        onClick = onClick,
        shape = Shapes.pill,
        enabled = isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.refresh_language),
            contentDescription = "Refresh translation list",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(Sizing.iconMedium)
        )
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit, isSaving: Boolean) {
    Button(
        onClick = onClick,
        enabled = !isSaving,
        shape = Shapes.pill,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(Sizing.iconMedium),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = Spacing.extraSmall
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Save",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(Sizing.iconMedium)
            )
            /*
            Text(
                text = "Save",
                style = MaterialTheme.typography.labelLarge
            )

             */
        }
    }
}

enum class TopBarType {
    LIST,
    SAVE,
}