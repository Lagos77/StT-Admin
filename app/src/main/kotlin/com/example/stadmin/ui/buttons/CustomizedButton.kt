package com.example.stadmin.ui.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun CustomizedButton(
    modifier: Modifier = Modifier,
    label: String?,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector?,
    containerColor: ContainerColor,
    iconColorType: IconType = IconType.DEFAULT,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = isEnabled,
        contentPadding = PaddingValues(horizontal = Spacing.medium),
        colors = ButtonDefaults.buttonColors(
            containerColor = setContainerColor(containerColor),
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = setContainerColor(containerColor),
            disabledContentColor = MaterialTheme.colorScheme.outline

        ),
        shape = RoundedCornerShape(Sizing.buttonRadius),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Spacing.extraLarge),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(Sizing.iconVerySmall),
                        tint = if (!isEnabled) MaterialTheme.colorScheme.outline else setIconColor(iconColorType),
                    )
                }
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun setContainerColor(color: ContainerColor): Color {
    return when (color) {
        ContainerColor.LIGHT_BROWN -> MaterialTheme.colorScheme.background
        ContainerColor.WHITE -> MaterialTheme.colorScheme.onPrimary
    }
}

@Composable
private fun setIconColor(color: IconType): Color {
    return when (color) {
        IconType.DEFAULT ->    MaterialTheme.colorScheme.onBackground
        IconType.ERROR ->    MaterialTheme.colorScheme.error
    }
}

enum class IconType {
    DEFAULT,
    ERROR,
}

enum class ContainerColor {
    LIGHT_BROWN,
    WHITE,
}

@Composable
@Preview(showBackground = true)
private fun CustomizedButtonPreview() {
    STAdminTheme {
        CustomizedButton(
            label = "Translate",
            icon = Icons.Filled.Translate,
            containerColor = ContainerColor.LIGHT_BROWN,
            onClick = {}
        )
    }
}