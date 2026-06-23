package com.example.stadmin.translation.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.stadmin.screens.trace.domain.TraceEra
import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.translation.presentation.TranslationLanguage
import com.example.stadmin.translation.presentation.TranslationViewModel
import com.example.stadmin.ui.Border
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.buttons.ContainerColor
import com.example.stadmin.ui.buttons.CustomizedButton
import com.example.stadmin.ui.buttons.IconType
import com.example.stadmin.ui.common.BasicCustomTextField
import com.example.stadmin.ui.common.BibleBookSelector
import com.example.stadmin.ui.theme.STAdminTheme
import com.example.stadmin.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationScreen(
    trace: Trace,
    onBack: () -> Unit,
) {
    val viewModel: TranslationViewModel = hiltViewModel()
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initiate(trace)
    }

    when {
        viewState.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        else -> {
            TranslationScreenContent(
                trace = trace,
                viewState = viewState,
                onBack = onBack,
                onLanguageChange = viewModel::onLanguageChange,
                onTitleChanged = viewModel::onTitleChanged,
                onDescriptionChanged = viewModel::onDescriptionChanged,
                onContentChanged = viewModel::onContentChanged,
                onPassageChanged = viewModel::onPassageChanged,
                onTranslate = viewModel::translate,
                onTranslateRemainingContent = viewModel::translateRemainingContent,
                onSave = { viewModel.saveTranslation(trace.slug) },
                onDelete = {
                    viewState.selectedTranslation?.id?.let {
                        viewModel.deleteTranslation(
                            it,
                            trace.slug
                        )
                    }
                },
                onSnackBarMessageConsumed = viewModel::onSnackBarMessageConsumed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TranslationScreenContent(
    trace: Trace,
    viewState: TranslationViewModel.TranslationViewState,
    onBack: () -> Unit,
    onLanguageChange: (TranslationLanguage) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onContentChanged: (Int, String) -> Unit,
    onPassageChanged: (Int, Passage) -> Unit,
    onTranslate: (String, String, (String) -> Unit) -> Unit,
    onTranslateRemainingContent: (Int, String, List<String>) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onSnackBarMessageConsumed: () -> Unit,
) {

    val snackBarHostState = remember { SnackbarHostState() }
    var showFullContentText by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var contentStep by remember { mutableIntStateOf(0) }
    var passageStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(viewState.snackBarMessage) {
        viewState.snackBarMessage?.let {
            snackBarHostState.showSnackbar(it)
            onSnackBarMessageConsumed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Row(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .fillMaxWidth()
                    .padding(Spacing.verySmall)
            ) {
                CustomizedButton(
                    label = trace.title,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    containerColor = ContainerColor.WHITE,
                    onClick = onBack
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(
                selectedTabIndex = TranslationLanguage.entries.indexOf(viewState.language),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                TranslationLanguage.entries.forEach { language ->
                    Tab(
                        selected = viewState.language == language,
                        onClick = { onLanguageChange(language) },
                        text = {
                            Text(
                                text = languageName(language),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = Spacing.small, horizontal = Spacing.small),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                StackedField(
                    label = "Title",
                    placeHolder = "Enter title translation...",
                    currentLanguage = viewState.language,
                    sourceText = trace.title,
                    targetValue = viewState.titleDraft,
                    onTargetChange = onTitleChanged,
                    onTranslate = {
                        onTranslate(
                            trace.title,
                            viewState.language.code,
                            onTitleChanged
                        )
                    },
                )
                if (!trace.description.isNullOrBlank()) {
                    StackedField(
                        label = "Description",
                        placeHolder = "Enter description translation...",
                        currentLanguage = viewState.language,
                        sourceText = trace.description,
                        targetValue = viewState.descriptionDraft,
                        onTargetChange = onDescriptionChanged,
                        onTranslate = {
                            onTranslate(
                                trace.description,
                                viewState.language.code,
                                onDescriptionChanged
                            )
                        }
                    )
                }

                if (!trace.content.isNullOrEmpty()) {
                    val contentSize = trace.content.size
                    val currentSource = trace.content.getOrElse(contentStep) { "" }
                    val currentTarget = viewState.contentDraft.getOrElse(contentStep) { "" }

                    StepperCard(
                        label = "Content",
                        step = contentStep,
                        total = contentSize,
                        translatedCount = viewState.contentDraft.count { it.isNotBlank() },
                        onPrevious = { if (contentStep > 0) contentStep-- },
                        onNext = { if (contentStep < contentSize - 1) contentStep++ }
                    ) {
                        Column(modifier = Modifier.padding(Spacing.small)) {
                            Text(
                                text = "English",
                                modifier = Modifier.padding(bottom = Spacing.small),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Text(
                                text = currentSource,
                                modifier = Modifier
                                    .padding(bottom = Spacing.small)
                                    .clickable { showFullContentText = true },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )

                            if (showFullContentText) {
                                ModalBottomSheet(
                                    sheetState = sheetState,
                                    containerColor = MaterialTheme.colorScheme.background,
                                    onDismissRequest = { showFullContentText = false }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = Spacing.small),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        SelectionContainer {
                                            Text(
                                                text = currentSource,
                                                modifier = Modifier.padding(Spacing.medium),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onBackground,
                                            )
                                        }

                                        CustomizedButton(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = Spacing.small),
                                            label = "Close",
                                            icon = null,
                                            containerColor = ContainerColor.WHITE,
                                            onClick = { showFullContentText = false }
                                        )
                                    }
                                }
                            }

                            BasicCustomTextField(
                                label = languageName(viewState.language),
                                modifier = Modifier.fillMaxWidth(),
                                placeHolder = "Not translated yet…",
                                value = currentTarget,
                                onValueChange = { onContentChanged(contentStep, it) },
                                minLines = 3,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                Spacing.small,
                                Alignment.End
                            )
                        ) {
                            CustomizedButton(
                                label = "Translate this",
                                icon = Icons.Filled.Translate,
                                containerColor = ContainerColor.LIGHT_BROWN,
                                onClick = {
                                    onTranslate(
                                        currentSource,
                                        viewState.language.code
                                    ) { translated ->
                                        onContentChanged(contentStep, translated)
                                    }
                                }
                            )
                            CustomizedButton(
                                label = "Translate rest",
                                icon = Icons.Filled.Translate,
                                containerColor = ContainerColor.LIGHT_BROWN,
                                onClick = {
                                    onTranslateRemainingContent(
                                        contentStep,
                                        viewState.language.code,
                                        trace.content
                                    )
                                }
                            )
                        }
                    }
                }

                if (!trace.passages.isNullOrEmpty()) {
                    val passagesSize = trace.passages.size
                    val currentPassageDraft = viewState.passagesDraft.getOrElse(passageStep) {
                        Passage("", null, null, null, "", "")
                    }

                    StepperCard(
                        label = "Passages",
                        step = passageStep,
                        total = passagesSize,
                        translatedCount = viewState.passagesDraft.count { it.book.isNotBlank() },
                        onPrevious = { if (passageStep > 0) passageStep-- },
                        onNext = { if (passageStep < passagesSize - 1) passageStep++ },
                    ) {
                        currentPassageDraft.let { passage ->
                            Text(
                                text = getBasePassage(passage),
                                modifier = Modifier.padding(
                                    horizontal = Spacing.small,
                                    vertical = Spacing.small
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                            )
                            HorizontalDivider(modifier = Modifier.background(color = MaterialTheme.colorScheme.outline))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.small)
                                .padding(top = Spacing.verySmall),
                        ) {
                            BibleBookSelector(
                                modifier = Modifier.weight(1f),
                                selectedBook = currentPassageDraft.book,
                                language = viewState.language,
                                onBookSelected = {
                                    onPassageChanged(
                                        passageStep,
                                        currentPassageDraft.copy(book = it)
                                    )
                                },
                            )
                            BasicCustomTextField(
                                label = "Version",
                                placeHolder = "",
                                enabled = false,
                                value = bibleVersion(viewState.language),
                                onValueChange = {},
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.small)
                                .padding(top = Spacing.small),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BasicCustomTextField(
                                label = "Chapter",
                                placeHolder = "",
                                value = currentPassageDraft.chapter?.toString() ?: "",
                                onValueChange = { newValue ->
                                    onPassageChanged(
                                        passageStep,
                                        currentPassageDraft.copy(chapter = newValue.toIntOrNull())
                                    )
                                },
                            )

                            BasicCustomTextField(
                                label = "Verse start",
                                placeHolder = "1",
                                value = currentPassageDraft.verseStart?.toString() ?: "",
                                onValueChange = { newValue ->
                                    onPassageChanged(
                                        passageStep,
                                        currentPassageDraft.copy(verseStart = newValue.toIntOrNull())
                                    )
                                },
                                keyboardType = KeyboardType.Number,
                            )
                            BasicCustomTextField(
                                label = "Verse end",
                                placeHolder = "2",
                                value = currentPassageDraft.verseEnd?.toString() ?: "",
                                onValueChange = { newValue ->
                                    onPassageChanged(
                                        passageStep,
                                        currentPassageDraft.copy(verseEnd = newValue.toIntOrNull())
                                    )
                                },
                                keyboardType = KeyboardType.Number,
                            )
                        }
                        Column(modifier = Modifier.padding(horizontal = Spacing.small)) {
                            BasicCustomTextField(
                                label = "",
                                placeHolder = "Add bible passage here..",
                                modifier = Modifier.fillMaxWidth(),
                                value = currentPassageDraft.text,
                                onValueChange = {
                                    onPassageChanged(
                                        passageStep,
                                        currentPassageDraft.copy(text = it)
                                    )
                                },
                                minLines = 3,
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Spacing.verySmall),
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = Spacing.small,
                                    alignment = Alignment.End
                                )
                            ) {
                                CustomizedButton(
                                    label = "Paste",
                                    icon = Icons.Default.ContentPaste,
                                    containerColor = ContainerColor.LIGHT_BROWN,
                                    onClick = {},
                                )
                                CustomizedButton(
                                    label = "Clear",
                                    icon = Icons.Default.ClearAll,
                                    containerColor = ContainerColor.LIGHT_BROWN,
                                    onClick = {},
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                CustomizedButton(
                    modifier = Modifier.weight(1f),
                    label = "Save ${languageName(viewState.language)}",
                    icon = Icons.Default.Language,
                    isLoading = viewState.isSaving,
                    containerColor = ContainerColor.WHITE,
                    onClick = onSave
                )
                CustomizedButton(
                    label = null,
                    icon = Icons.Default.Delete,
                    isEnabled = !viewState.isSaving,
                    iconColorType = IconType.ERROR,
                    containerColor = ContainerColor.WHITE,
                    onClick = onDelete
                )
            }

        }
    }
}

@Composable
private fun StackedField(
    label: String,
    placeHolder: String,
    currentLanguage: TranslationLanguage,
    sourceText: String,
    targetValue: String,
    onTargetChange: (String) -> Unit,
    onTranslate: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = Shapes.small,
        border = BorderStroke(Border.small, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )

                StatusIndicator(isEdited = targetValue.isNotBlank())
            }
            HorizontalDivider(modifier = Modifier.background(color = MaterialTheme.colorScheme.outline))

            Column(
                modifier = Modifier.padding(
                    vertical = Spacing.verySmall,
                    horizontal = Spacing.small
                )
            ) {
                Text(
                    text = "English",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = sourceText,
                    modifier = Modifier.padding(vertical = Spacing.verySmall),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            HorizontalDivider(modifier = Modifier.background(color = MaterialTheme.colorScheme.outline))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        vertical = Spacing.small,
                        horizontal = Spacing.small
                    ),
            ) {
                BasicCustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = languageName(language = currentLanguage),
                    placeHolder = placeHolder,
                    value = targetValue,
                    onValueChange = onTargetChange,
                    minLines = 1,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    CustomizedButton(
                        label = if (targetValue.isBlank()) "Translate" else "Re-translate",
                        icon = Icons.Filled.Translate,
                        containerColor = ContainerColor.LIGHT_BROWN,
                        onClick = onTranslate
                    )
                }
            }

        }
    }
}

@Composable
private fun StepperCard(
    label: String,
    step: Int,
    total: Int,
    translatedCount: Int = 0,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = Shapes.small,
        border = BorderStroke(Border.small, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                CustomizedButton(
                    label = null,
                    isEnabled = step > 0,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    containerColor = ContainerColor.LIGHT_BROWN,
                    onClick = onPrevious,
                )
                Text(
                    text = "${step + 1} / $total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                CustomizedButton(
                    label = null,
                    isEnabled = step < total - 1,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    containerColor = ContainerColor.LIGHT_BROWN,
                    onClick = onNext,
                )
            }
        }
        HorizontalDivider(modifier = Modifier.background(color = MaterialTheme.colorScheme.outline))
        Row(
            modifier = Modifier.padding(Spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                modifier = Modifier.weight(1f),
                progress = { (translatedCount.toFloat() / total.toFloat()) },
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
            )
            Text(
                text = "$translatedCount of $total done",
                modifier = Modifier.padding(start = Spacing.small),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        HorizontalDivider(modifier = Modifier.background(color = MaterialTheme.colorScheme.outline))
        content()
    }
}

@Composable
private fun StatusIndicator(isEdited: Boolean) {
    if (isEdited) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall)) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(Sizing.iconSmall)
            )
            Text(
                text = "Done",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Sizing.iconSmall)
            )
            Text(
                text = "Missing",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

private fun getBasePassage(passage: Passage): String {
    return if (passage.verseEnd != null) {
        "English: ${passage.book} · ${passage.chapter}:${passage.verseStart}–${passage.verseEnd} · ${passage.version}"
    } else {
        "English: ${passage.book} · ${passage.chapter}:${passage.verseStart} · ${passage.version}"
    }
}

private fun languageName(language: TranslationLanguage): String = when (language) {
    TranslationLanguage.ES -> "Español"
    TranslationLanguage.PT -> "Português"
    TranslationLanguage.SV -> "Svenska"
}

private fun bibleVersion(language: TranslationLanguage): String {
    return when (language) {
        TranslationLanguage.ES -> Constants.Versions.REINA_VALERA_1960
        TranslationLanguage.PT -> Constants.Versions.NOVA_VERSAO_INTERNACIONAL
        TranslationLanguage.SV -> Constants.Versions.SVENSKA_FOLKBIBELN_1998
    }
}

@Preview(showBackground = true)
@Composable
private fun TranslationScreenPreview() {
    STAdminTheme {
        TranslationScreenContent(
            trace = Trace(
                id = 1,
                slug = "noahs-ark",
                title = "Noah's Ark",
                description = null,
                //description = "A description",
                year = -2500,
                era = TraceEra.OT,
                imageUrl = null,
                heroImageUrl = null,
                latitude = null,
                longitude = null,
                content = null,
                //content = listOf("At the summit of Mount Everest, the highest point on Earth at 8,848 meters above sea level, climbers plant their flags over something remarkable. Beneath their feet is not granite or volcanic rock — it is limestone made of ancient sea-floor sediments, packed with the fossilized remains of creatures that once lived at the bottom of the ocean. This is not a fringe claim. It appears in Holt Modern Earth Science, a mainstream secular school textbook published in 1983, which states plainly that climbers at the top of the world stand over the remains of animals that once lived in the sea."),
                passages = listOf(
                    Passage(
                        book = "Genesis",
                        chapter = 6,
                        verseStart = 14,
                        verseEnd = 16,
                        text = "Make thee an ark of gopher wood; rooms shalt thou make in the ark.",
                        version = "KJV"
                    ),
                    Passage(
                        book = "Genesis",
                        chapter = 7,
                        verseStart = 1,
                        verseEnd = 5,
                        text = "And the Lord said unto Noah, Come thou and all thy house into the ark.",
                        version = "KJV"
                    )
                ),
                videos = null,
                sources = null,
                published = true,
                createdAt = null,
                updatedAt = null,
            ),
            viewState = TranslationViewModel.TranslationViewState(),
            onBack = {},
            onLanguageChange = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onContentChanged = { _, _ -> },
            onPassageChanged = { _, _ -> },
            onTranslate = { _, _, _ -> },
            onTranslateRemainingContent = { _, _, _ -> },
            onSave = {},
            onDelete = {},
            onSnackBarMessageConsumed = {},
        )
    }
}