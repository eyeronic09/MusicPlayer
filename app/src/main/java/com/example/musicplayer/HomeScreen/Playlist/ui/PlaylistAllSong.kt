package com.example.musicplayer.HomeScreen.Playlist.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


import androidx.compose.ui.tooling.preview.Preview
import com.example.musicplayer.HomeScreen.compontent.AudioItem
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.MusicPlayerScreen.UI.MusicEvent
import com.example.musicplayer.MusicPlayerScreen.UI.MusicViewModel
import com.example.musicplayer.ui.theme.MusicPlayerTheme

class PlaylistAllSong(val playlist: Long = -1L) : Screen {
    @Composable
    override fun Content() {
        PlayListAllSongRoot(
            id = playlist
        )
    }

}

@Composable
fun PlayListAllSongRoot(id: Long) {
    val viewModel: PlaylistAllSongViewModel = koinViewModel { parametersOf(id) }
    val uiState by viewModel.uiState.collectAsState()

    PlayListAllSongContent(uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListAllSongContent(uiState: PlaylistAllSongUIState , viewModel: MusicViewModel = koinViewModel()) {
    val onEvent = viewModel::onEvent


    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "Playlist") }
            )
        }
    ){ paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(uiState.allSongs) { song ->
                AudioItem(
                    audio = song,
                    onItemClick = {
                        onEvent(MusicEvent.PlayOnlySong(audio = song))
                    },
                    isSelected = song.id == viewModel.currentSelectedAudio.id
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayListAllSongRootPreview() {
    MusicPlayerTheme {
        PlayListAllSongContent(
            uiState = PlaylistAllSongUIState(
                allSongs = listOf(
                    AudioFile(
                        id = 1L,
                        albumIdForArt = 1L,
                        displayName = "Sample Song 1",
                        artist = "Sample Artist 1",
                        album = "Sample Album 1",
                        duration = 300000,
                    ),
                    AudioFile(
                        id = 2L,
                        albumIdForArt = 2L,
                        displayName = "Sample Song 2",
                        artist = "Sample Artist 2",
                        album = "Sample Album 2",
                        duration = 240000,
                    )
                )
            )
        )
    }
}
