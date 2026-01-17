package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.musicplayer.HomeScreen.ListofSong
import com.example.musicplayer.HomeScreen.PickAudioFolder
import com.example.musicplayer.HomeScreen.SongPlayerVM
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                val viewModel: SongPlayerVM = koinViewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.folderUri == null) {
                            PickAudioFolder(viewModel = viewModel)
                        } else {
                            ListofSong(viewModel = viewModel)
                        }

                    }
                }
            }
        }
    }
}
