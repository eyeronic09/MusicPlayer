package com.example.musicplayer.FolderScreen.Data_layer.local.DataSources

import com.example.musicplayer.FolderScreen.Data_layer.local.Dao.FolderDao
import com.example.musicplayer.FolderScreen.Data_layer.local.Entity.FolderEntity
import kotlinx.coroutines.flow.Flow

class FolderLocalDataSourceImpl(private val dao: FolderDao) : FolderLocalDataSource {
    override suspend fun insertFolder(folder: FolderEntity) {
        return dao.insertFolder(folder)
    }

    override fun getSelectFolders(): Flow<List<FolderEntity>> {
        return dao.getAllFolders()
    }

    override suspend fun clear(folder: FolderEntity) {
       return dao.clear(folder)
    }
}

