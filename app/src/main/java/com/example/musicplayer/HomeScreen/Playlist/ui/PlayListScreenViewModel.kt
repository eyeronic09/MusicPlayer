package com.example.musicplayer.HomeScreen.Playlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.ui.HomeScreenUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayListScreenUIState(
    val playList : List<PlayList> = emptyList(),
    val ERROR: String? = "",
    val Loading: Boolean = false

)
class PlayListScreenViewModel (
    private val repo : PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayListScreenUIState())
    val uiState : StateFlow<PlayListScreenUIState> = _uiState.asStateFlow()

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            _uiState.update { it.copy(Loading = true) }
            try {
                repo.getAllPlayList().collect { playLists ->
                    _uiState.update {
                        it.copy(
                            playList = playLists,
                            Loading = false
                        )
                    }
                }
            }catch (e: Exception){
                _uiState.update { it.copy(Loading = false) }
            }
        }
    }
}