package com.example.musicplayer.PlayerScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerScreen(
    navController: NavHostController,
    viewModel: PlayerScreenVm = koinViewModel(),
    exoPlayer: ExoPlayer,
    folderId: Long
) {

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(folderId) {
        viewModel.onEvent(PlayerScreenUiEvent.LoadFolder(folderId))
    }
    
    LaunchedEffect(uiState.value.selectedFolderToPlay) {
        if (uiState.value.selectedFolderToPlay.isNotEmpty()) {
            Log.d("PlayerScreen", "Playing ${uiState.value.selectedFolderToPlay.size} files")
            //this is play in side the folder content
            val mediaItems = uiState.value.selectedFolderToPlay.map { uri ->
                MediaItem.fromUri(uri)
            }
            exoPlayer.setMediaItems(mediaItems)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
            }
        }
    )
}
