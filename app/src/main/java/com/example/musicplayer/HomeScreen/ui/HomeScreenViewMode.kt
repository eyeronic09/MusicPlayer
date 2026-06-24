package com.example.musicplayer.HomeScreen.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import com.example.musicplayer.HomeScreen.ui.HomeUiEffect.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUIState(
    val allSongs : List<AudioFile> = emptyList(),
    val songList : List<AudioFile> = emptyList(),
    val playlist : List<PlayList> = emptyList(),
    val filteredItem : List<AudioFile> = emptyList(),
    val searchedQuery : String = "",
    val selectedAudioId : Int? = null,
    val isSearching : Boolean = false,
    val selectedPlayList : Int? = null,
    val ERROR: String? = "",
    val Loading: Boolean = false
)


sealed interface HomeUiEffect {
    data class showToast(val message : String) : HomeUiEffect

}
sealed interface HomeEvent {
    object OpenSearchBar : HomeEvent
    object CloseSearchBar : HomeEvent
    data class OnSearchQueryChange(val query: String) : HomeEvent
    data class SelectedSongId(val id : Int) : HomeEvent
    data class SelectedPlayList(val id : Int) : HomeEvent
    data class AddToPlaylist(val Audio: AudioFile, val playlistId: Long) : HomeEvent
}
class HomeScreenViewModel(private val repository: MusicRepository , private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState.asStateFlow()


    private val _uiEffect = MutableSharedFlow<HomeUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()


    init {
        loadSongs()
    }


    private fun loadPlaylist() {
        viewModelScope.launch {
                playlistRepository.getAllPlayList().collectLatest { playlist ->
                    _uiState.update {
                        it.copy(playlist = playlist)
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
                            allSongs = song,
                            Loading = false,
                            songList = if (it.searchedQuery.isEmpty()) song else it.songList
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error loading songs", e)
                _uiState.update { it.copy(Loading = false, ERROR = e.localizedMessage) }
            }
        }
    }

    private fun filterSongs(query: String) {
        if (query.isEmpty()){
            _uiState.update {
                it.copy(filteredItem = emptyList(),
                    isSearching = false,
                    searchedQuery = "")
            }
        }else {
            val filtered =  _uiState.value.allSongs.filter { song -> song.displayName.contains(query , ignoreCase = true) }
            Log.d("filterCheck", "Query: $query, Found: ${filtered.size}")
            _uiState.update {
                it.copy(
                    filteredItem = filtered,
                    isSearching = true,
                    searchedQuery = query
                )
            }
        }

    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectedSongId -> {
                _uiState.update {
                    it.copy(selectedAudioId = event.id)
                }

            }
            is HomeEvent.SelectedPlayList -> {
                _uiState.update {
                    it.copy(selectedPlayList = event.id)
                }
            }
            is HomeEvent.AddToPlaylist -> {
                viewModelScope.launch {
                    playlistRepository.insertSongFromPlaylist(audioFile = event.Audio ,
                        event.playlistId
                    )
                    _uiEffect.emit(showToast("Added  to playlist"))
                }
            }

            HomeEvent.OpenSearchBar -> {
                _uiState.update { it.copy(isSearching = true)  }
            }

            HomeEvent.CloseSearchBar -> {
                _uiState.update { it.copy(isSearching = false) }
            }
            is HomeEvent.OnSearchQueryChange -> {
                _uiState.update {
                    it.copy(
                        searchedQuery = event.query,
                    )
                }
                filterSongs(event.query)
                Log.d("filteredItem" , uiState.value.filteredItem.toString())
            }
        }
    }
}
