package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FolderScreenUiState(
    val folders : List<Folder> = emptyList(),
    val isLoading : Boolean = false,
    val isAddButtonClicked : Boolean = false,
    val error : String = ""
)
sealed interface FolderScreenUiEvent {
    data object addFolderButton : FolderScreenUiEvent
    data class AddFolder(val uri: Uri) : FolderScreenUiEvent
}
class FolderScreenVM (
    private val repository: FolderRepository
) : ViewModel() {


    private val _screenUiState = MutableStateFlow(FolderScreenUiState())
    val screenUiState : StateFlow<FolderScreenUiState> = _screenUiState.asStateFlow()

    init {
        loadFolder()
    }

    fun onEvent(event: FolderScreenUiEvent){
        when(event){

            is FolderScreenUiEvent.AddFolder -> {
                addFolder(userSelectedFolder = event.uri)
            }
            is FolderScreenUiEvent.addFolderButton -> {

            }
        }
    }

    fun loadFolder(){
        viewModelScope.launch {
            _screenUiState.update{ it.copy(isLoading = true) }
            repository.getSelectFolders().collect { value ->
                _screenUiState.update { it.copy(
                    folders = value,
                    isLoading = false,
                    )
                }
            }
        }
    }

    fun addFolder(userSelectedFolder : Uri){
        viewModelScope.launch(context = Dispatchers.IO){
            val folder = Folder(
                id = 0 ,
                folderUri = userSelectedFolder.toString(),
                folderName = userSelectedFolder.lastPathSegment ?: "Unknow Folder"
            )
            repository.insertFolder(folder)
            _screenUiState.update {it -> it.copy(
                isAddButtonClicked = true,
            )
        }
        }
    }


}