package com.example.musicplayer.HomeScreen.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlayListEntity
import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlaylistSongCrossRef
import com.example.musicplayer.HomeScreen.data.local.converter.Converters
import com.example.musicplayer.HomeScreen.Playlist.data.local.dao.PlaylistDao
import com.example.musicplayer.HomeScreen.data.local.dao.SongDao
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity

@Database(
    entities = [SongEntity::class , PlayListEntity::class , PlaylistSongCrossRef::class],
    version = 1 ,
    exportSchema = true

)
@TypeConverters(Converters::class)
abstract class SONG_DB : RoomDatabase() {
    abstract fun dao(): SongDao
    abstract fun playlistDao(): PlaylistDao
}