package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.translation.presentation.TranslationLanguage
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.buttons.RemoveButton
import com.example.stadmin.ui.common.CustomTextField
import com.example.stadmin.ui.common.SelectionBottomSheet
import com.example.stadmin.ui.common.SelectorField
import com.example.stadmin.ui.theme.STAdminTheme
import com.example.stadmin.util.Constants

@Composable
fun PassagesSection(
    passages: List<Passage>,
    onPassagesChanged: (List<Passage>) -> Unit
) {
    CollapsibleSection(
        title = "Passages",
        count = passages.size,
        onAdd = { onPassagesChanged(passages + Passage("", null, null, null, "", Constants.Versions.NEW_INTERNATIONAL_VERSION)) }
    ) {
        passages.forEachIndexed { index, passage ->
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
                            selectedBook = passage.book,
                            language = null,
                            onBookSelected = {
                                onPassagesChanged(
                                    passages.toMutableList().also { list ->
                                        list[index] = passage.copy(book = it)
                                    }
                                )
                            },
                            modifier = Modifier.weight(2f)
                        )
                        CustomTextField(
                            label = "Chapter",
                            value = passage.chapter?.toString() ?: "",
                            onValueChange = {
                                onPassagesChanged(
                                    passages.toMutableList().also { list ->
                                        list[index] = passage.copy(chapter = it.toIntOrNull() ?: 1)
                                    }
                                )
                            },
                            keyboardType = KeyboardType.Number,
                            maxLength = 2,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        CustomTextField(
                            label = "Verse Start",
                            value = passage.verseStart?.toString() ?: "",
                            onValueChange = {
                                onPassagesChanged(
                                    passages.toMutableList().also { list ->
                                        list[index] =
                                            passage.copy(verseStart = it.toIntOrNull() ?: 1)
                                    })
                            },
                            keyboardType = KeyboardType.Number,
                            maxLength = 3,
                            modifier = Modifier.weight(1f)
                        )
                        CustomTextField(
                            label = "Verse End",
                            value = passage.verseEnd?.toString() ?: "",
                            onValueChange = {
                                onPassagesChanged(
                                    passages.toMutableList().also { list ->
                                        list[index] = passage.copy(verseEnd = it.toIntOrNull())
                                    })
                            },
                            keyboardType = KeyboardType.Number,
                            maxLength = 3,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    CustomTextField(
                        label = "Text",
                        value = passage.text,
                        onValueChange = {
                            onPassagesChanged(
                                passages.toMutableList()
                                    .also { list -> list[index] = passage.copy(text = it) })
                        },
                        minLines = 3
                    )
                    RemoveButton(onClick = {
                        onPassagesChanged(
                            passages.toMutableList().also { it.removeAt(index) })
                    })
                }
            }
        }
    }
}

@Composable
fun BibleBookSelector(
    selectedBook: String,
    onBookSelected: (String) -> Unit,
    language: TranslationLanguage?,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val books = when (language) {
        TranslationLanguage.ES -> Constants.BIBLE_BOOKS_ES
        TranslationLanguage.PT -> Constants.BIBLE_BOOKS_PT
        TranslationLanguage.SV -> Constants.BIBLE_BOOKS_SV
        null -> Constants.BIBLE_BOOKS_EN
    }

    if (showBottomSheet) {
        SelectionBottomSheet(
            title = "Select Book",
            items = books,
            selectedItem = selectedBook,
            onItemSelected = onBookSelected,
            onDismiss = { showBottomSheet = false },
            searchable = true
        )
    }

    SelectorField(
        label = "Book",
        value = selectedBook.ifBlank { "Select book" },
        onClick = { showBottomSheet = true },
        modifier = modifier
    )
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        PassagesSection(
            passages = listOf(
                Passage(
                    book = "Genesis",
                    chapter = 6,
                    verseStart = 13,
                    verseEnd = 14,
                    text = "So God said to Noah, I am going to put an end to all people...",
                    version = "NIV"
                )
            ),
            onPassagesChanged = {}
        )
    }
}










