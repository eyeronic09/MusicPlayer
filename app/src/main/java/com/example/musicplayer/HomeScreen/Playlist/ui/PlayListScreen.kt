package com.example.musicplayer.HomeScreen.Playlist.ui

import android.app.LauncherActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.example.musicplayer.HomeScreen.ui.HomeScreenUIState
import com.example.musicplayer.HomeScreen.ui.HomeScreenViewModel
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
    PlaylistScreen(uiState)
}

@Composable
fun PlaylistScreen(uiState: PlayListScreenUIState) {
    LazyColumn(modifier = Modifier.fillMaxSize()){
        items(uiState.playList){ it ->
            it.playListName
        }
    }
}
