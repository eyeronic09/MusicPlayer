package com.example.musicplayer.FolderScreen.Data_layer.local.DataSources

import com.example.musicplayer.FolderScreen.Data_layer.local.Entity.FolderEntity
import kotlinx.coroutines.flow.Flow

interface FolderLocalDataSource {
    suspend fun insertFolder(folder: FolderEntity)
    fun getSelectFolders() : Flow<List<FolderEntity>>
    suspend fun clear(folder: FolderEntity)

    suspend fun getSelectedFolder(folderId : Int) : FolderEntity?

}