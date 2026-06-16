package com.example.musicplayer.HomeScreen.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import com.google.common.base.Objects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeScreenUIState(
    val SongList : List<AudioFile> = emptyList(),
    val ERROR: String? = "",
    val Loading: Boolean = false

)

class HomeScreenViewModel(private val repository: MusicRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenUIState>(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState.asStateFlow()

    init {
        loadSongs()
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
}
