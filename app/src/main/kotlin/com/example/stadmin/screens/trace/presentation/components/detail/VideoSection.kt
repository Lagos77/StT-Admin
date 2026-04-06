package com.example.stadmin.screens.trace.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.stadmin.screens.trace.domain.model.Video
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing

@Composable
fun VideosSection(
    videos: List<Video>,
    onVideosChanged: (List<Video>) -> Unit
) {
    CollapsibleSection(
        title = "Videos",
        count = videos.size,
        onAdd = { onVideosChanged(videos + Video("", "", "", "")) }
    ) {
        videos.forEachIndexed { index, video ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = Shapes.small
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    TraceTextField(
                        label = "Label",
                        value = video.label,
                        onValueChange = {
                            onVideosChanged(
                                videos.toMutableList()
                                    .also { list -> list[index] = video.copy(label = it) })
                        }
                    )
                    TraceTextField(
                        label = "URL",
                        value = video.url,
                        onValueChange = {
                            onVideosChanged(
                                videos.toMutableList()
                                    .also { list -> list[index] = video.copy(url = it) })
                        }
                    )
                    TraceTextField(
                        label = "Video ID",
                        value = video.videoId,
                        onValueChange = {
                            onVideosChanged(
                                videos.toMutableList()
                                    .also { list -> list[index] = video.copy(videoId = it) })
                        }
                    )
                    TraceTextField(
                        label = "Platform",
                        value = video.platform,
                        onValueChange = {
                            onVideosChanged(
                                videos.toMutableList()
                                    .also { list -> list[index] = video.copy(platform = it) })
                        }
                    )
                    TextButton(
                        onClick = {
                            onVideosChanged(videos.toMutableList().also { it.removeAt(index) })
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove video",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(Sizing.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(Spacing.extraSmall))
                        Text(
                            text = "Remove",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}