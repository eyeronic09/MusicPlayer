package com.example.musicplayer.MusicPlayerScreen.UI

import java.util.concurrent.TimeUnit
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import com.example.musicplayer.MusicPlayerScreen.Service.AudioServiceHandler
import com.example.musicplayer.MusicPlayerScreen.Service.AudioState
import com.example.musicplayer.MusicPlayerScreen.Service.PlayerEvent
import com.example.musicplayer.MusicPlayerScreen.mapper.toMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val audioDummy = AudioFile(
    id = 1,
    displayName = "",
    artist = "",
    album = "",
    duration = 0
)

@OptIn(SavedStateHandleSaveableApi::class)
class MusicViewModel(
    private val audioService: AudioServiceHandler,
    private val repository: MusicRepository,
    saveStateHandler: SavedStateHandle
) : ViewModel() {

    var duration by saveStateHandler.saveable { mutableStateOf(0L) }
    var progress by saveStateHandler.saveable { mutableStateOf(0f) }
    var progressString by saveStateHandler.saveable { mutableStateOf("00:00") }
    var isPlaying by saveStateHandler.saveable { mutableStateOf(false) }
    var currentSelectedAudio by saveStateHandler.saveable { mutableStateOf(audioDummy) }
    var audioList by saveStateHandler.saveable { mutableStateOf(listOf<AudioFile>()) }


    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    init {
        loadAudioData()
        viewModelScope.launch {
            audioService.playerState.collectLatest { state ->
                when (state) {
                    is AudioState.Buffering -> calculateProgress(state.progress)
                    is AudioState.CurrentPlaying -> {
                        if (audioList.isNotEmpty()) {
                            currentSelectedAudio = audioList[state.mediaItemIndex]
                        }
                    }
                    AudioState.Initial -> _uiState.value = UiState.Initial
                    is AudioState.Playing -> {
                        isPlaying = state.isPlaying
                    }
                    is AudioState.Progress -> {
                        calculateProgress(state.progress)
                    }
                    is AudioState.Ready -> {
                        duration = state.duration
                        _uiState.value = UiState.Ready
                    }
                }
            }
        }
    }

    fun onEvent(event: MusicEvent) {
        viewModelScope.launch {
            when (event) {
                MusicEvent.Backward -> audioService.onPlayerEvents(PlayerEvent.Backward)
                MusicEvent.Forward -> audioService.onPlayerEvents(PlayerEvent.Forward)
                MusicEvent.SeekToNext -> audioService.onPlayerEvents(PlayerEvent.SeekToNext)
                MusicEvent.SeekToPrevious -> audioService.onPlayerEvents(PlayerEvent.SeekToPrevious)
                MusicEvent.PlayPause -> audioService.onPlayerEvents(PlayerEvent.PlayPause)
                MusicEvent.Stop -> audioService.onPlayerEvents(PlayerEvent.Stop)

                is MusicEvent.SeekTo -> {
                    audioService.onPlayerEvents(
                        PlayerEvent.SeekTo,
                        seekPosition = (duration * event.seekto).toLong()
                    )
                }

                is MusicEvent.SelectedAudioChange -> {
                    audioService.onPlayerEvents(
                        PlayerEvent.SelectedAudioChange,
                        selectedAudioIndex = event.index
                    )
                }

                is MusicEvent.UpdateProgress -> {
                    audioService.onPlayerEvents(
                        PlayerEvent.UpdateProgress(event.newProgress)
                    )
                    progress = event.newProgress
                }

                is MusicEvent.LongUpdateProgress -> {
                    audioService.onPlayerEvents(
                        PlayerEvent.SeekTo,
                        seekPosition = event.newProgress
                    )
                }
                is MusicEvent.SelectedAudioIndex -> {
                    audioService.onPlayerEvents(
                        PlayerEvent.SelectedAudioChange,
                        selectedAudioIndex = event.index
                    )
                }
            }
        }
    }

    private fun calculateProgress(currentProgress: Long) {
        progress = if (currentProgress > 0 && duration > 0) {
            (currentProgress.toFloat() / duration.toFloat())
        } else 0f
    }

    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) - 
                TimeUnit.SECONDS.convert(minutes, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun loadAudioData() {
        viewModelScope.launch {
            val audioFiles = repository.getAudioFiles()
            audioList = audioFiles
            val mappedAudio = audioFiles.map { it.toMediaItem() }
            setMediaItems(mappedAudio)
        }
    }

    private fun setMediaItems(audioList: List<MediaItem>) {
        viewModelScope.launch {
            audioService.setMediaItems(
                mediaItems = audioList
            )
        }
    }
}


sealed class MusicEvent {
    object PlayPause : MusicEvent()
    data class SelectedAudioChange(val index: Int) : MusicEvent()
    object Backward : MusicEvent()
    object SeekToNext : MusicEvent()
    object SeekToPrevious : MusicEvent()
    object Forward : MusicEvent()
    data class UpdateProgress(val newProgress: Float) : MusicEvent() // Percentage (0.0 - 1.0)
    data class LongUpdateProgress(val newProgress: Long) : MusicEvent() // Absolute position (ms)
    object Stop : MusicEvent()
    data class SeekTo(val seekto: Float) : MusicEvent()
    data class SelectedAudioIndex(val index: Int) : MusicEvent()
}

sealed class UiState {
    object Initial : UiState()
    object Ready : UiState()
}
