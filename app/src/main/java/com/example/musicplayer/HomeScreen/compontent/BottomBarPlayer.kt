package com.example.musicplayer.HomeScreen.compontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.ui.timeStampToDuration


@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: AudioFile,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious:() -> Unit,
    onRepeat:() -> Unit
) {
    BottomAppBar(
        modifier = Modifier.height(220.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Slider(
                value = progress,
                onValueChange = { onProgress(it) },
                valueRange = 0f..1f
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onRepeat) {
                    Icon(
                        Icons.Default.RepeatOn,
                        contentDescription = null,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArtistInfo(
                    audio = audio,
                    modifier = Modifier.weight(1f),
                )
                MediaPlayerController(
                    isAudioPlaying = isAudioPlaying,
                    onStart = onStart,
                    onNext = onNext,
                    onPrevious = onPrevious
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = timeStampToDuration((progress * audio.duration).toLong()),
                )
                Text(
                    text = timeStampToDuration(audio.duration.toLong()),
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPlayerPreview() {
    BottomBarPlayer(
        progress = 0.5f,
        onProgress = {},
        audio = AudioFile(
            id = 1L,
            albumIdForArt = 1L,
            displayName = "Sample Song",
            artist = "Sample Artist",
            album = "Sample Album",
            duration = 300000,
        ),
        isAudioPlaying = true,
        onStart = {},
        onNext = {},
        onPrevious = {}, {
        }
    )
}
