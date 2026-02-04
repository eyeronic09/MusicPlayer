package com.example.musicplayer.PlayerScreen.Component

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.R
import com.example.musicplayer.Utilts.getEmbeddedArtwork
import kotlinx.coroutines.delay


@Composable
fun PlayerScreenContent(
    exoPlayer: ExoPlayer,
) {
    val context = LocalContext.current
    var hasMedia by remember { mutableStateOf(exoPlayer.currentMediaItem != null) }

    var currentUri by remember {mutableStateOf( exoPlayer.currentMediaItem?.localConfiguration?.uri) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableLongStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var trackTitle by remember { mutableStateOf("Unknown Title") }
    var trackArtists by remember { mutableStateOf("Unknown Artist") }
    var embeddedArtworkBitmap by remember { mutableStateOf<Bitmap?>(null) }


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

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // Update URI when track changes
                currentUri = (mediaItem?.localConfiguration?.uri
                    ?: mediaItem?.mediaMetadata?.extras?.getString("uri")
                    ?: mediaItem?.mediaId?.toUri()) as Uri?
                hasMedia = mediaItem != null
            }

        }

        exoPlayer.addListener(listener)

        // Initial state
        isPlaying = exoPlayer.isPlaying
        duration = exoPlayer.duration.coerceAtLeast(0L)
        trackTitle = exoPlayer.mediaMetadata.title?.toString() ?: "Unknown Title"
        trackArtists = exoPlayer.mediaMetadata.artist?.toString() ?: "Unknown Artist"
        hasMedia = exoPlayer.currentMediaItem != null
        // Try multiple ways to get the URI
        currentUri = (exoPlayer.currentMediaItem?.localConfiguration?.uri
            ?: exoPlayer.currentMediaItem?.mediaMetadata?.extras?.getString("uri")
            ?: exoPlayer.currentMediaItem?.mediaId?.toUri()) as Uri?
        
        android.util.Log.d("ArtworkDebug", "MediaItem: ${exoPlayer.currentMediaItem}")
        android.util.Log.d("ArtworkDebug", "LocalConfiguration URI: ${exoPlayer.currentMediaItem?.localConfiguration?.uri}")
        android.util.Log.d("ArtworkDebug", "MediaId: ${exoPlayer.currentMediaItem?.mediaId}")


        onDispose {
            exoPlayer.removeListener(listener)
        }
    }


    LaunchedEffect(currentUri) {
        android.util.Log.d("ArtworkDebug", "LaunchedEffect triggered with URI: $currentUri")
        if (currentUri != null) {
            embeddedArtworkBitmap = getEmbeddedArtwork(context, uri = currentUri!!)
        } else {
            embeddedArtworkBitmap = null
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
        embeddedArtworkBitmap = embeddedArtworkBitmap,
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
    embeddedArtworkBitmap: Bitmap? = null,
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!hasMedia) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nothing Playing",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            modifier = Modifier.fillMaxSize(),
                            onClick = { }
                        ) {
                            if (embeddedArtworkBitmap != null) {
                                Image(
                                    bitmap = embeddedArtworkBitmap.asImageBitmap(),
                                    contentDescription = "Album Art",
                                    modifier = Modifier.aspectRatio(1f)
                                )

                            } else {
                                Icon(
                                    modifier = Modifier.aspectRatio(1f),
                                    painter = painterResource(
                                        R.drawable.baseline_music_note_24
                                    ),
                                    contentDescription = "Music Note",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
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
}