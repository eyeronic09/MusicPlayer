package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FolderScreenUiState(
    val folders : List<Folder> = emptyList()  ,
    val isLoading : Boolean = false,
    val error : String = ""
)
sealed interface FolderScreenUiEvent{

    data class addFolder(val AddFolderuri : Uri) : FolderScreenUiEvent

    data object addFolderButton : FolderScreenUiEvent

}
class FolderScreenVM (
    private val repository: FolderRepository
) : ViewModel() {
    private val _screenUiState = MutableStateFlow(FolderScreenUiState())
    val screenUiState : StateFlow<FolderScreenUiState> = _screenUiState.asStateFlow()

    fun onEvent(event: FolderScreenUiEvent){
        when(event){
            is FolderScreenUiEvent.addFolder -> {
               loadFolder()
            }
            is FolderScreenUiEvent.addFolderButton -> TODO()
            else -> {
                _screenUiState.update { it ->
                    it.copy(error = "error")
                }
            }
        }
    }

    fun loadFolder(){
        viewModelScope.launch {
            _screenUiState.update{ it.copy(isLoading = true) }
            repository.getSelectFolders().collect { value ->
                _screenUiState.update { it.copy(
                    folders = value,
                    )
                }
            }
        }
    }




}