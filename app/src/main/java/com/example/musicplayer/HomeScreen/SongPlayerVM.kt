package com.example.musicplayer.HomeScreen

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.core.net.toUri

/**
 * Data class representing a music file.
 */
data class Song(val name : String, val uri : Uri)

class SongPlayerVM(application: Application) : AndroidViewModel(application) {
    // A reactive stream of songs that the UI can observe
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    // Holds the current folder URI. Being a 'mutableStateOf', it triggers UI updates in Compose.
    var folderUri by mutableStateOf<Uri?>(null)
        private set

    // Opens a private storage file named "MusicPlayerPrefs" to save and load settings.
    private val sharedPreferences = application.getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE)

    init {
        // Automatically check for a previously saved folder as soon as the app starts.
        loadSavedFolder()
    }

    /**
     * Checks if a folder was previously selected and if the app still has permission to read it.
     */
    private fun loadSavedFolder() {
        // Step 1: Look for a saved URI string under the key "folder_uri".
        val savedUriString = sharedPreferences.getString("folder_uri", null)

        if (savedUriString != null) {
            val savedUri = savedUriString.toUri()

            // Step 2: Verify with the Android System if our "Persistable" permission is still active.
            // This is necessary in case the user revoked permissions in system settings.
            val hasPermission = getApplication<Application>().contentResolver.persistedUriPermissions.any {
                it.uri == savedUri && it.isReadPermission && it.isWritePermission
            }

            // Step 3: If valid, update the state and load the songs immediately.
            if (hasPermission) {
                folderUri = savedUri
                loadSongsFromFolder(savedUri)
            }
        }
    }

    /**
     * Called when the user picks a folder. Saves the URI for future use and starts playback logic.
     */
    fun PlayMusic(uri: Uri) {
        // Step 1: Persist the URI string to the "MusicPlayerPrefs" file.
        // This ensures the app "remembers" this folder next time it is opened.
        sharedPreferences.edit { putString("folder_uri", uri.toString()) }

        // Step 2: Update the UI state so the screen switches from the Picker to the List.
        folderUri = uri

        // Step 3: Start scanning the folder for music files.
        loadSongsFromFolder(uri)
    }

    /**
     * Scans the selected folder for audio files in a background thread.
     */
    private fun loadSongsFromFolder(uri: Uri) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext

            // Access the directory using the Storage Access Framework.
            val directory = DocumentFile.fromTreeUri(context, uri)
            val files = directory?.listFiles() ?: emptyArray()

            // Convert the files found in the folder into our list of Song objects.
            _songs.value = files.asList()
                // You can uncomment the filter line to only show music files:
                // .filter { it.name?.endsWith(".mp3", ignoreCase = true) == true }
                .map { Song(uri = it.uri, name = it.name ?: "Unknown") }
        }
    }
}