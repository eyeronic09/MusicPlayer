package com.example.musicplayer.PlayerScreen.Component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay

@Composable
fun exoplayerCustomPlayerState(
    modifier: Modifier = Modifier,
    artworkUri: Uri?,
    currentPosition: Float,
    duration: Long,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround

        ) {

        val artworkModifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(16.dp))

        if (artworkUri != null) {
            SubcomposeAsyncImage(
                model = artworkUri,
                contentDescription = "Artwork",
                modifier = artworkModifier,
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = artworkModifier
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = artworkModifier
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            )
        } else {
            Box(
                modifier = artworkModifier
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Slider(
            value = if (duration > 0) currentPosition / duration else 0f,
            onValueChange = { onSeek(it) },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF4081),
                activeTrackColor = Color(0xFFFF4081),
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )

        // Control buttons

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            IconButton(
                onClick = onPrevious,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous track",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Play/Pause button
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Next button
            IconButton(
                onClick = onNext,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next track",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun exoplayerCustomPlayer(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer
) {
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableLongStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentArtwork by remember { mutableStateOf<Uri?>(null) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(Playing: Boolean) {
                isPlaying = Playing
            }

            override fun onPlaybackStateChanged(p: Int) {
                duration = exoPlayer.duration.coerceAtLeast(0L)
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                currentArtwork = mediaMetadata.artworkUri
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // Update current position periodically
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition.toFloat()
            delay(500) // Update every second
        }
    }

    // Update position when not playing
    LaunchedEffect(currentPosition) {
        if (!isPlaying) {
            currentPosition = exoPlayer.currentPosition.toFloat()
        }
    }

    exoplayerCustomPlayerState(
        modifier = modifier,
        artworkUri = currentArtwork,
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
        onPrevious = { exoPlayer.previousMediaItemIndex },
        onSeek = { exoPlayer.seekTo((it * duration).toLong()) },
    )
}

@Preview(showSystemUi = true)
@Composable
private fun ButtonPlayer() {
    exoplayerCustomPlayerState(
        artworkUri = null,
        currentPosition = 30000f,
        duration = 60000,
        isPlaying = true,
        onPlayPause = {},
        onNext = {},
        onPrevious = {},
        onSeek = {},
    )
}