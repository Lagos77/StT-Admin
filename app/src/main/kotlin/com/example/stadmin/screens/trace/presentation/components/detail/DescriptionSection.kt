package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.ui.buttons.PasteAndClearButtonsRow
import com.example.stadmin.ui.common.CustomTextField
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun DescriptionSection(
    description: String,
    onDescriptionChanged: (String) -> Unit
) {
    SectionCard(title = "Description") {
        CustomTextField(
            label = "Year",
            value = description,
            onValueChange = onDescriptionChanged,
            keyboardType = KeyboardType.Text,
            labelTrailingContent = {
                PasteAndClearButtonsRow(
                    onPaste = { onDescriptionChanged(it) },
                    clearValue = description,
                    onClear = { onDescriptionChanged("") }
                )
            }
        )
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        DescriptionSection(
            description = "Example",
            onDescriptionChanged = {},
        )
    }
}