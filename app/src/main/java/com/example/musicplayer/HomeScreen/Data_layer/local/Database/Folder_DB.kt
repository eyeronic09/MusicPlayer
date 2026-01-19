package com.example.musicplayer.HomeScreen.Data_layer.local.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicplayer.HomeScreen.Data_layer.local.Dao.FolderDao
import com.example.musicplayer.HomeScreen.Data_layer.local.Entity.FolderEntity

@Database(
    entities = [FolderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Folder_DB() : RoomDatabase() {
    abstract val dao : FolderDao
}