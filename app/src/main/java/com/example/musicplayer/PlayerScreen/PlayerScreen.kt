@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.example.musicplayer.PlayerScreen

import android.util.Log
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
import androidx.compose.ui.platform.LocalGraphicsContext
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
                val mediaItems = source.files.map {
                    MediaItem.fromUri(it)
                }
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
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {

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