package com.example.musicplayer.HomeScreen.Playlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.musicplayer.HomeScreen.compontent.AudioItem
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.MusicPlayerScreen.UI.MusicEvent
import com.example.musicplayer.MusicPlayerScreen.UI.MusicViewModel
import com.example.musicplayer.ui.theme.MusicPlayerTheme
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

    PlayListAllSongContents(uiState = uiState, playlistId = id)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListAllSongContents(
    uiState: PlaylistAllSongUIState,
    playlistId: Long,
    viewModel: MusicViewModel = koinViewModel()
) {
    val nav = LocalNavigator.currentOrThrow
    val onEvent = viewModel::onEvent

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Playlist Details") },
                navigationIcon = {
                    IconButton(onClick = { nav.pop() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom player
        ) {
            // --- HEADER SECTION ---
            item {
                PlaylistHeader(
                    playlistName = "Playlist Songs",
                    songCount = uiState.allSongs.size,
                    onPlayAll = {
                        if (uiState.allSongs.isNotEmpty()) {
                            onEvent(MusicEvent.PlayPlaylist(playlist = playlistId)) 
                        }
                    },
                    onShuffle = {
                        if (uiState.allSongs.isNotEmpty()) {
                            onEvent(MusicEvent.ShufflePlaylist(playlist = playlistId))
                        }
                    }
                )
            }

            // --- SONG LIST SECTION ---
            if (uiState.allSongs.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyPlaylistViews()
                }
            } else {
                itemsIndexed(uiState.allSongs) { index, audio ->
                    AudioItem(
                        audio = audio,
                        onItemClick = {
                            onEvent(MusicEvent.PlayOnlySong(audio))
                        },
                        isSelected = { viewModel.currentSelectedAudio.id == audio.id }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistHeader(
    playlistName: String,
    songCount: Int,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Playlist Icon
        Card(
            modifier = Modifier.size(180.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name and Stats
        Text(
            text = playlistName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$songCount Songs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onPlayAll,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Play All")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = onShuffle,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(Icons.Default.Shuffle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Shuffle")
            }
        }
    }
}

@Composable
fun EmptyPlaylistViews() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.LibraryMusic,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            "No songs in this playlist",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayListAllSongRootPreview() {
    MusicPlayerTheme {
        PlayListAllSongContents(
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
            ),
            playlistId = 1L
        )
    }
}
