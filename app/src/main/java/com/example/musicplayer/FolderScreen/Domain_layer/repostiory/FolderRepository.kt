package com.example.musicplayer.FolderScreen.Domain_layer.repostiory

import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    suspend fun insertFolder(folder: Folder)
    fun getSelectFolders(): Flow<List<Folder>>
    suspend fun clear(folder: Folder)
}