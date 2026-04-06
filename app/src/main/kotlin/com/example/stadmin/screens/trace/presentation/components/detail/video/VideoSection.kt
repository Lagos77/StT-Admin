package com.example.stadmin.screens.trace.presentation.components.detail.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.stadmin.screens.trace.domain.model.Video
import com.example.stadmin.screens.trace.presentation.components.detail.CollapsibleSection
import com.example.stadmin.screens.trace.presentation.screens.TraceTextField
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.common.PasteButton
import com.example.stadmin.ui.common.RemoveButton
import com.example.stadmin.ui.common.SelectionBottomSheet
import com.example.stadmin.ui.common.SelectorField
import com.example.stadmin.ui.extractYoutubeVideoId

@Composable
fun VideosSection(
    videos: List<Video>,
    onVideosChanged: (List<Video>) -> Unit
) {
    var showPlatformSheet by remember { mutableStateOf<Int?>(null) }

    if (showPlatformSheet != null) {
        val index = showPlatformSheet!!
        SelectionBottomSheet(
            title = "Select Platform",
            items = VideoPlatform.entries.map { it.label },
            selectedItem = videos[index].platform,
            onItemSelected = { platform ->
                onVideosChanged(
                    videos.toMutableList().also { list ->
                        list[index] = videos[index].copy(platform = platform)
                    }
                )
            },
            onDismiss = { showPlatformSheet = null },
            searchable = false
        )
    }

    CollapsibleSection(
        title = "Videos",
        count = videos.size,
        onAdd = { onVideosChanged(videos + Video("", "", "", "")) }
    ) {
        videos.forEachIndexed { index, video ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                        label = "Video ID",
                        labelTrailingContent = {
                            PasteButton(
                                label = "Paste URL",
                                onPaste = { text ->
                                    val videoId = extractYoutubeVideoId(text) ?: return@PasteButton
                                    onVideosChanged(
                                        videos.toMutableList().also { list ->
                                            list[index] = video.copy(
                                                videoId = videoId,
                                                url = "https://www.youtube.com/watch?v=$videoId",
                                                platform = VideoPlatform.YOUTUBE.label
                                            )
                                        }
                                    )
                                }
                            )
                        },
                        value = video.videoId,
                        onValueChange = {
                            onVideosChanged(
                                videos.toMutableList()
                                    .also { list -> list[index] = video.copy(videoId = it) })
                        }
                    )
                    SelectorField(
                        label = "Platform",
                        value = video.platform.ifBlank { VideoPlatform.YOUTUBE.label },
                        onClick = { showPlatformSheet = index }
                    )
                    RemoveButton(onClick = {
                        onVideosChanged(videos.toMutableList().also { it.removeAt(index) })
                    })
                }
            }
        }
    }
}