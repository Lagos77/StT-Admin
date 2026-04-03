package com.example.stadmin.screens.trace.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.common.formatDate
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun TraceCard(
    trace: Trace,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    var displayDeleteDialog by remember { mutableStateOf(false) }

    if (displayDeleteDialog) {
        AlertDialog(
            onDismissRequest = { displayDeleteDialog = false },
            title = { Text("Delete trace") },
            text = { Text("Are you sure you want to delete \"${trace.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    displayDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { displayDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        shape = Shapes.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            AsyncImage(
                model = trace.imageUrl,
                contentDescription = trace.title,
                modifier = Modifier
                    .size(Sizing.thumbnailMedium)
                    .clip(Shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    Text(
                        text = if (trace.isNt) "NT" else "OT",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (trace.isNt) Color(0xFF2E5F8A) else Color(0xFF8B3A3A)
                    )
                    trace.year?.let {
                        Text(
                            text = if (it < 0) "c. ${Math.abs(it)} BC" else "c. $it AD",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    StatusPill(
                        text = if (trace.published) "Published" else "Draft",
                        isPositive = trace.published
                    )
                }
                Text(
                    text = trace.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    StatusPill(
                        text = if (trace.isComplete) "Complete" else "Incomplete",
                        isPositive = trace.isComplete
                    )
                    trace.updatedAt?.let {
                        val dateLabel =
                            if (trace.updatedAt == trace.createdAt) "Created" else "Edited"
                        Text(
                            text = "$dateLabel ${formatDate(it)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                IconButton(
                    onClick = { displayDeleteDialog = true },
                    modifier = Modifier.size(Sizing.iconMedium)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Sizing.iconSmall)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(Sizing.iconSmall)
                )
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    isPositive: Boolean
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = if (isPositive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
        modifier = Modifier
            .background(
                color = if (isPositive) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                shape = Shapes.pill,
            )
            .padding(horizontal = Spacing.small, vertical = Spacing.extraSmall)
    )
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        TraceCard(
            trace = Trace(
                id = 1,
                slug = "noahs-ark",
                title = "Noah's Ark",
                description = "A large wooden vessel built by Noah to save his family.",
                year = -2500,
                isNt = false,
                imageUrl = null,
                heroImageUrl = null,
                latitude = 39.72,
                longitude = 44.25,
                content = listOf("First paragraph.", "Second paragraph."),
                videos = null,
                sources = null,
                passages = null,
                published = true,
                createdAt = "2026-03-10T10:00:00Z",
                updatedAt = "2026-04-01T10:00:00Z"
            ),
            onClick = {},
            onDelete = {}
        )
    }
}