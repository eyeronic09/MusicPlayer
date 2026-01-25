package com.example.musicplayer.PlayerScreen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class PlayerScreenUiState(
    val selectedFolderToPlay: List<Uri> = emptyList(),
    val selectedFolderToPlayUri: Uri? = null,
    val playSpecificAudioUri : Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val folders: List<String> = emptyList(),
    val playSpecificAudio: Boolean = false
)

sealed interface PlayerScreenUiEvent{
    data class LoadFolder(val folderId: Long) : PlayerScreenUiEvent
    data class UserSpecificAudio(val uri: Uri) : PlayerScreenUiEvent
    object ResetSpecificAudio : PlayerScreenUiEvent
}
class PlayerScreenVm(
    private val repository: FolderRepository,
    @SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel() {


    private val _uiState = MutableStateFlow(PlayerScreenUiState())
    val uiState: StateFlow<PlayerScreenUiState> = _uiState.asStateFlow()

    fun onEvent(event: PlayerScreenUiEvent) {
        when (event) {
            is PlayerScreenUiEvent.LoadFolder -> {
                loadFolder(event)
            }
            is PlayerScreenUiEvent.UserSpecificAudio ->{
                getSpecificAudio(event)
                _uiState.update { it.copy(playSpecificAudio = true) }
                Log.d("PlayerScreenVm", "UserSpecificAudio: ${event.uri}")
            }
            PlayerScreenUiEvent.ResetSpecificAudio -> {
                _uiState.update { it.copy(playSpecificAudio = false) }
            }
        }
    }

    private fun loadFolder(loadFolder: PlayerScreenUiEvent.LoadFolder ) {
        viewModelScope.launch {
            try {
                val folder = repository.getSelectedFolder(loadFolder.folderId.toInt())
                _uiState.update { it ->
                    it.copy(
                        playSpecificAudio = false,
                        selectedFolderToPlayUri = folder?.folderUri?.toUri(),
                        selectedFolderToPlay = folder?.folderUri?.toUri()?.let { getAllFiles(it) } ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun getAllFiles(folderUri: Uri) : List<Uri> {
        return try {
            val folderDoc = DocumentFile.fromTreeUri(context, folderUri)
            val files = folderDoc?.listFiles()?.map { it.uri } ?: emptyList()
            Log.d("PlayerScreenVm", "Found ${files.size} files in folder")
            files
        } catch (e: Exception) {
            Log.e("PlayerScreenVm", "Error getting files: ${e.message}")
            emptyList()
        }
    }

    private fun getSpecificAudio(event: PlayerScreenUiEvent.UserSpecificAudio) {
        _uiState.update { it ->
            it.copy(
                playSpecificAudio = true,
                playSpecificAudioUri = event.uri,
                selectedFolderToPlayUri = null,
                selectedFolderToPlay = emptyList()
            )
        }
    }

}



