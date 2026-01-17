package com.example.musicplayer.HomeScreen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListofSong(viewModel: SongPlayerVM) {
    val songs by viewModel.songs.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(songs) { song ->
            Text(
                text = song.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        Log.d("ListofSong", "songs: $songs")
    }
}
