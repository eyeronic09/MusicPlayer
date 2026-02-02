package com.example.musicplayer.PlayerScreen.Component

import android.graphics.Bitmap
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
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay


@Composable
fun PlayerScreenContent(
    exoPlayer: ExoPlayer,
) {
    var hasMedia by remember { mutableStateOf(exoPlayer.currentMediaItem != null) }

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
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val contecxt = LocalContext.current

            var artWork by remember { mutableStateOf<Bitmap?>(null) }


            var currentPosition by remember { mutableFloatStateOf(0f) }
            var duration by remember { mutableLongStateOf(0) }
            var isPlaying by remember { mutableStateOf(false) }
            var artAlbum by remember { mutableStateOf<Uri?> (null) }
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
                        artAlbum = mediaMetadata.artworkUri
                        trackTitle = mediaMetadata.title?.toString() ?: "Unknown Title"
                        trackArtists = mediaMetadata.artist?.toString() ?: "Unknown Artist"


                    }

                    override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                        hasMedia = mediaItem != null
                    }
                }

                exoPlayer.addListener(listener)

                // Initial state
                isPlaying = exoPlayer.isPlaying
                duration = exoPlayer.duration.coerceAtLeast(0L)
                artAlbum = exoPlayer.mediaMetadata.artworkUri
                trackTitle = exoPlayer.mediaMetadata.title?.toString() ?: "Unknown Title"
                trackArtists = exoPlayer.mediaMetadata.artist?.toString() ?: "Unknown Artist"

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


            PlayerArtworkDisplay(
                modifier = Modifier.weight(1f),
                artworkUri = artAlbum
            )

            Column {
                TitlePlate(
                    trackName = trackTitle,
                    trackArtists = trackArtists
                )
                PlayerControls(
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
        }
    }
}
