package com.example.stadmin.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.coroutines.launch

@Composable
fun PasteButton(
    onPaste: (String) -> Unit,
    label: String = "Paste",
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    TextButton(
        onClick = {
            scope.launch {
                val text = clipboard.getClipEntry()
                    ?.clipData
                    ?.getItemAt(0)
                    ?.text
                    ?.toString() ?: return@launch
                onPaste(text)
            }
        }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}