package com.example.musicplayer.FolderScreen.Data_layer.local.Repository

import com.example.musicplayer.FolderScreen.Data_layer.local.DataSources.FolderLocalDataSource
import com.example.musicplayer.FolderScreen.Data_layer.local.mapper.toDomain
import com.example.musicplayer.FolderScreen.Data_layer.local.mapper.toEntity
import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FolderRepositoryImpl(private val source: FolderLocalDataSource) : FolderRepository {

    // covert to entity this is will passed to local data source and insert
    override suspend fun insertFolder(folder: Folder) {
        return source.insertFolder(folder.toEntity())
    }

    // covert to domain this is will passed to local data source and getSelectFolders only return entity
    // AND convert to Domain only one way
    override  fun getSelectFolders(): Flow<List<Folder>> {
        return source.getSelectFolders().map { folderEntities ->
            folderEntities.map { it.toDomain() }
        }
    }
    // covert to entity this is will passed to local data source and clear
    override suspend fun clear(folder: Folder) {
       return source.clear(folder.toEntity())
    }

}