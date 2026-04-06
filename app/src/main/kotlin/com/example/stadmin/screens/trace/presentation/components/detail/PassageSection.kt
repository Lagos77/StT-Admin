package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing

@Composable
fun PassagesSection(
    passages: List<Passage>,
    onPassagesChanged: (List<Passage>) -> Unit
) {
    CollapsibleSection(
        title = "Passages",
        count = passages.size,
        onAdd = { onPassagesChanged(passages + Passage("", 1, 1, null, "", null)) }
    ) {
        passages.forEachIndexed { index, passage ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                        TraceTextField(
                            label = "Book",
                            value = passage.book,
                            onValueChange = {
                                onPassagesChanged(
                                    passages.toMutableList().also {
                                        it[index] = passage.copy(book = it[index].book)
                                    })
                            },
                            modifier = Modifier.weight(2f)
                        )
                        TraceTextField(
                            label = "Chapter",
                            value = passage.chapter.toString(),
                            onValueChange = {
                                onPassagesChanged(
                                    passages.toMutableList().also { list ->
                                        list[index] =
                                            passage.copy(chapter = it.toIntOrNull() ?: 1)
                                    })
                            },
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        TraceTextField(
                            label = "Verse Start",
                            value = passage.verseStart.toString(),
                            onValueChange = {
                                onPassagesChanged(
                                    passages.toMutableList().also { list ->
                                        list[index] =
                                            passage.copy(verseStart = it.toIntOrNull() ?: 1)
                                    })
                            },
                            keyboardType = KeyboardType.Number,
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
                            modifier = Modifier.weight(1f)
                        )
                    }
                    TraceTextField(
                        label = "Version",
                        value = passage.version ?: "",
                        onValueChange = {
                            onPassagesChanged(
                                passages.toMutableList()
                                    .also { list -> list[index] = passage.copy(version = it) })
                        }
                    )
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