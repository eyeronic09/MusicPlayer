package com.example.musicplayer.PlayerScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel

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

    
    LaunchedEffect(uiState.value) {
        Log.d("PlayerScreen", "State changed - playSpecificAudio: ${uiState.value.playSpecificAudio}, playSpecificAudioUri: ${uiState.value.playSpecificAudioUri}")
        when {
            uiState.value.selectedFolderToPlay.isNotEmpty() -> {
                Log.d("PlayerScreen", "Playing ${uiState.value.selectedFolderToPlay.size} files")
                val mediaItems = uiState.value.selectedFolderToPlay.map { uri ->
                    MediaItem.fromUri(uri)
                }
                exoPlayer.setMediaItems(mediaItems)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true

            }
            uiState.value.selectedFolderToPlayUri == null && uiState.value.playSpecificAudioUri != null && uiState.value.playSpecificAudio -> {
                Log.d("PlayerScreen", "Playing specific audio")
                val mediaItem = MediaItem.fromUri(uiState.value.playSpecificAudioUri!!)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
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
