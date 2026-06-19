package com.example.musicplayer.HomeScreen.Playlist.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


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


    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
           items(uiState.allSongs) { song ->
               Text(text = song.displayName)
           }
        }
    }
}
