package com.example.musicplayer.MusicPlayerScreen.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import coil.compose.AsyncImage
import com.example.musicplayer.HomeScreen.compontent.BottomBarPlayer
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.R
import org.koin.androidx.compose.koinViewModel

object MediaPlayerTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.PlayArrow)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Player",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val viewModel: MusicViewModel = koinViewModel()
        MediaPlayerScreenContent(
            progress = viewModel.progress,
            onProgress = { viewModel.onEvent(MusicEvent.UpdateProgress(it)) },
            isAudioPlaying = viewModel.isPlaying,
            audio = viewModel.currentSelectedAudio,
            onStart = { viewModel.onEvent(MusicEvent.PlayPause) },
            onNext = { viewModel.onEvent(MusicEvent.SeekToNext) },
            onPrevious = {
                viewModel.onEvent(MusicEvent.SeekToPrevious)
            },
            onRepeat = {

            }

        )
    }
}

@Composable
fun MediaPlayerScreenContent(
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    audio: AudioFile,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious : () -> Unit,
    onRepeat : () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBarPlayer(
                progress = progress,
                onProgress = onProgress,
                audio = audio,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
                onPrevious = onPrevious,
                onRepeat = onRepeat,
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(300.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.baseline_music_note_24),
                error = painterResource(R.drawable.baseline_music_note_24),
                model = audio.albumArtUri,
                contentDescription = "Album art for ${audio.album}"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MediaPlayerScreenContentPreview() {
    MediaPlayerScreenContent(
        progress = 0.5f,
        onProgress = {},
        isAudioPlaying = true,
        audio = AudioFile(
            id = 1L,
            albumIdForArt = 1L,
            displayName = "Sample Song",
            artist = "Sample Artist",
            album = "Sample Album",
            duration = 300000
        ),
        onStart = {},
        onNext = {},
        onPrevious = {},
        onRepeat = {}
    )
}
