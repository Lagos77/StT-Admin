package com.example.stadmin.ui.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ClearButton(
    value: String,
    onClear: () -> Unit,
    label: String = "Clear",
) {
    if (value.isBlank()) return

    TextButton(onClick = onClear) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}