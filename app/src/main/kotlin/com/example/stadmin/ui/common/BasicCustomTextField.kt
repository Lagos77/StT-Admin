package com.example.stadmin.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import com.example.stadmin.ui.Border
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Spacing

@Composable
fun BasicCustomTextField(
    label: String,
    placeHolder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    isWarning: Boolean = false,
    labelTrailingContent: (@Composable () -> Unit)? = null
) {
    Column {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            labelTrailingContent?.invoke()
        }
        Spacer(modifier = Modifier.height(Spacing.extraSmall))
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            modifier = modifier,
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            ),
            minLines = minLines,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(
                            color = if (enabled) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = Shapes.small
                        )
                        .border(
                            width = Border.small,
                            color = when {
                                isWarning -> MaterialTheme.colorScheme.error
                                !enabled -> MaterialTheme.colorScheme.outline
                                else -> MaterialTheme.colorScheme.outline
                            },
                            shape = Shapes.small
                        )
                        .padding(Spacing.small),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeHolder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            fontStyle = FontStyle.Italic,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}