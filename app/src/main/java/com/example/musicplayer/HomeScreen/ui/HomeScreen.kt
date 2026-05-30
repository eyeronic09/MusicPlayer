package com.example.musicplayer.HomeScreen.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.MusicPlayerScreen.UI.MusicEvent
import org.koin.androidx.compose.koinViewModel

object HomeScreenTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        HomeScreenRoot()
    }
}

@Composable
fun HomeScreenRoot(viewModel: HomeScreenViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = uiState,
        onRefresh = { viewModel.loadSongs() }
    )
}

@Composable
fun HomeScreen(
    state: HomeScreenUIState,
    onRefresh: () -> Unit
) {
    HomeScreenContent(state = state)
}

@Composable
fun HomeScreenContent(state : HomeScreenUIState  ){
    Column(modifier = Modifier.fillMaxSize()) {
        when {
            state.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.ERROR?.isNotEmpty() == true -> {
                Text(text = "Error: ${state.ERROR}", modifier = Modifier.padding(16.dp))
            }
            else -> {
                LazyColumn {
                    items(state.SongList) { audio ->
                        AudioItem(audio = audio)
                    }
                }
            }
        }
    }
}
@Composable
fun AudioItem(audio: AudioFile) {
    Text(text = audio.displayName, modifier = Modifier.padding(16.dp))
}
