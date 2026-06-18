package com.example.musicplayer.HomeScreen.Playlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaylistAllSongUIState(
    val allSongs: List<AudioFile> = emptyList(),
    val isLoading: Boolean = false,
)

class PlaylistAllSongViewModel(
    private val playlistRepository: PlaylistRepository,
    private val playlistId : Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistAllSongUIState())
    val uiState: StateFlow<PlaylistAllSongUIState> = _uiState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

        }
    }
}