package com.example.musicplayer.HomeScreen.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUIState(
    val SongList : List<AudioFile> = emptyList(),
    val Playlist : List<PlayList> = emptyList(),
    val selectedAudioId : Int? = null,
    val selectedPlayList : Int? = null,
    val ERROR: String? = "",
    val Loading: Boolean = false

)

sealed interface HomeEvent {
    data class selectedSongId(val id : Int) : HomeEvent
    data class selectedPlayList(val id : Int) : HomeEvent
    data class AddToPlaylist(val Audio: AudioFile, val playlistId: Long) : HomeEvent

}
class HomeScreenViewModel(private val repository: MusicRepository , private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenUIState>(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState.asStateFlow()

    init {
        loadSongs()
        loadPlaylist()
    }


    private fun loadPlaylist() {
        viewModelScope.launch {
                playlistRepository.getAllPlayList().collectLatest { playlist ->
                    _uiState.update {
                        it.copy(Playlist = playlist)
                    }
            }
        }
    }

    fun loadSongs() {
        viewModelScope.launch {
            _uiState.update { it.copy(Loading = true) }
            try {
                repository.getAllAudioFilesFromDb().collect { song ->
                    _uiState.update {
                        it.copy(
                            SongList = song,
                            Loading = false
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error loading songs", e)
                _uiState.update { it.copy(Loading = false, ERROR = e.localizedMessage) }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.selectedSongId -> {
                _uiState.update {
                    it.copy(selectedAudioId = event.id)
                }
            }
            is HomeEvent.selectedPlayList -> {
                _uiState.update {
                    it.copy(selectedPlayList = event.id)
                }
            }
            is HomeEvent.AddToPlaylist -> {
                viewModelScope.launch {
                    playlistRepository.insertSongFromPlaylist(audioFile = event.Audio ,
                        event.playlistId
                    )
                }
            }
        }
    }
}
