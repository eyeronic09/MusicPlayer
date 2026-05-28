package com.example.musicplayer.HomeScreen.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        androidx.compose.material3.Button(onClick = onRefresh) {
            Text("Refresh List")
        }
        Text(text = "Welcome to Home", modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn {
            items(state.SongList) { song ->
                Text(text = "${song.displayName} - ${song.artist}")
            }
        }
    }
    Log.d("HomeScreen", "Current song list size: ${state.SongList.size}")
}
