package com.example.musicplayer.HomeScreen.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.musicplayer.HomeScreen.compontent.AudioItem
import com.example.musicplayer.HomeScreen.compontent.BottomBarPlayer
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.MusicPlayerScreen.UI.MusicEvent
import com.example.musicplayer.MusicPlayerScreen.UI.MusicViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.floor

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
fun HomeScreenRoot(viewModel: MusicViewModel = koinViewModel()) {
    HomeScreen(
        progress = viewModel.progress,
        onProgress = { viewModel.onEvent(MusicEvent.UpdateProgress(it)) },
        isAudioPlaying = viewModel.isPlaying,
        currentPlayingAudio = viewModel.currentSelectedAudio,
        audiList = viewModel.audioList,
        onStart = { viewModel.onEvent(MusicEvent.PlayPause) },
        onItemClick = { viewModel.onEvent(MusicEvent.SelectedAudioChange(it)) },
        onNext = { viewModel.onEvent(MusicEvent.SeekToNext) },
        onPrevious = { viewModel.onEvent(MusicEvent.SeekToPrevious) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: AudioFile,
    audiList: List<AudioFile>,
    onStart: () -> Unit,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            BottomBarPlayer(
                progress = progress,
                onProgress = onProgress,
                audio = currentPlayingAudio,
                onStart = onStart,
                onNext = onNext,
                isAudioPlaying = isAudioPlaying,
                onPrevious = onPrevious,
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(audiList) { index, audio ->
                Log.d("HomeScreen", "Rendering index: $index, audio: ${audio.displayName}")
                AudioItem(
                    audio = audio,
                    isSelected = audio.id == currentPlayingAudio.id,
                    onItemClick = { onItemClick(index) }
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockAudioList = listOf(
        AudioFile(id = 1, albumIdForArt = 1, displayName = "Song One", artist = "Artist A", album = "Album 1", duration = 120000),
        AudioFile(id = 2, albumIdForArt = 2, displayName = "Song Two", artist = "Artist B", album = "Album 2", duration = 180000),
        AudioFile(id = 3, albumIdForArt = 3, displayName = "Song Three", artist = "Artist C", album = "Album 3", duration = 240000)
    )

    Surface {
        HomeScreen(
            progress = 0.5f,
            onProgress = {},
            isAudioPlaying = true,
            currentPlayingAudio = mockAudioList[0],
            audiList = mockAudioList,
            onStart = {},
            onItemClick = {},
            onNext = {},
            onPrevious = {}
        )
    }
}

 fun timeStampToDuration(position: Long): String {
    val totalSecond = floor(position / 1E3).toInt()
    val minutes = totalSecond / 60
    val remainingSeconds = totalSecond - (minutes * 60)
    return if (position < 0) "--:--"
    else "%d:%02d".format(minutes, remainingSeconds)
}
