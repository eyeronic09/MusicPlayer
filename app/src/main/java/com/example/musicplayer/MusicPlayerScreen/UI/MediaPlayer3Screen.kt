package com.example.musicplayer.MusicPlayerScreen.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
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
            onNext = { viewModel.onEvent(MusicEvent.SeekToNext) }
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
    onNext: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBarPlayer(
                progress = progress,
                onProgress = onProgress,
                audio = audio,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Now Playing: ${audio.displayName}", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: AudioFile,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
) {
    BottomAppBar(
        modifier = Modifier.height(110.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArtistInfo(
                    audio = audio,
                    modifier = Modifier.weight(1f),
                )
                MediaPlayerController(
                    isAudioPlaying = isAudioPlaying,
                    onStart = onStart,
                    onNext = onNext
                )
            }
            Slider(
                value = progress,
                onValueChange = { onProgress(it) },
                valueRange = 0f..1f
            )
        }
    }
}

@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    audio: AudioFile,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIconItem(
            icon = Icons.Default.MusicNote,
            borderStroke = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {}
        Spacer(modifier = Modifier.size(4.dp))
        Column {
            Text(
                text = audio.displayName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlayerIconItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    borderStroke: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            },
        contentColor = color,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}

@Composable
fun MediaPlayerController(
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp)
    ) {
        PlayerIconItem(
            icon = if (isAudioPlaying) Icons.Default.Pause
            else Icons.Default.PlayArrow
        ) {
            onStart()
        }
        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            imageVector = Icons.Default.SkipNext,
            modifier = Modifier.clickable {
                onNext()
            },
            contentDescription = null
        )
    }
}
