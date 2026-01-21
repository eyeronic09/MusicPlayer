package com.example.musicplayer.FolderScreen.Data_layer.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicplayer.FolderScreen.Data_layer.local.Entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Insert
    suspend fun insertFolder(folder: FolderEntity)

    @Query("SELECT * FROM Audio_File ")
    fun getSelectFolders(): Flow<List<FolderEntity>>

    @Delete
    suspend fun clear(folder: FolderEntity)

}