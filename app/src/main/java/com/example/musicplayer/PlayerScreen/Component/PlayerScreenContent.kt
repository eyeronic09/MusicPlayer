package com.example.musicplayer.PlayerScreen.Component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import kotlinx.coroutines.delay


@Composable
fun PlayerScreenContent(
    exoPlayer: ExoPlayer,
) {
    var hasMedia by remember { mutableStateOf(exoPlayer.currentMediaItem != null) }

    var currentPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableLongStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var trackTitle by remember { mutableStateOf("Unknown Title") }
    var trackArtists by remember { mutableStateOf("Unknown Artist") }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                duration = exoPlayer.duration.coerceAtLeast(0L)
                hasMedia = exoPlayer.currentMediaItem != null
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                trackTitle = mediaMetadata.title?.toString() ?: "Unknown Title"
                trackArtists = mediaMetadata.artist?.toString() ?: "Unknown Artist"
            }
        }

        exoPlayer.addListener(listener)

        // Initial state
        isPlaying = exoPlayer.isPlaying
        duration = exoPlayer.duration.coerceAtLeast(0L)
        trackTitle = exoPlayer.mediaMetadata.title?.toString() ?: "Unknown Title"
        trackArtists = exoPlayer.mediaMetadata.artist?.toString() ?: "Unknown Artist"
        hasMedia = exoPlayer.currentMediaItem != null

        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // Update current position periodically
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition.toFloat()
            delay(500)
        }
    }

    // Update position when not playing (e.g. after a seek)
    LaunchedEffect(currentPosition) {
        if (!isPlaying) {
            currentPosition = exoPlayer.currentPosition.toFloat()
        }
    }

    PlayerScreenContentStateless(
        hasMedia = hasMedia,
        trackTitle = trackTitle,
        trackArtists = trackArtists,
        currentPosition = currentPosition,
        duration = duration,
        isPlaying = isPlaying,
        onPlayPause = {
            if (isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
        },
        onNext = { exoPlayer.seekToNext() },
        onPrevious = { exoPlayer.seekToPrevious() },
        onSeek = { fraction ->
            exoPlayer.seekTo((fraction * duration).toLong())
            currentPosition = (fraction * duration)
        }
    )
}

@Composable
fun PlayerScreenContentStateless(
    hasMedia: Boolean,
    trackTitle: String,
    trackArtists: String,
    currentPosition: Float,
    duration: Long,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit
) {
    if (!hasMedia) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nothing Playing",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Gray
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                TitlePlate(
                    trackName = trackTitle,
                    trackArtists = trackArtists
                )
                PlayerControls(
                    currentPosition = currentPosition,
                    duration = duration,
                    isPlaying = isPlaying,
                    onPlayPause = onPlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onSeek = onSeek
                )
            }
        }
    }
}

@Preview
@Composable
fun PlayerScreenContentPreview() {
    MusicPlayerTheme {
        PlayerScreenContentStateless(
            hasMedia = true,
            trackTitle = "Sample Track",
            trackArtists = "Sample Artist",
            currentPosition = 30000f,
            duration = 180000L,
            isPlaying = true,
            onPlayPause = {},
            onNext = {},
            onPrevious = {},
            onSeek = {}
        )
    }
}

@Preview
@Composable
fun PlayerScreenContentNoMediaPreview() {
    MusicPlayerTheme {
        PlayerScreenContentStateless(
            hasMedia = false,
            trackTitle = "",
            trackArtists = "",
            currentPosition = 0f,
            duration = 0L,
            isPlaying = false,
            onPlayPause = {},
            onNext = {},
            onPrevious = {},
            onSeek = {}
        )
    }
}
