package com.example.musicplayer.HomeScreen.data.local.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import com.example.musicplayer.HomeScreen.data.local.dao.SongDao
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity

@Database(
    entities = [SongEntity::class],
    version = 1 ,
    exportSchema = true

)
abstract class SONG_DB : RoomDatabase() {
    abstract fun dao(): SongDao
}