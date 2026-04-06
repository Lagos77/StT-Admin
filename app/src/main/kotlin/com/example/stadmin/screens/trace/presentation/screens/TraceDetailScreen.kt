package com.example.stadmin.screens.trace.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.screens.trace.presentation.TraceViewModel
import com.example.stadmin.screens.trace.presentation.TraceViewModel.TraceViewState
import com.example.stadmin.screens.trace.presentation.TraceViewModelFactory
import com.example.stadmin.screens.trace.presentation.components.detail.BasicInfoSection
import com.example.stadmin.screens.trace.presentation.components.detail.ContentSection
import com.example.stadmin.screens.trace.presentation.components.detail.DescriptionSection
import com.example.stadmin.screens.trace.presentation.components.detail.LocationSection
import com.example.stadmin.screens.trace.presentation.components.detail.PassagesSection
import com.example.stadmin.screens.trace.presentation.components.detail.PublishedToggleSection
import com.example.stadmin.screens.trace.presentation.components.detail.SourcesSection
import com.example.stadmin.screens.trace.presentation.components.detail.TraceDetailTopBar
import com.example.stadmin.screens.trace.presentation.components.detail.VideosSection
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun TraceDetailScreen(
    onBack: () -> Unit,
    onSaveTrace: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: TraceViewModel = viewModel(factory = TraceViewModelFactory(context))
    val viewState by viewModel.viewState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val isEditMode = viewState.selectedTrace != null

    LaunchedEffect(viewState.error) {
        viewState.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.onErrorConsumed()
        }
    }

    LaunchedEffect(viewState.saveSuccess) {
        if (viewState.saveSuccess) {
            onSaveTrace()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TraceDetailTopBar(
                title = if (isEditMode) "Edit Trace" else "New Trace",
                isSaving = viewState.isSaving,
                onBack = { viewModel.onDiscardChanges(onBack) },
                onSave = { viewModel.saveTrace(trace =  viewState.toTrace()) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            contentPadding = PaddingValues(vertical = Spacing.medium)
        ) {
            item {
                PublishedToggleSection(
                    published = viewState.published,
                    onPublishedChanged = viewModel::onPublishedChanged
                )
            }
            item {
                BasicInfoSection(
                    title = viewState.title,
                    slug = viewState.slug,
                    year = viewState.year,
                    isNt = viewState.isNt,
                    imageUrl = viewState.imageUrl,
                    heroImageUrl = viewState.heroImageUrl,
                    isUploadingImage = viewState.isUploadingImage,
                    onTitleChanged = viewModel::onTitleChanged,
                    onSlugChanged = viewModel::onSlugChanged,
                    onYearChanged = viewModel::onYearChanged,
                    onIsNtChanged = viewModel::onIsNtChanged,
                    onImageSelected = { imageType, uri ->
                        viewModel.onImageSelected(
                            context = context, imageType = imageType, uri = uri
                        )
                    },
                    onImageDeleted = { viewModel.onImageDeleted(imageType = it) }
                )
            }
            item {
                DescriptionSection(
                    description = viewState.description,
                    onDescriptionChanged = viewModel::onDescriptionChanged
                )
            }
            item {
                LocationSection(
                    latitude = viewState.latitude,
                    longitude = viewState.longitude,
                    onLatitudeChanged = viewModel::onLatitudeChanged,
                    onLongitudeChanged = viewModel::onLongitudeChanged
                )
            }
            item {
                ContentSection(
                    content = viewState.content,
                    onContentChanged = viewModel::onContentChanged
                )
            }
            item {
                PassagesSection(
                    passages = viewState.passages,
                    onPassagesChanged = viewModel::onPassagesChanged
                )
            }
            item {
                VideosSection(
                    videos = viewState.videos,
                    onVideosChanged = viewModel::onVideosChanged
                )
            }
            item {
                SourcesSection(
                    sources = viewState.sources,
                    onSourcesChanged = viewModel::onSourcesChanged
                )
            }
        }
    }
}

private fun TraceViewState.toTrace(): Trace = Trace(
    id = selectedTrace?.id ?: 0,
    slug = slug,
    title = title,
    description = description.ifBlank { null },
    year = year.toIntOrNull(),
    isNt = isNt,
    imageUrl = imageUrl.ifBlank { null },
    heroImageUrl = heroImageUrl.ifBlank { null },
    latitude = latitude.toDoubleOrNull(),
    longitude = longitude.toDoubleOrNull(),
    content = content.filter { it.isNotBlank() },
    passages = passages,
    videos = videos,
    sources = sources,
    published = published,
    createdAt = selectedTrace?.createdAt,
    updatedAt = selectedTrace?.updatedAt
)

@Composable
fun TraceTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    enabled: Boolean = true,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(Spacing.extraSmall))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = Shapes.small,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                disabledBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        TraceDetailScreen(
            onBack = {},
            onSaveTrace = {}
        )
    }
}