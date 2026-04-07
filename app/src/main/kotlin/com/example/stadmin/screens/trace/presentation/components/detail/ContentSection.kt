package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.common.RemoveButton
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun ContentSection(
    content: List<String>,
    onContentChanged: (List<String>) -> Unit
) {
    CollapsibleSection(
        title = "Content",
        count = content.size,
        onAdd = {
                onContentChanged(content + "")
        }
    ) {
        content.forEachIndexed { index, paragraph ->
            Column(modifier = Modifier) {
                TraceTextField(
                    label = "Text",
                    value = paragraph,
                    onValueChange = { newValue ->
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

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        ContentSection(
            content = listOf(""),
            onContentChanged = {},
        )
    }
}