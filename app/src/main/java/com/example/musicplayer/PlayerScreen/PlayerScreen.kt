package com.example.musicplayer.PlayerScreen

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.musicplayer.PlayerScreen.Component.exoplayerCustomPlayer
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

    LaunchedEffect(folderId) {
        folderId?.let {
            viewModel.onEvent(PlayerScreenUiEvent.LoadFolder(it))
        }
    }
    
    LaunchedEffect(audioUri) {
        audioUri?.let { uri ->
            viewModel.onEvent(PlayerScreenUiEvent.UserSpecificAudio(uri.toUri()))
        }
    }
    LaunchedEffect(uiState.value.playbackSource) {
        when(val source = uiState.value.playbackSource){
            is PlaybackSource.Folder -> {
                val mediaItems = source.files.map { MediaItem.fromUri(it) }
                exoPlayer.setMediaItems(mediaItems)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
            is PlaybackSource.SingleAudio -> {
                val mediaItem = MediaItem.fromUri(source.uri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
            is PlaybackSource.None -> {
                exoPlayer.stop()
            }
            null -> Unit
        }


    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Main player view
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    controllerAutoShow = false
                    useController = false
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
            }
        )
        
        // Custom controls using Compose
        exoplayerCustomPlayer(
            modifier = Modifier.fillMaxWidth(),
            exoPlayer = exoPlayer
        )
    }
}
