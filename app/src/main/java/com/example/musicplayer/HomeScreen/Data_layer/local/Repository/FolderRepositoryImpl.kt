package com.example.musicplayer.HomeScreen.Data_layer.local.Repository

import com.example.musicplayer.HomeScreen.Data_layer.local.DataSources.FolderLocalDataSource
import com.example.musicplayer.HomeScreen.Data_layer.local.mapper.toDomain
import com.example.musicplayer.HomeScreen.Data_layer.local.mapper.toEntity
import com.example.musicplayer.HomeScreen.Domain_layer.model.Folder
import com.example.musicplayer.HomeScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FolderRepositoryImpl(private val source: FolderLocalDataSource) : FolderRepository {
    override suspend fun insertFolder(folder: Folder) {
        return source.insertFolder(folder.toEntity())
    }

    override suspend fun getSelectFolders(): Flow<List<Folder>> {
        return source.getSelectFolders().map { folderEntities ->
            folderEntities.map { it.toDomain() }
        }
    }

    override suspend fun clear(folder: Folder) {
       return source.clear(folder.toEntity())
    }

}