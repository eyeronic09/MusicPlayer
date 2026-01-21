package com.example.musicplayer.FolderScreen.Data_layer.local.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicplayer.FolderScreen.Data_layer.local.Converter.Converter
import com.example.musicplayer.FolderScreen.Data_layer.local.Dao.FolderDao
import com.example.musicplayer.FolderScreen.Data_layer.local.Entity.FolderEntity

@TypeConverters(Converter::class)
@Database(
    entities = [FolderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Folder_DB() : RoomDatabase() {
    abstract val dao : FolderDao
}