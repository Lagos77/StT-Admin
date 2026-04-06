package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Border
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.theme.STAdminTheme
import com.example.stadmin.util.Constants.BIBLE_BOOKS

@Composable
fun PassagesSection(
    passages: List<Passage>,
    onPassagesChanged: (List<Passage>) -> Unit
) {
    CollapsibleSection(
        title = "Passages",
        count = passages.size,
        onAdd = { onPassagesChanged(passages + Passage("", null, null, null, "", "NIV")) }
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
                            onBookSelected = {
                                onPassagesChanged(
                                    passages.toMutableList().also { list ->
                                        list[index] = passage.copy(book = it)
                                    }
                                )
                            },
                            modifier = Modifier.weight(2f)
                        )
                        TraceTextField(
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
                        TraceTextField(
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
                        TraceTextField(
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
                    TraceTextField(
                        label = "Text",
                        value = passage.text,
                        onValueChange = {
                            onPassagesChanged(
                                passages.toMutableList()
                                    .also { list -> list[index] = passage.copy(text = it) })
                        },
                        minLines = 3
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onPassagesChanged(
                                    passages.toMutableList().also { it.removeAt(index) })
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remove passage",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(Sizing.iconSmall)
                            )
                            Spacer(modifier = Modifier.width(Spacing.extraSmall))
                            Text(
                                text = "Remove",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BibleBookSelector(
    selectedBook: String,
    onBookSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        BibleBookBottomSheet(
            selectedBook = selectedBook,
            onBookSelected = onBookSelected,
            onDismiss = { showBottomSheet = false }
        )
    }

    Column(modifier = modifier) {
        Text(
            text = "Book",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(Spacing.extraSmall))
        OutlinedCard(
            onClick = { showBottomSheet = true },
            shape = Shapes.small,
            border = BorderStroke(Border.small, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            OutlinedTextField(
                value = selectedBook.ifBlank { "Select book" },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBottomSheet = true },
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = Shapes.small,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(Sizing.iconSmall)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = if (selectedBook.isBlank()) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.onSurface
                ),
                enabled = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BibleBookBottomSheet(
    selectedBook: String,
    onBookSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredBooks = remember(searchQuery) {
        if (searchQuery.isBlank()) BIBLE_BOOKS
        else BIBLE_BOOKS.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = Sizing.cardRadius, topEnd = Sizing.cardRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium)
        ) {
            Text(
                text = "Select Book",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.medium)
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.small),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = Shapes.small,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            if (filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.large),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results for \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = Spacing.extraLarge)
                ) {
                    items(filteredBooks) { book ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onBookSelected(book)
                                    onDismiss()
                                }
                                .padding(vertical = Spacing.medium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = book,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (book == selectedBook) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            if (book == selectedBook) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(Sizing.iconSmall)
                                )
                            }
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            thickness = Border.small
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










