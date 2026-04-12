package com.example.stadmin.screens.trace.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stadmin.R
import com.example.stadmin.screens.trace.presentation.TraceViewModel
import com.example.stadmin.screens.trace.presentation.TraceViewModelFactory
import com.example.stadmin.screens.trace.presentation.components.detail.BasicInfoSection
import com.example.stadmin.screens.trace.presentation.components.detail.ContentSection
import com.example.stadmin.screens.trace.presentation.components.detail.DescriptionSection
import com.example.stadmin.screens.trace.presentation.components.detail.LocationSection
import com.example.stadmin.screens.trace.presentation.components.detail.PassagesSection
import com.example.stadmin.screens.trace.presentation.components.detail.PublishedToggleSection
import com.example.stadmin.screens.trace.presentation.components.detail.SourcesSection
import com.example.stadmin.screens.trace.presentation.components.detail.video.VideosSection
import com.example.stadmin.screens.trace.presentation.toTrace
import com.example.stadmin.translation.presentation.screens.TranslationBottomSheet
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.common.TopBar
import com.example.stadmin.ui.common.TopBarType
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun TraceDetailScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: TraceViewModel = viewModel(factory = TraceViewModelFactory(context))
    val viewState by viewModel.viewState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val isEditMode = viewState.selectedTrace != null

    LaunchedEffect(viewState.snackBarMessage) {
        viewState.snackBarMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.onSnackBarMessageConsumed()
        }
    }

    LaunchedEffect(viewState.isFirstTimeCreated) {
        if (viewState.isFirstTimeCreated) {
            viewModel.onFirstTimeCreatedConsumed()
            onBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopBar(
                type = TopBarType.SAVE,
                title = if (isEditMode) {
                    stringResource(R.string.trace_details_edit_title)
                } else {
                    stringResource(R.string.trace_details_new_title)
                },
                isEditMode = isEditMode,
                isSaving = viewState.isSaving,
                onBack = { viewModel.onDiscardChanges(onBack) },
                onAdd = { viewModel.saveTrace(trace = viewState.toTrace()) },
                onRefresh = { viewModel.getTranslation(viewState.selectedTrace?.slug ?: "") }
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
                    era = viewState.era,
                    imageUrl = viewState.imageUrl,
                    heroImageUrl = viewState.heroImageUrl,
                    isUploadingImage = viewState.isUploadingImage,
                    onTitleChanged = viewModel::onTitleChanged,
                    onSlugChanged = viewModel::onSlugChanged,
                    onYearChanged = viewModel::onYearChanged,
                    onEraChanged = viewModel::onEraChanged,
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
                PassagesSection(
                    passages = viewState.passages,
                    onPassagesChanged = viewModel::onPassagesChanged
                )
            }
            item {
                ContentSection(
                    content = viewState.content,
                    onContentChanged = viewModel::onContentChanged
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
            if (isEditMode) {
                item {
                    OutlinedButton(
                        onClick = viewModel::onShowTranslationSheet,
                        enabled = !viewState.isLoadingTranslations,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.extraSmall))
                        Text("Manage translations")
                    }
                }
            }
        }
    }
    if (viewState.showTranslationSheet) {
        viewState.selectedTrace?.let { trace ->
            TranslationBottomSheet(
                baseTrace = trace,
                viewState = viewState,
                onLanguageChanged = viewModel::onLanguageChanged,
                onTitleChanged = viewModel::onTitleTranslatedChanged,
                onDescriptionChanged = viewModel::onDescriptionTranslatedChanged,
                onContentChanged = viewModel::onContentTranslatedChanged,
                onPassagesChanged = viewModel::onPassagesTranslatedChanged,
                onVideosChanged = viewModel::onVideosTranslatedChanged,
                onSaveSuccessConsumed = viewModel::onTranslationSaveSuccessConsumed,
                onNewTranslation = viewModel::onNewTranslation,
                onTranslationSelected = viewModel::onTranslationSelected,
                onSave = { viewModel.saveTranslation(it) },
                onDelete = { viewModel.deleteTranslation(it, trace.slug) },
                onDismiss = {
                    viewModel.onHideTranslationSheet()
                    viewModel.onNewTranslation()
                }
            )
        }
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        TraceDetailScreen(
            onBack = {},
        )
    }
}