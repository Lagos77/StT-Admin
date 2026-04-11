package com.example.stadmin.screens.trace.presentation.components.detail

import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.buttons.PasteButton
import com.example.stadmin.ui.common.CustomTextField
import com.example.stadmin.ui.theme.STAdminTheme
import java.util.Locale

@Composable
fun LocationSection(
    latitude: String,
    longitude: String,
    onLatitudeChanged: (String) -> Unit,
    onLongitudeChanged: (String) -> Unit
) {
    val context = LocalContext.current

    SectionCard(
        title = "Location",
        trailingContent = {
            PasteButton(
                label = "Paste",
                onPaste = { text ->
                    val parts = text.split(",").map { it.trim() }
                    if (parts.size == 2 && parts[0].toDoubleOrNull() != null) {
                        onLatitudeChanged(parts[0])
                        onLongitudeChanged(parts[1])
                    } else {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        geocoder.getFromLocationName(text, 1) { addresses ->
                            addresses.firstOrNull()?.let { address ->
                                onLatitudeChanged(address.latitude.toString())
                                onLongitudeChanged(address.longitude.toString())
                            }
                        }
                    }
                }
            )
        }) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            CustomTextField(
                label = "Latitude",
                value = latitude,
                onValueChange = onLatitudeChanged,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            CustomTextField(
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