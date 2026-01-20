package com.example.musicplayer.HomeScreen.Data_layer.local.DataSources

import com.example.musicplayer.HomeScreen.Data_layer.local.Entity.FolderEntity
import kotlinx.coroutines.flow.Flow

interface FolderLocalDataSource {
    suspend fun insertFolder(folder: FolderEntity)
    suspend fun getSelectFolders() : Flow<List<FolderEntity>>
    suspend fun clear(folder: FolderEntity)
}