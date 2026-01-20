package com.example.musicplayer.HomeScreen.Domain_layer.repostiory

import com.example.musicplayer.HomeScreen.Data_layer.local.Entity.FolderEntity
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun insertFolder(folder: FolderEntity)
    fun getSelectFolders(): Flow<List<FolderEntity>>
    fun clear(folder: FolderEntity)
}