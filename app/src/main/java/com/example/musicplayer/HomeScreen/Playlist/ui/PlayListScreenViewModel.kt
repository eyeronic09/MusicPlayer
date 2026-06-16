package com.example.musicplayer.HomeScreen.Playlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayListScreenUIState(
    val playList : List<PlayList> = emptyList(),
    val ERROR: String? = "",
    val Loading: Boolean = false,
    val showPopup : Boolean = false,
    val playListName : String = "",
    val str : String? = ""

)
sealed interface PlaylistEvent {
    data class PopUp(val show : Boolean) : PlaylistEvent
    object CreatePlayList : PlaylistEvent
    data class OnNameChange(val name: String) : PlaylistEvent
}
class PlayListScreenViewModel (
    private val repo : PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayListScreenUIState())
    val uiState : StateFlow<PlayListScreenUIState> = _uiState.asStateFlow()

    init {
        loadPlaylist()
    }

    fun onEvent(event: PlaylistEvent){
        when (event){
            is PlaylistEvent.PopUp -> {
                popUp(event.show)
            }

            PlaylistEvent.CreatePlayList -> {
                CreatePlaylist()
            }

            is PlaylistEvent.OnNameChange -> {
                _uiState.update { it.copy(playListName = event.name) }
            }
        }
    }

    private fun popUp(show: Boolean){
        _uiState.update { it.copy(showPopup = show , playListName = "") }
    }

    private fun CreatePlaylist(){
        viewModelScope.launch {
            val playlist = PlayList(
                    playListName = _uiState.value.playListName.ifEmpty { "new Playlist" }
                    )
            repo.insertPlaylist(playlist)
            loadPlaylist()
        }

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