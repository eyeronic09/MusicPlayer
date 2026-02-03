package com.example.musicplayer.PlayerScreen.Component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.Utilts.formatDuration
import com.example.musicplayer.Utilts.formatTimeWithHours


@Composable
fun PlayerControls(
    currentPosition: Float,
    duration: Long,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
) {

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
    ) {

        Slider(
            value = if (duration > 0) currentPosition / duration.toFloat() else 0f,
            onValueChange = { onSeek(it) },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF2196F3),
                activeTrackColor = Color(0xFFFF4081),
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                activeTickColor = Color.Green,
                inactiveTickColor = Color.DarkGray
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatTimeWithHours(currentPosition))
            Text(text = formatDuration(duration))
        }



        Row(
            modifier = Modifier
                .fillMaxWidth()
                ,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = onPrevious
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "SkipPrevious"
                )
            }
            IconButton(
                onClick = onPlayPause
            ) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "pause"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "PlayBack"
                    )
                }

            }
            IconButton(
                onClick = onNext
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "skip"
                )
            }

        }
    }

}
@Preview(showBackground = true)
@Composable
fun PlayerControlsPreview() {
    PlayerControls(
        currentPosition = 3200f,
        duration = 10000,
        isPlaying = true,
        onPlayPause = {},
        onNext = {},
        onPrevious = {},
        onSeek = { _ -> }
    )
}

