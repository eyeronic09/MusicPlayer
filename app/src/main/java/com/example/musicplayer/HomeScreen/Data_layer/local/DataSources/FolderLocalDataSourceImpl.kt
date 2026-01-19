package com.example.musicplayer.HomeScreen.Data_layer.local.DataSources

import com.example.musicplayer.HomeScreen.Data_layer.local.Dao.FolderDao
import com.example.musicplayer.HomeScreen.Data_layer.local.Entity.FolderEntity

class FolderLocalDataSourceImpl(private val dao: FolderDao) : FolderLocalDataSource {
    override suspend fun InstertFolder(folder: FolderEntity) {
        return dao.InstertFolder(folder)

    }

    override suspend fun getSelectFolders(): List<FolderEntity> {
        return dao.getSelectFolders()
    }

    override suspend fun clear(folder: FolderEntity) {
       return dao.clear(folder)
    }
}

