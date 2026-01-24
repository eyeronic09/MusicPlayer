package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FolderScreenUiState(
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val isAddButtonClicked: Boolean = false,
    val isFolderClick: Boolean = false,
    val error: String = "",
    val userSelectedFolderToAdd : Uri? = null,
    val selectedUriToList: Uri? = null
    )

sealed interface FolderScreenUiEvent {
    data class AddFolder(val uri: Uri) : FolderScreenUiEvent
    data class SelectedUriToList(val uri: String) : FolderScreenUiEvent
    object NavigateBack : FolderScreenUiEvent
}

class FolderScreenVM(
    private val repository: FolderRepository,
    @SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel() {



    private val _screenUiState = MutableStateFlow(FolderScreenUiState())
    val screenUiState: StateFlow<FolderScreenUiState> = _screenUiState.asStateFlow()

    init {
        loadFolder()
    }

    fun onEvent(event: FolderScreenUiEvent) {
        when (event) {
            is FolderScreenUiEvent.AddFolder -> {
                addFolder(userSelectedFolder = event.uri)
            }
            is FolderScreenUiEvent.SelectedUriToList -> {
                _screenUiState.update {
                    it.copy(
                        isFolderClick = true,
                        selectedUriToList = event.uri.toUri()
                    )
                }
            }
            FolderScreenUiEvent.NavigateBack -> {
                _screenUiState.update {
                    it.copy(isFolderClick = false, selectedUriToList = null)
                }
            }
        }
    }

    fun loadFolder() {
        viewModelScope.launch {
            _screenUiState.update { it.copy(isLoading = true) }
            repository.getSelectFolders().collect { value ->
                _screenUiState.update {
                    it.copy(
                        folders = value,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun addFolder(userSelectedFolder: Uri) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val folder = Folder(
                id = 0,
                folderUri = userSelectedFolder.toString(),
                folderName = userSelectedFolder.lastPathSegment ?: "Unknown Folder"
            )
            repository.insertFolder(folder)
            _screenUiState.update {
                it.copy(
                    isAddButtonClicked = true,
                )
            }
        }
    }

    fun listFile(context : Context, uri : Uri  ) : List<Uri> {
        return try {
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            val documentFile = DocumentFile.fromTreeUri(context, uri)
            documentFile?.listFiles()?.mapNotNull { f ->
                if (f.exists()) f.uri else null
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
