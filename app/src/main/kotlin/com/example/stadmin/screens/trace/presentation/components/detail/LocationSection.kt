package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.common.PasteButton
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun LocationSection(
    latitude: String,
    longitude: String,
    onLatitudeChanged: (String) -> Unit,
    onLongitudeChanged: (String) -> Unit
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    SectionCard(
        title = "Location",
        trailingContent = {
            PasteButton(
                label = "Paste",
                onPaste = { text ->
                    val parts = text.split(",").map { it.trim() }
                    if (parts.size == 2) {
                        onLatitudeChanged(parts[0])
                        onLongitudeChanged(parts[1])
                    }
                }
            )
        }) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            TraceTextField(
                label = "Latitude",
                value = latitude,
                onValueChange = onLatitudeChanged,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            TraceTextField(
                label = "Longitude",
                value = longitude,
                onValueChange = onLongitudeChanged,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        LocationSection(
            latitude = "123",
            longitude = "321",
            onLatitudeChanged = {},
            onLongitudeChanged = {},
        )
    }
}