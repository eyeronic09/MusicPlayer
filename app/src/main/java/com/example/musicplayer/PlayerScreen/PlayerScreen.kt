@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.example.musicplayer.PlayerScreen

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import com.example.musicplayer.PlayerScreen.Component.PlayerScreenContent
import org.koin.androidx.compose.koinViewModel


@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    navController: NavHostController,
    viewModel: PlayerScreenVm = koinViewModel(),
    exoPlayer: ExoPlayer,
    folderId: Long? = null,
    audioUri: String? = null
) {
    val uiState = viewModel.uiState.collectAsState()

    // Trigger loading only if arguments are provided
    LaunchedEffect(folderId) {
        if (folderId != null && folderId != 0L) {
            viewModel.onEvent(PlayerScreenUiEvent.LoadFolder(folderId))
        }
    }

    LaunchedEffect(audioUri) {
        if (audioUri != null && audioUri.isNotEmpty()) {
            viewModel.onEvent(PlayerScreenUiEvent.UserSpecificAudio(audioUri.toUri()))
        }
    }


    LaunchedEffect(uiState.value.playbackSource) {
        when(val source = uiState.value.playbackSource){
            is PlaybackSource.Folder -> {
                val mediaItems = source.files.map {
                    MediaItem.fromUri(it)
                }
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.setMediaItems(mediaItems)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
            is PlaybackSource.SingleAudio -> {
                val mediaItem = MediaItem.fromUri(source.uri)
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
            is PlaybackSource.None -> {
                // If we're just navigating to the Player tab, don't stop existing playback.
                // Only stop if the player is explicitly empty.
                if (exoPlayer.currentMediaItem == null) {
                    exoPlayer.stop()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Now Playing",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    // Only show back button if we're not on a main tab
                    if (folderId != null || audioUri != null) {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PlayerScreenContent(
                exoPlayer = exoPlayer
            )
        }
    }
}
