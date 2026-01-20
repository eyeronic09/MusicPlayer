package com.example.musicplayer.HomeScreen.Domain_layer.repostiory

import com.example.musicplayer.HomeScreen.Domain_layer.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    suspend fun insertFolder(folder: Folder)
    suspend fun getSelectFolders(): Flow<List<Folder>>
    suspend fun clear(folder: Folder)
}