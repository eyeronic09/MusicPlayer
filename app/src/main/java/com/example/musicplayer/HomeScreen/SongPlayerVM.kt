package com.example.musicplayer.HomeScreen

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Song(
    val uri: Uri,
    val name: String
)

class SongPlayerVM(application: Application) : AndroidViewModel(application) {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    var folderUri by mutableStateOf<Uri?>(null)
        private set

    fun PlayMusic(uri: Uri) {
        folderUri = uri
        loadSongsFromFolder(uri)
    }

    private fun loadSongsFromFolder(uri: Uri) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val directory = DocumentFile.fromTreeUri(context , uri)
            val files = directory?.listFiles() ?: emptyArray()

            _songs.value = files.asList()
                .map { Song(it.uri, it.name ?: "Unknown") }
        }
    }
}
