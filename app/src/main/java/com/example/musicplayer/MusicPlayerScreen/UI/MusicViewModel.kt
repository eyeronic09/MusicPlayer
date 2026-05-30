package com.example.musicplayer.MusicPlayerScreen.UI

import android.media.browse.MediaBrowser
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import com.example.musicplayer.MusicPlayerScreen.Service.AudioServiceHandler
import com.example.musicplayer.MusicPlayerScreen.Service.PlayerEvent
import com.example.musicplayer.MusicPlayerScreen.mapper.toMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MusicViewModel(
    private val audioService: AudioServiceHandler ,
    private val repository: MusicRepository,
    saveStateHandler: SavedStateHandle
): ViewModel() {

    var duration by saveStateHandler.saveable { mutableStateOf(0L) }
    var progress by saveStateHandler.saveable { mutableStateOf(0f) }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState : StateFlow<UiState> = _uiState.asStateFlow()


    init {
        loadAudioData()
    }

    fun onEvent(event: MusicEvent) {
        viewModelScope.launch {
            when (event) {
                MusicEvent.Backward -> {
                    audioService.onPlayerEvent(PlayerEvent.Backward)
                }
                MusicEvent.Forward -> {
                    audioService.onPlayerEvent(PlayerEvent.Forward)
                }
                MusicEvent.PlayPause -> {
                    audioService.onPlayerEvent(PlayerEvent.PlayPause)
                }
                is MusicEvent.SeekTo -> {
                    audioService.onPlayerEvent(PlayerEvent.SeekTo(event.position))
                }
                is MusicEvent.SelectedAudioChange -> {
                    audioService.onPlayerEvent(PlayerEvent.SelectedAudioChange((event.index.toInt())))
                }
                MusicEvent.Stop -> {
                    audioService.onPlayerEvent(PlayerEvent.Stop)
                }
                is MusicEvent.UpdateProgress -> {
                    audioService.onPlayerEvent(PlayerEvent.UpdateProgress(event.newProgress))
                }
            }
        }
    }



    private fun loadAudioData() {
        viewModelScope.launch {
            val audioList = repository.getAudioFiles()
            val mappedAudio = audioList.map {it.toMediaItem()}
            setMediaItems(mappedAudio)
            _uiState.value = UiState.Ready(audioList)
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
    data class SelectedAudioChange(val index : Float) : MusicEvent()
    object Forward : MusicEvent()
    object Backward : MusicEvent()
    object Stop : MusicEvent()
    data class SeekTo(val position: Long) : MusicEvent()
    data class UpdateProgress(val newProgress: Float) : MusicEvent()
}

sealed class UiState {
    object Initial : UiState()
    data class Ready(val audioList: List<AudioFile>) : UiState()
}