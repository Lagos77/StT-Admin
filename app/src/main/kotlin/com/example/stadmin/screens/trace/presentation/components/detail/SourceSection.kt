package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.stadmin.screens.trace.domain.model.Source
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing

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
                        value = source.url,
                        onValueChange = {
                            onSourcesChanged(
                                sources.toMutableList()
                                    .also { list -> list[index] = source.copy(url = it) })
                        }
                    )
                    TextButton(
                        onClick = {
                            onSourcesChanged(
                                sources.toMutableList().also { it.removeAt(index) })
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove source",
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