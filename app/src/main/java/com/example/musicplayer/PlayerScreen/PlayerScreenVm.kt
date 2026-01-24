package com.example.musicplayer.PlayerScreen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class PlayerScreenUiState(
    val selectedFolderToPlay: List<Uri> = emptyList(),
    val selectedFolderToPlayUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val folders: List<String> = emptyList()
)

sealed interface PlayerScreenUiEvent{
    data class LoadFolder(val folderId: Long) : PlayerScreenUiEvent

}
class PlayerScreenVm(
    private val repository: FolderRepository,
    @SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel() {
    private var exoPlayer: ExoPlayer? = null


    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }


    private val _uiState = MutableStateFlow(PlayerScreenUiState())
    val uiState: StateFlow<PlayerScreenUiState> = _uiState.asStateFlow()

    fun onEvent(event: PlayerScreenUiEvent) {
        when (event) {
            is PlayerScreenUiEvent.LoadFolder -> {
                loadFolder(event)
            }
        }
    }

    private fun loadFolder(loadFolder: PlayerScreenUiEvent.LoadFolder ) {
        viewModelScope.launch {
            try {
                val folder = repository.getSelectedFolder(loadFolder.folderId.toInt())
                _uiState.update {
                    it.copy(
                        selectedFolderToPlayUri = folder?.folderUri?.toUri(),
                        selectedFolderToPlay = getAllFiles(folder?.folderUri?.toUri()!!)
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

}



