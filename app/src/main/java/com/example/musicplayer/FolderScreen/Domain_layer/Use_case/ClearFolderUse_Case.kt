package com.example.musicplayer.FolderScreen.Domain_layer.Use_case

import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository

class ClearFolderUseCase(private val repository : FolderRepository) {
    suspend fun clear(folder : Folder) {
        return repository.clear(folder)
    }
}

