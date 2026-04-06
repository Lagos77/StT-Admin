package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.stadmin.screens.trace.domain.model.Source
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.common.PasteButton
import com.example.stadmin.ui.common.RemoveButton

@Composable
fun SourcesSection(
    sources: List<Source>,
    onSourcesChanged: (List<Source>) -> Unit
) {
    CollapsibleSection(
        title = "Sources",
        count = sources.size,
        onAdd = { onSourcesChanged(sources + Source("", "")) }
    ) {
        sources.forEachIndexed { index, source ->
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
                    TraceTextField(
                        label = "Label",
                        value = source.label,
                        onValueChange = {
                            onSourcesChanged(
                                sources.toMutableList()
                                    .also { list -> list[index] = source.copy(label = it) })
                        }
                    )
                    TraceTextField(
                        label = "URL",
                        labelTrailingContent = {
                            PasteButton(
                                label = "Paste",
                                onPaste = { text ->
                                    onSourcesChanged(
                                        sources.toMutableList()
                                            .also { list -> list[index] = source.copy(url = text) }
                                    )
                                }
                            )
                        },
                        value = source.url,
                        onValueChange = {
                            onSourcesChanged(
                                sources.toMutableList()
                                    .also { list -> list[index] = source.copy(url = it) })
                        }
                    )
                    RemoveButton(onClick = {
                        onSourcesChanged(
                            sources.toMutableList().also { it.removeAt(index) })
                    })
                }
            }
        }
    }
}