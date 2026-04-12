package com.example.stadmin.ui.buttons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.stadmin.ui.Spacing

@Composable
fun PasteAndClearButtonsRow(
    onPaste: (String) -> Unit,
    clearValue: String,
    onClear: () -> Unit,
) {
    Row {
        PasteButton(onPaste = onPaste)
        if (!clearValue.isBlank()) {
            Spacer(modifier = Modifier.padding(horizontal = Spacing.small))
        }
        ClearButton(
            value = clearValue,
            onClear = onClear,
        )
    }
}