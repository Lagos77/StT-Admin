package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing

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
                OutlinedTextField(
                    value = paragraph,
                    onValueChange = { newValue ->
                        onContentChanged(content.toMutableList().also { it[index] = newValue })
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 80.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = Shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                IconButton(
                    onClick = {
                        onContentChanged(content.toMutableList().also { it.removeAt(index) })
                    },
                    modifier = Modifier.size(Sizing.iconMedium)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remove paragraph",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Sizing.iconSmall)
                    )
                }
            }
        }
    }
}