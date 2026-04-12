package com.example.stadmin.translation.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.screens.trace.domain.TraceEra
import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.screens.trace.presentation.TraceViewState
import com.example.stadmin.screens.trace.presentation.components.detail.BibleBookSelector
import com.example.stadmin.translation.domain.model.TraceTranslation
import com.example.stadmin.translation.presentation.TranslationLanguage
import com.example.stadmin.ui.Border
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.buttons.PasteAndClearButtonsRow
import com.example.stadmin.ui.common.CustomTextField
import com.example.stadmin.ui.theme.STAdminTheme
import com.example.stadmin.util.Constants

private enum class TranslationSection {
    CONTENT, PASSAGES, VIDEOS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationBottomSheet(
    baseTrace: Trace,
    viewState: TraceViewState,
    onLanguageChanged: (TranslationLanguage) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onContentChanged: (List<String>) -> Unit,
    onPassagesChanged: (List<Passage>) -> Unit,
    onVideosChanged: (List<String>) -> Unit,
    onSaveSuccessConsumed: () -> Unit,
    onNewTranslation: (TranslationLanguage) -> Unit,
    onTranslationSelected: (TraceTranslation) -> Unit,
    onSave: (String) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val existingTranslation = viewState.translations.find { it.language == viewState.language }
    var expandedSection by remember { mutableStateOf<TranslationSection?>(null) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    LaunchedEffect(viewState.language) {
        val existing = viewState.translations.find { it.language == viewState.language }
        if (existing != null) {
            onTranslationSelected(existing)
        } else {
            onNewTranslation(viewState.language)
        }
        expandedSection = null
    }

    LaunchedEffect(viewState.translationSaveSuccess) {
        if (viewState.translationSaveSuccess) {
            onSaveSuccessConsumed()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = Sizing.cardRadius, topEnd = Sizing.cardRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Translations",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = onDismiss,
                enabled = !viewState.isLoading,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }

        PrimaryTabRow(
            selectedTabIndex = TranslationLanguage.entries.indexOf(viewState.language),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            TranslationLanguage.entries.forEach { lang ->
                Tab(
                    selected = viewState.language == lang,
                    onClick = { onLanguageChanged(lang) },
                    text = {
                        Text(
                            text = languageName(lang),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.padding(Spacing.extraSmall))

        LazyColumn(
            modifier = Modifier.padding(horizontal = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            contentPadding = PaddingValues(bottom = Spacing.extraLarge)
        ) {
            item {
                CustomTextField(
                    label = "Title",
                    value = viewState.titleTranslated,
                    onValueChange = onTitleChanged,
                    labelTrailingContent = {
                        PasteAndClearButtonsRow(
                            onPaste = { onTitleChanged(it) },
                            clearValue = viewState.titleTranslated,
                            onClear = { onTitleChanged("") },
                        )
                    }
                )
            }
            if (!baseTrace.description.isNullOrBlank()) {
                item {
                    CustomTextField(
                        label = "Description",
                        value = viewState.descriptionTranslated,
                        onValueChange = onDescriptionChanged,
                        labelTrailingContent = {
                            PasteAndClearButtonsRow(
                                onPaste = { onDescriptionChanged(it) },
                                clearValue = viewState.descriptionTranslated,
                                onClear = { onDescriptionChanged("") },
                            )
                        },
                        minLines = 3
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    if (!baseTrace.content.isNullOrEmpty()) {
                        TranslationSummaryCard(
                            label = "Content",
                            count = viewState.contentTranslated.size,
                            total = baseTrace.content.size,
                            isExpanded = expandedSection == TranslationSection.CONTENT,
                            onClick = {
                                expandedSection =
                                    if (expandedSection == TranslationSection.CONTENT) null
                                    else TranslationSection.CONTENT
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (!baseTrace.passages.isNullOrEmpty()) {
                        TranslationSummaryCard(
                            label = "Passages",
                            count = viewState.passagesTranslated.size,
                            total = baseTrace.passages.size,
                            isExpanded = expandedSection == TranslationSection.PASSAGES,
                            onClick = {
                                expandedSection =
                                    if (expandedSection == TranslationSection.PASSAGES) null
                                    else TranslationSection.PASSAGES
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (!baseTrace.videos.isNullOrEmpty()) {
                        TranslationSummaryCard(
                            label = "Videos",
                            count = viewState.videosTranslated.size,
                            total = baseTrace.videos.size,
                            isExpanded = expandedSection == TranslationSection.VIDEOS,
                            onClick = {
                                expandedSection =
                                    if (expandedSection == TranslationSection.VIDEOS) null
                                    else TranslationSection.VIDEOS
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (expandedSection == TranslationSection.CONTENT && !baseTrace.content.isNullOrEmpty()) {
                itemsIndexed(baseTrace.content) { index, baseParagraph ->
                    val translatedValue = viewState.contentTranslated.getOrElse(index) { "" }
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                        Text(
                            text = "EN: $baseParagraph",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        CustomTextField(
                            label = "Paragraph ${index + 1}",
                            value = translatedValue,
                            onValueChange = { newValue ->
                                onContentChanged(viewState.contentTranslated.updateAt(index, "") { newValue })
                            },
                            minLines = 2,
                            labelTrailingContent = {
                                PasteAndClearButtonsRow(
                                    onPaste = { pasted ->
                                        onContentChanged(viewState.contentTranslated.updateAt(index, "") { pasted }) },
                                    clearValue = translatedValue,
                                    onClear = { onContentChanged(viewState.contentTranslated.updateAt(index, "") { "" }) }
                                )
                            }
                        )
                    }
                }
            }

            if (expandedSection == TranslationSection.PASSAGES && !baseTrace.passages.isNullOrEmpty()) {
                itemsIndexed(baseTrace.passages) { index, basePassage ->
                    val emptyPassage = Passage("", null, null, null, "", "")
                    val translatedPassage = viewState.passagesTranslated.getOrElse(index) { emptyPassage }
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                        Text(
                            text = "EN: ${basePassage.book} ${basePassage.chapter}:${basePassage.verseStart} — ${basePassage.text}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = Shapes.small
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.small),
                                verticalArrangement = Arrangement.spacedBy(Spacing.small)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                                    BibleBookSelector(
                                        selectedBook = translatedPassage.book,
                                        language = viewState.language,
                                        onBookSelected = { book ->
                                            onPassagesChanged(
                                                viewState.passagesTranslated.updateAt(index, emptyPassage) {
                                                    it.copy(book = book)
                                                }
                                            )
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    CustomTextField(
                                        label = "Version",
                                        value = getBibleVersion(viewState.language),
                                        enabled = false,
                                        onValueChange = {},
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                CustomTextField(
                                    label = "Text",
                                    value = translatedPassage.text,
                                    onValueChange = { text ->
                                        onPassagesChanged(
                                            viewState.passagesTranslated.updateAt(index, emptyPassage) {
                                                it.copy(text = text)
                                            }
                                        )
                                    },
                                    minLines = 3,
                                    labelTrailingContent = {
                                        PasteAndClearButtonsRow(
                                            onPaste = { pasted ->
                                                onPassagesChanged(
                                                    viewState.passagesTranslated.updateAt(index, emptyPassage) {
                                                        it.copy(text = pasted)
                                                    }
                                                )
                                            },
                                            clearValue = translatedPassage.text,
                                            onClear = {
                                                onPassagesChanged(
                                                    viewState.passagesTranslated.updateAt(index, emptyPassage) {
                                                        it.copy(text = "")
                                                    }
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (expandedSection == TranslationSection.VIDEOS && !baseTrace.videos.isNullOrEmpty()) {
                itemsIndexed(baseTrace.videos) { index, baseVideo ->
                    val translatedLabel = viewState.videosTranslated.getOrElse(index) { "" }
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                        Text(
                            text = "EN: ${baseVideo.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        CustomTextField(
                            label = "Video label ${index + 1}",
                            value = translatedLabel,
                            onValueChange = { newLabel ->
                                onVideosChanged(viewState.videosTranslated.updateAt(index, "") { newLabel })
                            },
                            labelTrailingContent = {
                                PasteAndClearButtonsRow(
                                    onPaste = { pasted ->
                                        onVideosChanged(viewState.videosTranslated.updateAt(index, "") { pasted }) },
                                    clearValue = translatedLabel,
                                    onClear = { onVideosChanged(viewState.videosTranslated.updateAt(index, "") { "" }) }
                                )
                            }
                        )
                    }
                }
            }

            item {
                SaveButton(
                    baseSlug = baseTrace.slug,
                    isLoading = viewState.isLoading,
                    language = viewState.language,
                    existingTranslation = existingTranslation,
                    onSave = onSave,
                    onDelete = onDelete
                )
            }
        }
    }
}

private fun getBibleVersion(language: TranslationLanguage): String {
    return when (language) {
        TranslationLanguage.ES -> Constants.Versions.REINA_VALERA_1960
        TranslationLanguage.PT -> Constants.Versions.NOVA_VERSAO_INTERNACIONAL
        TranslationLanguage.SV -> Constants.Versions.SVENSKA_FOLKBIBELN_1998
    }
}

private fun <T> List<T>.updateAt(index: Int, default: T, transform: (T) -> T): List<T> {
    return toMutableList().apply {
        while (size <= index) add(default)
        set(index, transform(get(index)))
    }
}

@Composable
private fun TranslationSummaryCard(
    label: String,
    count: Int,
    total: Int,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = Shapes.small,
        border = if (isExpanded) BorderStroke(
            Border.medium,
            MaterialTheme.colorScheme.primary
        ) else BorderStroke(Border.small, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isExpanded)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline
            )
            Text(
                text = "$count / $total",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isExpanded)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SaveButton(
    baseSlug: String,
    isLoading: Boolean,
    language: TranslationLanguage,
    existingTranslation: TraceTranslation?,
    onSave: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        Button(
            onClick = { onSave(baseSlug) },
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Sizing.iconSmall),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Save ${languageName(language)}")
            }
        }
        if (existingTranslation != null) {
            OutlinedButton(
                onClick = {
                    existingTranslation.id?.let {
                        onDelete(it)
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(
                    Border.small,
                    MaterialTheme.colorScheme.error
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Sizing.iconSmall),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete translation",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun languageName(language: TranslationLanguage): String {
    return when (language) {
        TranslationLanguage.ES -> "Español"
        TranslationLanguage.PT -> "Português"
        TranslationLanguage.SV -> "Svenska"
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TranslationBottomSheet(
                baseTrace = Trace(
                    id = 1,
                    slug = "noahs-ark",
                    title = "Noah's Ark",
                    description = "A large wooden vessel built by Noah to save his family.",
                    year = -2500,
                    era = TraceEra.OT,
                    imageUrl = null,
                    heroImageUrl = null,
                    latitude = 39.72,
                    longitude = 44.25,
                    content = listOf("First paragraph.", "Second paragraph."),
                    videos = null,
                    sources = null,
                    passages = null,
                    published = true,
                    createdAt = "2026-03-10T10:00:00Z",
                    updatedAt = "2026-04-01T10:00:00Z"
                ),
                onDismiss = {},
                viewState = TraceViewState(),
                onLanguageChanged = {},
                onTitleChanged = {},
                onDescriptionChanged = {},
                onContentChanged = {},
                onPassagesChanged = {},
                onVideosChanged = {},
                onSaveSuccessConsumed = {},
                onNewTranslation = {},
                onTranslationSelected = {},
                onSave = {},
                onDelete = {},
            )
        }
    }
}