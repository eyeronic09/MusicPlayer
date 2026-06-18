package com.example.musicplayer.HomeScreen.Playlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel

class PlayListScreen : Screen {
    @Composable
    override fun Content() {
        PlayListScreenRoot()
    }


}

@Composable
fun PlayListScreenRoot(viewModel: PlayListScreenViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    PlaylistScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    uiState: PlayListScreenUIState,
    onEvent: (PlaylistEvent) -> Unit
) {

    val nav = LocalNavigator.currentOrThrow

    if (uiState.showPopup) {
        AlertDialog(
            onDismissRequest = {
                onEvent(PlaylistEvent.PopUp(false))
            },
            title = { Text(text = "New Playlist") },
            text = {
                Column {
                    TextField(
                        value = uiState.playListName,
                        onValueChange = { onEvent(PlaylistEvent.OnNameChange(it)) },
                        placeholder = { Text("Playlist Name") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(PlaylistEvent.CreatePlayList)
                    onEvent(PlaylistEvent.PopUp(false))

                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(PlaylistEvent.PopUp(false)) }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(PlaylistEvent.PopUp(true))
            },
                content = { Icon(imageVector = Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null) }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->

        when {
            uiState.playList.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(uiState.playList) { playlist ->
                        Text(
                            text = playlist.playListName,
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    nav.push(PlaylistAllSong(playlistid = playlist.playlistId.toInt()))
                                }
                        )
                    }
                }
            }
            uiState.playList.isEmpty() -> {
                Text(text = "text")
            }

        }
    }

}
