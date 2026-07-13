package com.example.musicplayer.MusicPlayerScreen.UI

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import coil.compose.AsyncImage
import com.example.musicplayer.HomeScreen.compontent.MediaPlayerController
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.ui.timeStampToDuration
import com.example.musicplayer.R
import kotlinx.coroutines.flow.MutableStateFlow
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
        val tabNavigator = LocalTabNavigator.current

        // The 'key' function forces the entire UI inside it to be RECREATED 
        // whenever the value of the key changes. Using tabNavigator.current
        // ensures a fresh recomposition whenever you switch to this tab.
        key(tabNavigator.current) {
            val viewModel: MusicViewModel = koinViewModel()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is uiToastMessage.meassage -> {
                            Toast.makeText(context, effect.msg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            MediaPlayerScreenContent(
                displayName = viewModel.displayName,
                progress = viewModel.progress,
                onProgress = { viewModel.onEvent(MusicEvent.UpdateProgress(it)) },
                isAudioPlaying = viewModel.isPlaying,
                audio = viewModel.currentSelectedAudio,
                repeatMode = viewModel.repeatMode,
                isShuffleEnabled = viewModel.isShuffleEnabled,
                onStart = { viewModel.onEvent(MusicEvent.PlayPause) },
                onNext = { viewModel.onEvent(MusicEvent.SeekToNext) },
                onPrevious = {
                    viewModel.onEvent(MusicEvent.SeekToPrevious)
                },
                onRepeat = { viewModel.onEvent(MusicEvent.ToggleRepeat) },
                onShuffle = { viewModel.onEvent(MusicEvent.ToggleShuffle) },
                onSleepTimer = { viewModel.onEvent(MusicEvent.SleepTimer(it)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerScreenContent(
    displayName: String,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    audio: AudioFile,
    repeatMode: Int,
    isShuffleEnabled: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRepeat: () -> Unit,
    onShuffle: () -> Unit,
    onSleepTimer: (Int) -> Unit
) {
    var expand by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    Box {
                        IconButton(onClick = { expand = true }) {
                            Icon(Icons.Default.Timer, contentDescription = "Sleep Timer")
                        }
                        DropdownMenu(
                            expanded = expand,
                            onDismissRequest = { expand = false }
                        ) {
                            val options = listOf(
                                "Off" to null,
                                "5 min" to 5,
                                "15 min" to 15,
                                "30 min" to 30,
                                "60 min" to 60
                            )

                            options.forEach { (label, minutes) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        expand = false
                                        onSleepTimer(minutes?.let { it * 60 * 1000 } ?: 0)
                                        Toast.makeText(context, "Timer set for $label", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(320.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.baseline_music_note_24),
                error = painterResource(R.drawable.baseline_music_note_24),
                model = audio.albumArtUri,
                contentDescription = "Album art"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = audio.artist,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Slider(
                value = progress,
                onValueChange = onProgress,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = timeStampToDuration((progress * audio.duration).toLong()),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = timeStampToDuration(audio.duration.toLong()),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                MediaPlayerController(
                    isAudioPlaying = isAudioPlaying,
                    onStart = onStart,
                    onNext = onNext,
                    onPrevious = onPrevious
                )

                IconButton(onClick = onRepeat) {
                    val icon = if (repeatMode == Player.REPEAT_MODE_ONE)
                        Icons.Default.RepeatOne else Icons.Default.Repeat

                    val tint = if (repeatMode == Player.REPEAT_MODE_OFF)
                        MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary

                    Icon(
                        imageVector = icon,
                        contentDescription = "Repeat",
                        tint = tint
                    )
                }
            }
        }
    }


}





@Preview(showBackground = true)
@Composable
fun MediaPlayerScreenContentPreview() {
    MediaPlayerScreenContent(
        displayName = "Sample Song",
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
        repeatMode = Player.REPEAT_MODE_OFF,
        isShuffleEnabled = false,
        onStart = {},
        onNext = {},
        onPrevious = {},
        onRepeat = {},
        onShuffle = {},
        onSleepTimer = {}
    )
}
