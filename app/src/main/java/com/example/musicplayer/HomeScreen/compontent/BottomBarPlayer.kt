package com.example.musicplayer.HomeScreen.compontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.ui.timeStampToDuration


@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: AudioFile,
    isAudioPlaying: Boolean,
    repeatMode: Int = Player.REPEAT_MODE_OFF,
    isShuffleEnabled: Boolean = false,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRepeat: () -> Unit,
    onShuffle: () -> Unit = {}
) {

    BottomAppBar(
        modifier = Modifier.height(200.dp)
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
                IconButton(onClick = onShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRepeat) {
                    val icon = if (repeatMode == Player.REPEAT_MODE_ONE)
                        Icons.Default.RepeatOne else Icons.Default.Repeat

                    val tint = if (repeatMode == Player.REPEAT_MODE_OFF)
                        MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    Icon(
                        imageVector = icon,
                        contentDescription = "Repeat",
                        tint = tint
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
        repeatMode = Player.REPEAT_MODE_OFF,
        isShuffleEnabled = false,
        onStart = {},
        onNext = {},
        onPrevious = {},
        onRepeat = {},
        onShuffle = {}
    )
}
