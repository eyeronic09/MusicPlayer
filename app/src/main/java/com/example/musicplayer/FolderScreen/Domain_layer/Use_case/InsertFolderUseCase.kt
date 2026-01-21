package com.example.musicplayer.FolderScreen.Domain_layer.Use_case

import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository

class InsertFolderUseCase(private val repository : FolderRepository) {
    suspend fun insertFolder(folder: Folder) {
        return repository.insertFolder(folder)
    }
}