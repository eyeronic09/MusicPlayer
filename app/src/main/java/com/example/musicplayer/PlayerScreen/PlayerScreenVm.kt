package com.example.musicplayer.PlayerScreen

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface PlaybackSource {

    data class Folder(
        val folderUri: Uri,
        val files: List<Uri>
    ) : PlaybackSource

    data class SingleAudio(
        val uri: Uri
    ) : PlaybackSource

    object None : PlaybackSource
}


data class PlayerScreenUiState(
    val playbackSource: PlaybackSource = PlaybackSource.None,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface PlayerScreenUiEvent{
    data class LoadFolder(val folderId: Long) : PlayerScreenUiEvent
    data class UserSpecificAudio(val uri: Uri) : PlayerScreenUiEvent
    object ClearPlayBack : PlayerScreenUiEvent
}

class PlayerScreenVm(
    private val repository: FolderRepository,
    application: Application
) : AndroidViewModel(application) {


    private val _uiState = MutableStateFlow(PlayerScreenUiState())
    val uiState: StateFlow<PlayerScreenUiState> = _uiState.asStateFlow()

    fun onEvent(event: PlayerScreenUiEvent) {
        when (event) {
            is PlayerScreenUiEvent.LoadFolder -> {
                loadFolder(event.folderId)
            }
            is PlayerScreenUiEvent.UserSpecificAudio -> {
                playSingleAudio(event.uri)
            }
            is PlayerScreenUiEvent.ClearPlayBack -> {
                clearPlayBack()
            }
        }
    }

    private fun loadFolder(folderId: Long ) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
           runCatching {
               val folder = repository.getSelectedFolder(folderId.toInt()) ?: error ("Folder not found")
               val folderUri = folder.folderUri.toUri()
               val files = scanFolderForAudio(folderUri)
               PlaybackSource.Folder(folderUri, files)

           }.onSuccess { source ->
               _uiState.update { state -> 
                   state.copy(
                       isLoading = false,
                       error = null,
                       playbackSource = source
                   )
               }

           }.onFailure { throwable ->
               _uiState.update { state -> 
                   state.copy(
                       isLoading = false, 
                       error = throwable.message ?: "Unknown error"
                   ) 
               }
           }
        }
    }

    fun scanFolderForAudio(folderUri: Uri) : List<Uri> {
        return try {
            val folderDoc = DocumentFile.fromTreeUri(getApplication(), folderUri)
            folderDoc?.listFiles()
                ?.filter { it.isFile }
                ?.map { it.uri }
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun playSingleAudio(uri: Uri) {
        _uiState.update {
            it.copy(
                playbackSource = PlaybackSource.SingleAudio(uri),
                error = null
            )
        }
    }

    private fun clearPlayBack() {
        _uiState.update {
            it.copy(
                playbackSource = PlaybackSource.None,
                error = null
            )
        }
    }
}
