package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.common.RemoveButton

@Composable
fun ContentSection(
    content: List<String>,
    onContentChanged: (List<String>) -> Unit
) {
    CollapsibleSection(
        title = "Content",
        count = content.size,
        onAdd = { onContentChanged(content + "") }
    ) {
        content.forEachIndexed { index, paragraph ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                TraceTextField(
                    label = "Text",
                    value = paragraph,
                    onValueChange = {newValue ->
                        onContentChanged(content.toMutableList().also { it[index] = newValue })
                    },
                    minLines = 2
                )
                RemoveButton(onClick = {
                    onContentChanged(content.toMutableList().also { it.removeAt(index) })
                })
            }
        }
    }
}