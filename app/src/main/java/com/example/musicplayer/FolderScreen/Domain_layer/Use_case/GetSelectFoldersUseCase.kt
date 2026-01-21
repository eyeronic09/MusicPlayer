package com.example.musicplayer.FolderScreen.Domain_layer.Use_case

import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.flow.Flow

class GetSelectFoldersUseCase(private val repository: FolderRepository) {
    fun getSelectFolders(): Flow<List<Folder>> {
        return repository.getSelectFolders()
    }
}